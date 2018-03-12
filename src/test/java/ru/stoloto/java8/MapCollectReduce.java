package ru.stoloto.java8;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
class MapCollectReduce {
    private static Logger logger = LoggerFactory.getLogger(MapCollectReduce.class);


    @Test
    void getFirst() {
        List<String> strings = Arrays.asList("Foo", "Bar", "Baz");
        Stream<String> stream = strings.stream();
        String firstElemInStream = stream.findFirst().get();
        assertEquals(
                "Foo",
                firstElemInStream
        );
    }

    @Test
    void filterCollect() {
        List<String> list = Arrays.asList("S", "SS", "SSS", "SSSS");
        List<String> collect = list.stream()
                .filter((s) -> s.length() > 2)
                .collect(Collectors.toList());
        assertTrue(list.size() != collect.size());
        System.err.println(list);
        System.err.println(collect);
    }

    @Test
    void mapCollect() {
        List<Integer> integers =
                Arrays.asList(3, 3, 3);
        List<Integer> collect =
                Stream.of("Foo", "Bar", "Baz")
                        .map(String::length)
                        .collect(Collectors.toList());
        assertEquals(integers, collect);
    }

    @Test
    void sortCollect() {
        assertEquals(
                Arrays.asList("Bar", "Baz", "Foo"),
                Arrays.asList("Foo", "Bar", "Baz")
                        .stream()
                        .sorted(String::compareTo)
                        .collect(Collectors.toList())
        );
    }

    @Test
    void mapReduce() {
        assertEquals(
                Integer.valueOf(9),
                Arrays.asList("Foo", "BarBar", "BazBazBaz")
                        .stream()
                        .map(String::length)
                        .reduce((l, r) -> (l > r ? l : r))
                        .get()
        );
    }

    @Test
    void distinct() {
        assertEquals(
                new ArrayList<String>() {{
                    add("Foo");
                    add("Bar");
                    add("Baz");
                }},
                Arrays.asList("Foo", "Bar", "Baz", "Baz", "Foo", "Bar")
                        .stream()
                        .distinct()
                        .collect(Collectors.toList())
        );
    }

    @Test
//    @DisplayName("Склейка текста")
    void forEach() {
        StringBuilder sb = new StringBuilder();
        List<String> strings = Arrays.asList("Foo", "Bar", "Baz");
        strings.forEach(sb::append);
        assertEquals(
                "FooBarBaz",
                sb.toString()
        );
        System.err.println(strings);
        System.err.println(strings.get(1));
    }

    @Test
//    @DisplayName("Разбивка текста")
    void flatMapCollect() {
        assertEquals(
                Arrays.asList("Foo", "Bar", "Baz"),
                Arrays.asList("Foo Bar Baz")
                        .stream()
                        .flatMap((element) -> Arrays.stream(element.split(" ")))
                        .collect(Collectors.toList())
        );
    }

    private int invocations = 0;

    @Test
    void testLazy() {
        Stream<String> stream = Arrays.asList("Foo", "Marco", "Bar", "Polo", "Baz")
                .stream()
                .filter((s) -> {
                    invocations++;
                    return s.length() == 3;
                });

        Iterator<String> i = stream.iterator();

        assertEquals("Foo", i.next());
        assertEquals(1, invocations);

        assertEquals("Bar", i.next());
        assertEquals(3, invocations);

        assertEquals("Baz", i.next());
        assertEquals(5, invocations);
    }

    @Test
    void testEager() {
        List<String> list = Arrays.asList("Foo", "Marco", "Bar", "Polo", "Baz")
                .stream()
                .filter((s) -> {
                    invocations++;
                    return s.length() == 3;
                })
                .collect(Collectors.toList());
        assertEquals(3, list.size());
        assertEquals(5, invocations);
        System.err.println(list);
    }

    @Test
    void skipLimit() {
        List<String> stringList = new ArrayList<>();
        stringList.add("Кот");
        stringList.add("Пёс");
        stringList.add("Котопёс");
        stringList.add("Котяра");
        stringList.add("Котик");
//        stringList.add(null);
        List<String> resultList = stringList.stream()
                .filter(value -> value.startsWith("Кот"))
                .skip(1)
                .limit(2)
                .collect(Collectors.toList());
        resultList.forEach(value -> System.out.println(value));
    }
}
