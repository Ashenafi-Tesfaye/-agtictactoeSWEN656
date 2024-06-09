package SWEN656.tictactoe.agtictactoe;

import org.springframework.web.bind.annotation.GetMapping;

public class UIController {

    @GetMapping("/")
    public String index() {
        return "index.html";
    }
}