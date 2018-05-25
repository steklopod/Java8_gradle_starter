package ru.steklopod.other_examples;

import io.github.glytching.junit.extension.random.RandomBeansExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.IntStream;

@Slf4j
@SpringBootTest
@RunWith(JUnitPlatform.class)
@ExtendWith({SpringExtension.class, RandomBeansExtension.class})
class Others {

    @Test
    void intStream() {
        IntStream.iterate(0, i -> i + 2).limit(3);    // > 0, 2, 4

        IntStream.range(1, 5).map(i -> i * i);            // > 1, 4, 9, 16

        IntStream.range(1, 5).anyMatch(i -> i % 2 == 0);  // > true

        IntStream.range(1, 5).allMatch(i -> i % 2 == 0);  // > false
        IntStream.range(1, 5).noneMatch(i -> i % 2 == 0); // > false

        IntStream.range(1, 5)
                .filter(i -> i % 2 == 0)
                .allMatch(i -> i % 2 == 0);               // > true
        IntStream.range(1, 5)
                .filter(i -> i % 2 == 0)
                .noneMatch(i -> i % 2 != 0);              // > true

        IntStream.range(1, 5).max().getAsInt();           // > 4

        IntStream.range(1, 5)
                .reduce(1, (x, y) -> x * y);       // > 24
    }

}
