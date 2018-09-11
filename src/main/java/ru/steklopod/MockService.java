package ru.steklopod;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MockService {

    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

}
