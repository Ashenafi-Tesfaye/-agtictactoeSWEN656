package SWEN656.tictactoe.agtictactoe;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class TicTacToeHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new HashMap<>();
    private final GameSessionManager gameSessionManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TicTacToeHandler(GameSessionManager gameSessionManager) {
        this.gameSessionManager = gameSessionManager;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, String> payload = objectMapper.readValue(message.getPayload(), Map.class);
        String type = payload.get("type");

        if ("join".equals(type)) {
            String gameId = payload.get("gameId");
            sessions.put(gameId, session);
            broadcastGameState(gameId);
        } else if ("move".equals(type)) {
            String gameId = payload.get("gameId");
            String playerMark = payload.get("playerMark");
            int row = Integer.parseInt(payload.get("row"));
            int col = Integer.parseInt(payload.get("col"));

            TicTacToeGame game = gameSessionManager.getGame(gameId);
            if (game != null) {
                game.makeMove(row, col);
                broadcastGameState(gameId);
            }
        }
    }

    private void broadcastGameState(String gameId) throws Exception {
        TicTacToeGame game = gameSessionManager.getGame(gameId);
        if (game != null) {
            Map<String, Object> gameState = new HashMap<>();
            gameState.put("type", "update");
            gameState.put("board", game.getBoard().getGrid());
            gameState.put("gameOver", game.isGameOver());
            Player winner = game.getWinner();
            if (winner != null) {
                gameState.put("winner", winner.getMark());
            } else {
                gameState.put("winner", null);
            }

            String gameStateJson = objectMapper.writeValueAsString(gameState);
            TextMessage gameStateMessage = new TextMessage(gameStateJson);

            WebSocketSession session = sessions.get(gameId);
            if (session != null && session.isOpen()) {
                session.sendMessage(gameStateMessage);
            }
        }
    }
}