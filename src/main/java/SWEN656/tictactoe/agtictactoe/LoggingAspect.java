package SWEN656.tictactoe.agtictactoe;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* SWEN656.tictactoe.agtictactoe.TicTacToeGame.makeMove(..))")
    public void logMove(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        logger.info("Player {} made a move at position {}", args[0], args[1]);
    }

    @AfterReturning(pointcut = "execution(* SWEN656.tictactoe.agtictactoe.TicTacToeHandler.handleStartGame(..))", returning = "result")
    public void logGameStart(JoinPoint joinPoint, Object result) {
        logger.info("Game started: {}", result);
    }

    @After("execution(* SWEN656.tictactoe.agtictactoe.TicTacToeGame.checkWinner(..))")
    public void logGameEnd(JoinPoint joinPoint) {
        logger.info("Game ended. Checking winner...");
    }
}
