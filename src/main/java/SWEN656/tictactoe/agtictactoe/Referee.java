package SWEN656.tictactoe.agtictactoe;

import org.springframework.stereotype.Component;

@Component
public class Referee {

    public boolean isGameOver(TicTacToeGame game, Player player1, Player player2) {
        if (game == null || player1 == null || player2 == null) {
            throw new IllegalArgumentException("Invalid parameters: game or players cannot be null.");
        }
    
        return game.isBoardFull() || hasWinner(game.getBoard(), player1.getMark()) || hasWinner(game.getBoard(), player2.getMark());
    }

    public boolean hasWinner(char[][] board, char mark) {
        for (int i = 0; i < 3; i++) {
            if ((board[i][0] == mark && board[i][1] == mark && board[i][2] == mark) ||
                (board[0][i] == mark && board[1][i] == mark && board[2][i] == mark)) {
                return true;
            }
        }
        if ((board[0][0] == mark && board[1][1] == mark && board[2][2] == mark) ||
            (board[0][2] == mark && board[1][1] == mark && board[2][0] == mark)) {
            return true;
        }
        return false;
    }

    public Player getWinner(TicTacToeGame game, Player player1, Player player2) {
        if (hasWinner(game.getBoard(), player1.getMark())) {
            return player1;
        } else if (hasWinner(game.getBoard(), player2.getMark())) {
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
