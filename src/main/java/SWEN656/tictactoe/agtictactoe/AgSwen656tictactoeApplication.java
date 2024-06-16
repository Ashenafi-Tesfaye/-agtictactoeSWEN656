package SWEN656.tictactoe.agtictactoe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "SWEN656.tictactoe.agtictactoe")
public class AgSwen656tictactoeApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgSwen656tictactoeApplication.class, args);
	}

}
