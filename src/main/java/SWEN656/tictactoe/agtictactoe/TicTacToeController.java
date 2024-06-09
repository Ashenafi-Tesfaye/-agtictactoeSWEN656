package SWEN656.tictactoe.agtictactoe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tictactoe")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TicTacToeController {

    @Autowired
    private Board board;

    @Autowired
    private Player player1;

    @Autowired
    private Player player2;

    @Autowired
    private Referee referee;
    

    @PostMapping("/start")
    public String startGame() {
        board.initialize();
        return "Game started!";
    }

    @PostMapping("/move")
    public String makeMove(@RequestParam int player, @RequestParam int row, @RequestParam int col) {
        Player currentPlayer = (player == 1) ? player1 : player2;
        if (board.isValidMove(row, col)) {
            currentPlayer.move(board, row, col);
            if (referee.isGameOver(board)) {
                Player winner = referee.getWinner(board, player1, player2);
                if (winner != null) {
                    return "Player " + winner.getName() + " wins!";
                } else if (board.isBoardFull()) {
                    return "It's a tie!";
                }
            }
            return "Move accepted!";
        } else {
            return "Invalid move!";
        }
    }

    @GetMapping("/board")
    public String displayBoard() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            sb.append("-------------\n| ");
            for (int j = 0; j < 3; j++) {
                sb.append(board.grid[i][j]).append(" | ");
            }
            sb.append("\n");
        }
        sb.append("-------------");
        return sb.toString();
    }
}
