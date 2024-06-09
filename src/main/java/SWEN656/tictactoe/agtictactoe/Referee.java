package SWEN656.tictactoe.agtictactoe;

public class Referee {
    private Board board;

    public Referee(Board board) {
        this.board = board;
    }

    public boolean isGameOver() {
        return hasWinner('X') || board.isBoardFull();
    }

    public boolean hasWinner(char mark) {
        return (board.grid[0][0] == mark && board.grid[0][1] == mark && board.grid[0][2] == mark) ||
               (board.grid[1][0] == mark && board.grid[1][1] == mark && board.grid[1][2] == mark) ||
               (board.grid[2][0] == mark && board.grid[2][1] == mark && board.grid[2][2] == mark) ||
               (board.grid[0][0] == mark && board.grid[1][0] == mark && board.grid[2][0] == mark) ||
               (board.grid[0][1] == mark && board.grid[1][1] == mark && board.grid[2][1] == mark) ||
               (board.grid[0][2] == mark && board.grid[1][2] == mark && board.grid[2][2] == mark) ||
               (board.grid[0][0] == mark && board.grid[1][1] == mark && board.grid[2][2] == mark) ||
               (board.grid[0][2] == mark && board.grid[1][1] == mark && board.grid[2][0] == mark);
    }

    public void announceWinner(Player player1, Player player2) {
        if (hasWinner('X')) {
            System.out.println(player1.getName() + " wins!");
        } else if (hasWinner('O')) {
            System.out.println(player2.getName() + " wins!");
        } else {
            System.out.println("It's a tie!");
        }
    }
}
