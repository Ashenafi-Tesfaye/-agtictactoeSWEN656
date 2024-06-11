package SWEN656.tictactoe.agtictactoe;

import org.springframework.stereotype.Component;

@Component
public class Player {
    private String gameId;
    private String name;
    private char mark;

    public Player() {
    }
   
    public Player(String name, char mark) {
        this.name = name;
        this.mark = mark;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMark(char mark) {
        this.mark = mark;
    }

    public String getName() {
        return name;
    }

    public char getMark() {
        return mark;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }

    public boolean move(Board board, int row, int col) {
        if (board.isValidMove(row, col)) {
            board.placeMark(row, col, this.mark);
            System.out.println("Move made at row " + row + ", col " + col + " by player " + this.mark);
            return true;
        } else {
            System.out.println("Invalid move at row " + row + ", col " + col + " by player " + this.mark);
            return false;
        }
    }
}