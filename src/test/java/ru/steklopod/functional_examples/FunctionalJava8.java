package ru.steklopod.functional_examples;

import io.github.glytching.junit.extension.random.RandomBeansExtension;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import name.falgout.jeffrey.testing.junit.mockito.MockitoExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.steklopod.entities.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@SpringBootTest
@RunWith(JUnitPlatform.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class, RandomBeansExtension.class})
class FunctionalJava8 {

    @Test
    void unary() {
        UnaryOperator<Integer> square = x -> x * x;

        System.out.println(square.apply(5)); // 25
    }

    @Test
        //Функциональный интерфейс Function<T,R> представляет функцию перехода от объекта типа T к объекту типа R:
    void function() {
        Function<Integer, String> convert = x -> String.valueOf(x) + " долларов";
        System.out.println(convert.apply(5)); // 5 долларов
    }

    @Test
        //Consumer<T> выполняет некоторое действие над объектом типа T, при этом ничего не возвращая:
    void consumer() {
        Consumer<Integer> printer = x -> System.out.printf("%d долларов \n", x);

        printer.accept(600); // 600 долларов
    }

    @Test
    @Disabled
        //Supplier<T> не принимает никаких аргументов, но должен возвращать объект типа T:
    void supplier() {
        Supplier<User> userFactory = () -> {

            Scanner in = new Scanner(System.in);
            System.out.println("Введите имя: ");
            String name = in.nextLine();
            return new User(name);
        };

        User user1 = userFactory.get();
        User user2 = userFactory.get();

        System.out.println("Имя user1: " + user1.getName());
        System.out.println("Имя user2: " + user2.getName());
    }

    @Test
    void anotherPredicat() {
        Predicate<Integer> isPositive = x -> x > 0;

        System.out.println(isPositive.test(5)); // true
        System.out.println(isPositive.test(-7)); // false
    }


    @Test
    @SneakyThrows
    void pattern() {
        Pattern pattern = Pattern.compile(".*@gmail\\.com");
        long count = Stream.of("bob@gmail.com", "alice@hotmail.com")
                .filter(pattern.asPredicate())
                .count();
        System.err.println(count);
    }

    @Test
    void поискДлиннойСтроки() {
        List<String> strings = Arrays.asList("aaa", "bbb", "ccc", "ddd", "ffff");

        String s = strings.stream()
                .reduce("", (left, right) -> left.length() > right.length() ? left : right);

        System.out.println(s); //output ffff
    }

    @Test
    void file() throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(""))) {
            String joined = stream
                    .map(String::valueOf)
                    .filter(path -> !path.startsWith("."))
                    .sorted()
                    .collect(Collectors.joining("; "));
            System.err.println("Файлы: " + joined);
        }
    }


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
