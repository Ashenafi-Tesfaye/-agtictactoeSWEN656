package SWEN656.tictactoe.agtictactoe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final TicTacToeHandler ticTacToeHandler;

    @Autowired
    public WebSocketConfig(TicTacToeHandler ticTacToeHandler) {
        this.ticTacToeHandler = ticTacToeHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(ticTacToeHandler, "/tictactoe/ws").setAllowedOrigins("*");
    }
}