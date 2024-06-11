package SWEN656.tictactoe.agtictactoe;

import java.io.IOException;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.websocket.Session;

@Aspect
@Component
public class GameOverAspect {

    @Autowired
    private Referee referee;

    @Autowired
    private Player player1;
    
    @Autowired
    GameSessionManager gameSessionManager;

    @Autowired
    private Player player2;

    @Autowired
    private Board board;


    @After("execution(* SWEN656.tictactoe.agtictactoe.Player.move(..))")
    public void afterMove(JoinPoint joinPoint) {
        Player player = (Player) joinPoint.getTarget();
        String gameId = player.getGameId();
        TicTacToeGame game = gameSessionManager.getGame(gameId);

        if (referee.isGameOver(board)) {
            Player winner = referee.getWinner(board, player1, player2);
            referee.announceWinner(winner);
            sendGameOverMessage(gameId);
        } else {
            sendUpdateMessage(gameId, game);
        }
    }
    
    private void sendGameOverMessage(String gameId) {
        String message = String.format("{\"type\":\"gameover\",\"winner\":\"%s\"}", referee.getWinner(board, player1, player2).getName());
        gameSessionManager.broadcastMessage(gameId, message);
    }

    private void sendUpdateMessage(String gameId, TicTacToeGame game) {
        String boardString = game.getBoard().toString(); // Assuming you have a toString method in Board that returns the board as a string
        String message = String.format("{\"type\":\"update\",\"board\":\"%s\"}", boardString);
        gameSessionManager.broadcastMessage(gameId, message);
    }
}
