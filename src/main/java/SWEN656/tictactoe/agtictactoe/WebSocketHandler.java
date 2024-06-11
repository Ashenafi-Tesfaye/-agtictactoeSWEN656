package SWEN656.tictactoe.agtictactoe;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final GameSessionManager gameSessionManager;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, String> currentPlayerMarks = new ConcurrentHashMap<>();

    public WebSocketHandler(GameSessionManager gameSessionManager) {
        this.gameSessionManager = gameSessionManager;
    }

    
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        Map<String, String> payload = objectMapper.readValue(message.getPayload(), Map.class);
        String type = payload.get("type");
        String gameId = payload.get("gameId");

        if ("join".equals(type)) {
            boolean joined = gameSessionManager.joinGame(gameId, session);
            if (joined) {
                currentPlayerMarks.putIfAbsent(session.getId(), "X"); // Set the current player mark to "X" when a game is joined
                broadcastGameState(gameId, currentPlayerMarks.get(session.getId()));
            }
        } else if ("move".equals(type)) {
            String playerMark = currentPlayerMarks.get(session.getId());
            if (playerMark == null) {
                // Player's mark not assigned, handle appropriately
                return;
            }

            int row = Integer.parseInt(payload.get("row"));
            int col = Integer.parseInt(payload.get("col"));
            TicTacToeGame game = gameSessionManager.getGame(gameId);

            if (game != null) {
                boolean moveMade = game.makeMove(row, col);
                if (moveMade) {
                    currentPlayerMarks.put(session.getId(), playerMark.equals("X") ? "O" : "X");
                    broadcastGameState(gameId, currentPlayerMarks.get(session.getId()));
                }
            }
        }
    }

    private void broadcastGameState(String gameId, String currentPlayerMark) throws IOException {
        TicTacToeGame game = gameSessionManager.getGame(gameId);
        if (game != null) {
            Map<String, Object> gameState = new HashMap<>();
            gameState.put("type", "update");
            gameState.put("board", game.getBoard().getGrid());
            gameState.put("gameOver", game.isGameOver());
            gameState.put("currentPlayerMark", currentPlayerMark);
            Player winner = game.getWinner();
            gameState.put("winner", winner != null ? winner.getMark() : null);

            String gameStateJson = objectMapper.writeValueAsString(gameState);
            gameSessionManager.broadcastMessage(gameId, gameStateJson);
        }
    }
}
