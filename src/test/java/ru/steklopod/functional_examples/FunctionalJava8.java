package ru.steklopod.functional_examples;

import io.github.glytching.junit.extension.random.RandomBeansExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.steklopod.entities.User;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.github.benas.randombeans.api.EnhancedRandom.random;

@Slf4j
@SpringBootTest
@RunWith(JUnitPlatform.class)
@ExtendWith({SpringExtension.class, RandomBeansExtension.class})
class FunctionalJava8 {

    @Test
        // Возвращает и принимает один и тот же тип <T>:
    void UNARY() {
        UnaryOperator<Integer> square = x -> x * x;

        System.out.println(square.apply(5)); // 25

        UnaryOperator<String> trimFunction = String::trim;
        UnaryOperator<String> toUpperCaseFunction = String::toUpperCase;

        System.err.println(
                Stream.of(" a ", " b ")
                        .map(
                                trimFunction.andThen(toUpperCaseFunction)
                        )
        );// Stream is now ["A", "B"]
    }


    @Test
        //Принимает <T>, но ничего не возвращает:
    void CONSUMER() {
        Consumer<Integer> printer = x -> System.out.printf("%d долларов \n", x);

        printer.accept(600); // 600 долларов
    }



    @Test
        //Не принимает аргументов, но должен возвращать объект типа <T>:
    void SUPPLIER() {
        Supplier<User> userFactory = () -> random(User.class);

        User user1 = userFactory.get();
        User user2 = userFactory.get();

        System.out.println("Имя user1: " + user1.getName());
        System.out.println("Имя user2: " + user2.getName());
    }


    /**
     * Всячина:
     */

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
