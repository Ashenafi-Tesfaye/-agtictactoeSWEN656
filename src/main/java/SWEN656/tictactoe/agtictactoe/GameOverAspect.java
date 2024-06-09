package SWEN656.tictactoe.agtictactoe;

public class GameOverAspect {

    @Autowired
    private Referee referee;

    @After("execution(* SWEN656.tictactoe.agtictactoe.Board.placeMark(..))")
    public void afterMove() {
        if (referee.isGameOver()) {
            if (referee.hasWinner('X')) {
                referee.announceWinner(player1, player2);
            } else if (referee.hasWinner('O')) {
                referee.announceWinner(player1, player2);
            } else {
                System.out.println("It's a tie!");
            }
            referee.announceWinner(player1, player2);

        }
    }
}
