package ru.steklopod.functional_examples.interfaces;

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
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static io.github.benas.randombeans.api.EnhancedRandom.random;

@Slf4j
@SpringBootTest
@RunWith(JUnitPlatform.class)
@ExtendWith({SpringExtension.class, RandomBeansExtension.class})
class Interfaces {

    @Test
        //Function<T,R> - переход от объекта типа T к объекту типа R:
    void FUNCTION() {
        Function<Integer, Double> celsiyToFahrenheitInt = x -> (double) ((x * 9 / 5) + 32);
        Function<String, Integer> stringToInt = Integer::valueOf;

        System.out.println("Centigrade to Fahrenheit: " + celsiyToFahrenheitInt.apply(36));
        System.out.println(" String to Int: " + stringToInt.apply("4"));
    }

    @Test
        // Возвращает и принимает один и тот же тип <T>:
    void UNARY() {
        UnaryOperator<Integer> square = x -> x * x;

        System.out.println(
                square.apply(5)
        ); // 25

        UnaryOperator<String> trimFunction = String::trim;
        UnaryOperator<String> toUpperCaseFunction = String::toUpperCase;

        System.err.println(
                Stream.of(" a ", " b ")
                        .map(
                                trimFunction
                                        .andThen(toUpperCaseFunction)
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


}
