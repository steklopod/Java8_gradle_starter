package ru.steklopod;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Starter
        implements CommandLineRunner
{
    @Override
    public void run(String... args) throws Exception {
        System.out.println("Hello");
    }
}
