package ru.steklopod.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MockService {

    @RequestMapping("/getArchive")
    public String index() {
        return "Greetings from Spring Boot!";
    }
}
