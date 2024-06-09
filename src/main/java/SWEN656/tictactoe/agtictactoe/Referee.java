package SWEN656.tictactoe.agtictactoe;

import org.springframework.stereotype.Component;

@Component
public class Referee {
	public boolean isGameOver(Board board) {
        return board.isBoardFull() || hasWinner(board, 'X') || hasWinner(board, 'O');
    }

    public boolean hasWinner(Board board, char mark) {
        // Check rows, columns, and diagonals for a win
        for (int i = 0; i < 3; i++) {
            if ((board.grid[i][0] == mark && board.grid[i][1] == mark && board.grid[i][2] == mark) || 
                (board.grid[0][i] == mark && board.grid[1][i] == mark && board.grid[2][i] == mark)) {
                return true;
            }
        }
        if ((board.grid[0][0] == mark && board.grid[1][1] == mark && board.grid[2][2] == mark) || 
            (board.grid[0][2] == mark && board.grid[1][1] == mark && board.grid[2][0] == mark)) {
            return true;
        }
        return false;
    }

    public Player getWinner(Board board, Player player1, Player player2) {
        if (hasWinner(board, player1.getMark())) {
            return player1;
        } else if (hasWinner(board, player2.getMark())) {
            return player2;
        }
        return null;
    }

    public void announceWinner(Player winner) {
        if (winner != null) {
            System.out.println("Game Over: Player " + winner.getName() + " wins!");
        } else {
            System.out.println("Game Over: It's a tie!");
        }
    }
}
