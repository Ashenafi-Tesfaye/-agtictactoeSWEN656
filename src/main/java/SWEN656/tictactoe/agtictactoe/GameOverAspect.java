package SWEN656.tictactoe.agtictactoe;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class GameOverAspect {

    @Autowired
    private Referee referee;

    @Autowired
    private Player player1;

    @Autowired
    private Player player2;

    @Autowired
    private Board board;

    @After("execution(* SWEN656.tictactoe.agtictactoe.Player.move(..))")
    public void afterMove() {
        if (referee.isGameOver(board)) {
            Player winner = referee.getWinner(board, player1, player2);
            referee.announceWinner(winner);
        }
    }
}