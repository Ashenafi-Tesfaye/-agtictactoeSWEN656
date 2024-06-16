package SWEN656.tictactoe.agtictactoe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/tictactoe")
public class GameController {

    private final GameSessionManager gameSessionManager;

    @Autowired
    public GameController(GameSessionManager gameSessionManager) {
        this.gameSessionManager = gameSessionManager;
    }

    @PostMapping("/start")
    public Map<String, String> startGame() {
        return gameSessionManager.createGame();
    }

    @PostMapping("/join/{gameId}")
    public void joinGame(@PathVariable String gameId) {
        boolean joined = gameSessionManager.joinGame(gameId, null); // Placeholder
        if (!joined) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to join the game.");
        }
    }
}
