package SWEN656.tictactoe.agtictactoe;

public class UpdateMessage {
    private String type;
    private String[][] board;
    private boolean gameOver;
    private String winner;

    public UpdateMessage(String[][] board, boolean gameOver, String winner) {
        this.type = "update";
        this.board = board;
        this.gameOver = gameOver;
        this.winner = winner;
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String[][] getBoard() {
		return board;
	}

	public void setBoard(String[][] board) {
		this.board = board;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}
    
    
    
}
