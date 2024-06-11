package SWEN656.tictactoe.agtictactoe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class GameSessionManager {
    private final Map<String, TicTacToeGame> games = new HashMap<>();
    private final Map<String, List<WebSocketSession>> sessions = new HashMap<>();
    private final Referee referee;

    public GameSessionManager(Referee referee) {
        this.referee = referee;
    }

    public String createGame() {
        String gameId = "game-" + (games.size() + 1);
        TicTacToeGame game = new TicTacToeGame(referee);
        games.put(gameId, game);
        return gameId;
    }

    public boolean joinGame(String gameId, WebSocketSession playerSession) {
        TicTacToeGame game = games.get(gameId);
        if (game == null) {
            return false;
        }
        List<WebSocketSession> gameSessions = sessions.computeIfAbsent(gameId, k -> new ArrayList<>());
        gameSessions.removeIf(session -> !session.isOpen());
        if (gameSessions.size() >= 2) {
            return false;
        } else {
            gameSessions.add(playerSession);
            return true;
        }
    }

    public List<WebSocketSession> getSessions(String gameId) {
        return sessions.get(gameId);
    }

    public TicTacToeGame getGame(String gameId) {
        return games.get(gameId);
    }

    public void removeGame(String gameId) {
        games.remove(gameId);
        sessions.remove(gameId);
    }

    public void onSessionClose(WebSocketSession session) {
        sessions.values().forEach(sessionList -> sessionList.remove(session));
    }

    public void sendMessage(WebSocketSession session, String message) {
        if (session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastMessage(String gameId, String message) {
        List<WebSocketSession> gameSessions = sessions.get(gameId);
        if (gameSessions != null) {
            for (WebSocketSession session : gameSessions) {
                sendMessage(session, message);
            }
        }
    }
}
