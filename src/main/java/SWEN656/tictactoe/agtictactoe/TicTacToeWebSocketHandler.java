package SWEN656.tictactoe.agtictactoe;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class TicTacToeWebSocketHandler extends TextWebSocketHandler {
    private final GameSessionManager gameSessionManager;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public TicTacToeWebSocketHandler(GameSessionManager gameSessionManager) {
        this.gameSessionManager = gameSessionManager;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        Map<String, String> payload = objectMapper.readValue(message.getPayload(), Map.class);
        String type = payload.get("type");
        String gameId = payload.get("gameId");

        if ("join".equals(type)) {
            sessions.put(gameId, session);
            broadcastGameState(gameId);
        } else if ("move".equals(type)) {
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

    private void broadcastGameState(String gameId) throws IOException {
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