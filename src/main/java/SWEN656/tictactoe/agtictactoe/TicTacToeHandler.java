package SWEN656.tictactoe.agtictactoe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class TicTacToeHandler extends TextWebSocketHandler {

    private final GameSessionManager gameSessionManager;

    @Autowired
    public TicTacToeHandler(GameSessionManager gameSessionManager) {
        this.gameSessionManager = gameSessionManager;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode jsonNode = new ObjectMapper().readTree(message.getPayload());
        String type = jsonNode.get("type").asText();

        if ("join".equals(type)) {
            handleJoinMessage(session, jsonNode);
        } else if ("move".equals(type)) {
            handleMoveMessage(session, jsonNode);
        }
    }
    private void handleJoinMessage(WebSocketSession session, JsonNode jsonNode) throws IOException {
        String gameId = jsonNode.get("gameId").asText();
        boolean isJoined = gameSessionManager.joinGame(gameId, session); 
        if (!isJoined) {
            
            String errorMessage = "{\"type\":\"join\",\"status\":\"error\",\"message\":\"Failed to join the game.\"}";
            session.sendMessage(new TextMessage(errorMessage));
            return;
        }
    
        TicTacToeGame game = gameSessionManager.getGame(gameId);
        if (game == null) {
            String errorMessage = "{\"type\":\"join\",\"status\":\"error\",\"message\":\"Game not found.\"}";
            session.sendMessage(new TextMessage(errorMessage));
            return;
        }
    
        char playerMark = game.getPlayerMark(session.getId());
       
    
        // Send initial board state to the joining player
        ObjectMapper mapper = new ObjectMapper();
        String initialBoardMessage = mapper.writeValueAsString(Map.of("type", "join", "playerMark", playerMark, "status", "success", "board", game.getBoard()));
        session.sendMessage(new TextMessage(initialBoardMessage));
    }
    

    private void handleMoveMessage(WebSocketSession session, JsonNode jsonNode) throws IOException {
        String gameId = jsonNode.get("gameId").asText();
        char playerMark = jsonNode.get("playerMark").asText().charAt(0);
        int row = jsonNode.get("row").asInt();
        int col = jsonNode.get("col").asInt();

        TicTacToeGame game = gameSessionManager.getGame(gameId);
        if (game == null) {
            return;
        }

        boolean validMove = game.makeMove(row, col, playerMark);
        if (validMove) {
            ObjectMapper mapper = new ObjectMapper();
            String boardUpdateMessage = mapper.writeValueAsString(Map.of("type", "update", "board", game.getBoard()));
            List<WebSocketSession> gameSessions = gameSessionManager.getSessions(gameId);
            for (WebSocketSession s : gameSessions) {
                s.sendMessage(new TextMessage(boardUpdateMessage));
            }

            Player winner = game.getWinner();
            if (winner != null) {
                String gameOverMessage = mapper.writeValueAsString(Map.of("type", "gameOver", "winner", winner.getMark()));
                for (WebSocketSession s : gameSessions) {
                    s.sendMessage(new TextMessage(gameOverMessage));
                }
            } else if (game.isBoardFull()) {
                String gameOverMessage = mapper.writeValueAsString(Map.of("type", "gameOver"));
                for (WebSocketSession s : gameSessions) {
                    s.sendMessage(new TextMessage(gameOverMessage));
                }
                
                // Reset game after notifying clients
                game.resetGame();
            }
        } else {
            session.sendMessage(new TextMessage("{\"type\":\"error\",\"error\":\"Invalid move\"}"));
        }
    }
}