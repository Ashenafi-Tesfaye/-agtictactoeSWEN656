package SWEN656.tictactoe.agtictactoe;

public class Board {
    protected char[][] grid = new char[3][3];

    public void initialize() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                grid[i][j] = '-';
            }
        }
    }

    public void display() {
        System.out.println("-------------");
        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++) {
                System.out.print(grid[i][j] + " | ");
            }
            System.out.println();
            System.out.println("-------------");
        }
    }

    public boolean isBoardFull() {
        boolean isFull = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[i][j] == ' ') {
                    isFull = false;
                }
            }
        }
        return isFull;
    }

    public boolean isValidMove(int row, int col) {
        if (row >= 0 && row < 3 && col >= 0 && col < 3 && grid[row][col] == '-') {
            return true;
        }
        return false;
    }

    public void placeMark(int row, int col, char mark) {
        grid[row][col] = mark;
    }
}
