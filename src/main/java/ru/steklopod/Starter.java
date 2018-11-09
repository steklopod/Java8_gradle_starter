package ru.steklopod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import static ru.steklopod.config.MvcConfig.logAdresses;

@SpringBootApplication
public class Starter extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Starter.class);
    }
    public static void main(String[] args) {
        SpringApplication.run(Starter.class, args);
        logAdresses();
    }


}