package ru.steklopod;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Starter
        implements CommandLineRunner
{

    public void run(String... args){
        log.info("Hello");
    }

}
