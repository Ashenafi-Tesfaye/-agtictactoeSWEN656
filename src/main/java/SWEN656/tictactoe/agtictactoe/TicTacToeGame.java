package SWEN656.tictactoe.agtictactoe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TicTacToeGame {
    private final Board board;
    private final Player player1;
    private final Player player2;
    private Player currentPlayer;
    private final Referee referee;
    private String[][] grid; // Step 1: Grid state representation


    @Autowired
    public TicTacToeGame(Referee referee) {
        this.referee = referee;
        this.grid = new String[3][3]; // Step 2: Initialize grid state
        this.board = new Board();
        this.player1 = new Player("Player 1", 'X');
        this.player2 = new Player("Player 2", 'O');
        this.currentPlayer = player1;
        board.initialize();
    }

    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

   // Modify the makeMove method to include grid update
public boolean makeMove(int row, int col) {
    if (board.isValidMove(row, col)) {
        board.placeMark(row, col, currentPlayer.getMark());
        updateGrid(row, col); // Update the grid with the current player's mark
        if (referee.isGameOver(board)) {
            return true;
        }
        switchPlayer();
        return false;
    }
    return false;
}

    // Updates the grid after a move is made
private void updateGrid(int row, int col) {
    grid[row][col] = String.valueOf(currentPlayer.getMark());
}

// Resets the game to its initial state
public void resetGame() {
    this.grid = new String[3][3]; // Reinitialize the grid
    this.board.initialize(); // Reset the board
    this.currentPlayer = player1; // Reset the current player to player1
}

// Displays the current state of the grid
public void displayGrid() {
    for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
            if (grid[i][j] == null) {
                System.out.print("- ");
            } else {
                System.out.print(grid[i][j] + " ");
            }
        }
        System.out.println();
    }
}

    private void switchPlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }

    public boolean isGameOver() {
        return referee.isGameOver(board);
    }

    

    public Player getWinner() {
        if (referee.hasWinner(board)) {
            return currentPlayer;
        }
        return null;
    }
}