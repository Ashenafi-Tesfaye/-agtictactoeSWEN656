package SWEN656.tictactoe.agtictactoe;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;


@Component
public class TicTacToeGame {
    private char[][] board;
    private String gameId;
    private Map<String, Player> players;
    private int currentPlayerIndex;
    private Referee referee;

    private static final Logger logger = LoggerFactory.getLogger(TicTacToeGame.class);


    public TicTacToeGame(Referee referee) {
        this.referee = referee;
        board = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '-';
            }
        }
        players = new HashMap<>();
        currentPlayerIndex = 0;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }

    public char[][] getBoard() {
        return board;
    }

    public synchronized boolean makeMove(int row, int col, char mark) {
        if (row >= 0 && row < 3 && col >= 0 && col < 3 && board[row][col] == '-') {
            board[row][col] = mark;
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

            logger.info("Player {} made a move at [{}, {}]", mark, row, col); 

            return true;
        }
        return false;
    }

    public boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    return false;
                }
            }
        }
        return true;
    }

    public Player getCurrentPlayer() {
        return players.values().toArray(new Player[0])[currentPlayerIndex];
    }

    public void addPlayer(String playerId, String playerName, char mark) {
        Player player = new Player();
        player.setName(playerName);
        player.setMark(mark);
        players.put(playerId, player);
    }

    public Player getWinner() {
        for (Player player : players.values()) {
            if (referee.hasWinner(board, player.getMark())) {
                return player;
            }
        }
        return null;
    }

    public boolean isGameOver() {
        if (players.size() < 2) {
            return false;
        }

        Player[] playersArray = players.values().toArray(new Player[0]);
        return referee.isGameOver(this, playersArray[0], playersArray[1]);
    }
    

    public void resetGame() {
        // Reset the board
        board = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '-';
            }
        }
        // Clear players
        players.clear();
        // Reset current player index
        currentPlayerIndex = 0;
    }
    

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sb.append(board[i][j]);
                if (j < 2) sb.append("|");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public int getNumberOfPlayers() {
        return players.size();
    }
    
	public Map<String, Player> getPlayers() {
		return players;
	}

	public char getPlayerMark(String sessionId) {
        Player player = players.get(sessionId);
        if (player != null) {
            return player.getMark();
        }
        return '-';
    }
}
