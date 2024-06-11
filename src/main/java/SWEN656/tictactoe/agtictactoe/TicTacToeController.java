package SWEN656.tictactoe.agtictactoe;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tictactoe")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TicTacToeController {
    private final GameSessionManager gameSessionManager;

    @Autowired
    public TicTacToeController(GameSessionManager gameSessionManager) {
        this.gameSessionManager = gameSessionManager;
    }

    @GetMapping("/start")
    public ResponseEntity<Map<String, String>> startGame() {
        String gameId = gameSessionManager.createGame();
        String playerMark = "X"; // Assuming player 1 is always X

        Map<String, String> response = new HashMap<>();
        response.put("gameId", gameId);
        response.put("playerMark", playerMark);
        response.put("joinUrl", "http://localhost:8080/index.html?gameId=" + gameId + "&playerMark=" + playerMark);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/move")
    public String makeMove(@RequestParam String gameId, @RequestParam String playerMark, @RequestParam int row, @RequestParam int col) {
        TicTacToeGame game = gameSessionManager.getGame(gameId);
        if (game == null) {
            return "Game not found!";
        }

        Player currentPlayer = game.getCurrentPlayer();
        if (playerMark.equals(String.valueOf(currentPlayer.getMark()))) {
            boolean gameOver = game.makeMove(row, col);
            if (gameOver) {
                Player winner = game.getWinner();
                if (winner != null) {
                    return "Player " + winner.getName() + " wins!";
                } else {
                    return "It's a tie!";
                }
            }
            return "Move accepted!";
        } else {
            return "It's not your turn!";
        }
    }
}