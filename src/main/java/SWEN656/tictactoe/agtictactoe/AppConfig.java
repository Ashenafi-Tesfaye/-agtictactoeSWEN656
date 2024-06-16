package SWEN656.tictactoe.agtictactoe;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class AppConfig {

@Bean
public GameOverAspect gameOverAspect() {
    return new GameOverAspect();
}
    
}
