package ru.steklopod.functional_examples;

import io.github.glytching.junit.extension.random.RandomBeansExtension;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import name.falgout.jeffrey.testing.junit.mockito.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.IntBinaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Slf4j
@SpringBootTest
@RunWith(JUnitPlatform.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class, RandomBeansExtension.class})
class BinaryOperatorTests {
    @Test
    void binaryString() {
        BinaryOperator<String> concat = (a, b) -> a + b;

        assertEquals(
                concat.apply(concat.apply("hello", "world"), "c"),// "helloworldc"

                concat.apply("hello", concat.apply("world", "c"))// "helloworldc"
        );
    }

    @Test
    void biFunction_New() {
        // Using a method reference
        BiFunction<String, String, Locale> f = Locale::new;
        Locale loc = f.apply("en", "UK");

        // Using a lambda expression
        BiFunction<String, String, Locale> f2 = (lang, country) -> new Locale(lang, country);
        Locale loc2 = f2.apply("en", "UK");
    }


    @Test
    void binaryInt() {
        BinaryOperator<Integer> multiply = (x, y) -> x * y;

        System.out.println(multiply.apply(3, 5));   // 15
        System.out.println(multiply.apply(10, -2)); // -20
    }

    @Test
    void binaryList() {
        BinaryOperator<List<String>> concat = (a, b) -> {
            a.addAll(b);
            return a;
        };
        val listA = new ArrayList<String>();
        val listB = new ArrayList<String>() {{
            add("A");
            add("B");
            add("C");
        }};
        concat.apply(listA, listB);

        assertFalse(listA.isEmpty());
    }


    @Test
    void binarySearch() {
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        class Connection {
            private String from;
            private String to;
        }
        List<Connection> network = Arrays.asList(
                new Connection("A", "B"),
                new Connection("A", "C"),
                new Connection("A", "D"),
                new Connection("B", "C")
        );

        List<String> identity = new ArrayList<>();

        //просто добавляем наши входящие узлы в список (identity) и возвращаем его
        BiFunction<List<String>, Connection, List<String>> accumulator = (strings, connection) -> {
            strings.add(connection.getTo());
            return strings;
        };
        //соединяет два списка узлов в один
        BinaryOperator<List<String>> combiner = (strings, strings2) -> {
            strings.addAll(strings2);
            return strings;
        };
        List<String> list = network
                .stream()
                .filter(p -> "A".equals(p.getFrom()))
                .reduce(identity, accumulator, combiner);

        System.out.println(list); //output [B, C, D]
    }


    @Test
    void binary_Хардкорный() {
        BinaryOperator<Integer> sum1 = Integer::sum;
        IntBinaryOperator sum2 = Integer::sum;
        BinaryOperator<Integer> max1 = Integer::max;
        IntBinaryOperator max2 = Integer::max;
        BinaryOperator<Integer> min1 = Integer::min;
        IntBinaryOperator min2 = Integer::min;
        Comparator<Integer> cmp = Integer::compare;

        int[] numbers = {-1, 0, 1, 100, Integer.MAX_VALUE, Integer.MIN_VALUE};

        for (int i : numbers) {
            for (int j : numbers) {
                assertEquals(i + j, (int) sum1.apply(i, j));
                assertEquals(i + j, sum2.applyAsInt(i, j));
                assertEquals(Math.max(i, j), (int) max1.apply(i, j));
                assertEquals(Math.max(i, j), max2.applyAsInt(i, j));
                assertEquals(Math.min(i, j), (int) min1.apply(i, j));
                assertEquals(Math.min(i, j), min2.applyAsInt(i, j));
                assertEquals(((Integer) i).compareTo(j), cmp.compare(i, j));
            }
        }
    }


}
