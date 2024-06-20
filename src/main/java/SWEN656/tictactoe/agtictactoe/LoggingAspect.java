package SWEN656.tictactoe.agtictactoe;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;


@Aspect
@Configuration
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* SWEN656.tictactoe.agtictactoe.TicTacToeGame.makeMove(..))")
    public void logMove(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        logger.info("Player {} made a move at position {}", args[0], args[1]);
    }

   @After("execution(* SWEN656.tictactoe.agtictactoe.GameSessionManager.createGame(..))")
   public void logGameStart(JoinPoint joinPoint) {
       logger.info("Game started: {}");
   }
}
