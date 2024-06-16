package SWEN656.tictactoe.agtictactoe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GameSessionManager {
    private final Map<String, TicTacToeGame> games = new HashMap<>();
    private final Map<String, List<WebSocketSession>> sessions = new HashMap<>();
    private final Referee referee;

    @Autowired
    public GameSessionManager(Referee referee) {
        this.referee = referee;
    }

    public Map<String, String> createGame() {
        String gameId = "game-" + (games.size() + 1);
        TicTacToeGame game = new TicTacToeGame(referee);
        game.setGameId(gameId);

        games.put(gameId, game);
        
        sessions.put(gameId, new ArrayList<>()); // Initialize sessions for the new game


        Map<String, String> response = new HashMap<>();
        response.put("gameId", gameId);

        return response;
    }

    public boolean joinGame(String gameId, WebSocketSession playerSession) {
        TicTacToeGame game = games.get(gameId);
        if (game == null) {
            return false;
        }

        List<WebSocketSession> gameSessions = sessions.computeIfAbsent(gameId, k -> new ArrayList<>());

        synchronized (gameSessions) {
            // Check if player with the same session ID has already joined
            if (gameSessions.contains(playerSession)) {
                // Player has already joined, check player marks
                return true;
            } else if (gameSessions.size() >= 2) {
                return false; // Game is already full
            }

            char playerMark = assignPlayerMark(game);
            gameSessions.add(playerSession);
            game.addPlayer(playerSession.getId(), "Player", playerMark); // Add player to game with assigned marker

            // Send marker assignment to the player
            sendPlayerMark(playerSession, playerMark);
         

            return true;
        }
    }
    
    private char assignPlayerMark(TicTacToeGame game) {
        // Determine player mark based on current game state
        if (game.getNumberOfPlayers() == 0) {
            return 'X'; // First player gets 'X'
        } else if (game.getNumberOfPlayers() == 1) {
            return 'O'; // Second player gets 'O'
        } else {
            throw new IllegalStateException("More than two players joined the game unexpectedly.");
        }
    }
    
    private void sendPlayerMark(WebSocketSession session, char playerMark) {
        try {
            session.sendMessage(new TextMessage("{\"type\":\"join\",\"status\":\"success\",\"playerMark\":\"" + playerMark + "\"}"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TicTacToeGame getGame(String gameId) {
        return games.get(gameId);
    }

    public List<WebSocketSession> getSessions(String gameId) {
        return sessions.get(gameId);
    }

    public void broadcastMessage(String gameId, String message) {
        List<WebSocketSession> gameSessions = getSessions(gameId);
        if (gameSessions != null) {
            for (WebSocketSession session : gameSessions) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
