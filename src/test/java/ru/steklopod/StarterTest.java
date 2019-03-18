package ru.steklopod;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Random;

class StarterTest {

    @Test
    void randomsInts() {
        Random random = new Random();
        random.ints().limit(10).forEach(System.out::println);
    }
    @Test
    void localDate() {
        LocalDate today = LocalDate.now();

        // плюс день
        LocalDate tomorrow = today.plus(1, ChronoUnit.WEEKS);
            System.out.println(tomorrow);

        //слуд. пятница
        LocalDate nextFriday = today.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
            System.out.println(nextFriday);
    }

}



