package SWEN656.tictactoe.agtictactoe;

public class Player {
    private String name;
    private char mark;

    public Player(String name, char mark) {
        this.name = name;
        this.mark = mark;
    }

    public String getName() {
        return name;
    }

    public char getMark() {
        return mark;
    }
    
	public void move(Board board, int row, int col) {
		if (board.isValidMove(row, col)) {
            board.placeMark(row, col, this.mark);
        }
	}
}
