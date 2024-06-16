package SWEN656.tictactoe.agtictactoe;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

    @Aspect
    @Component
    public class GameOverAspect {

            private static final Logger logger = LoggerFactory.getLogger(GameOverAspect.class);


        @Autowired
        private GameSessionManager gameSessionManager;

        @After("execution(* SWEN656.tictactoe.agtictactoe.TicTacToeGame.makeMove(..))")
        public void afterMove(JoinPoint joinPoint) {
            TicTacToeGame game = (TicTacToeGame) joinPoint.getTarget();
            String gameId = game.getGameId();

            if (game.isGameOver()) {
                Player winner = game.getWinner();
                String message = String.format("{\"type\":\"gameover\",\"winner\":\"%s\"}", winner != null ? winner.getMark() : "none");
                gameSessionManager.broadcastMessage(gameId, message);
                logger.info("GameOverAspect triggered: Game ID: {}, Winner: {}", gameId, winner != null ? winner.getMark() : "none");

            }
        }
}
