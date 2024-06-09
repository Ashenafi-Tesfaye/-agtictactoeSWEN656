package SWEN656.tictactoe.agtictactoe;

import org.springframework.stereotype.Component;


@Component
public class Player {
    private String name;
    private char mark;

    public Player() {
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

    public void move(Board board, int row, int col) {
        if (board.isValidMove(row, col)) {
            board.placeMark(row, col, this.mark);
        }
    }
}
