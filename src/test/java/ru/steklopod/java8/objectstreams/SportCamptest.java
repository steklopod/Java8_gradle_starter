package ru.steklopod.java8.objectstreams;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
class SportCamptest {
    private static Logger logger = LoggerFactory.getLogger(SportCamptest.class);

    Collection<SportsCamp> people = Arrays.asList(
            new SportsCamp("Ivan", 5),
            new SportsCamp("Petr", 7),
            new SportsCamp("Ira", 10),
            new SportsCamp("Anna", 12),
            new SportsCamp("Putin", 12)
    );

    @Test
//    Поиск имени самого большого по продолжительности нахождения в лагере
    void sportCamp() {
        String name = people
                .stream()
                .max((p1, p2) -> p1.getDay().compareTo(p2.getDay()))
                .get()
                .getName();

        // То же самое
        String name2 = people
                .stream()
                .max(Comparator.comparing(SportsCamp::getDay))
                .get()
                .getName();

        System.out.println("больше всех пробыл в лагере -" + name);

        String min = people.stream().min(Comparator.comparing(SportsCamp::getDay)).get().getName();
        System.err.println("меньше всех пробыл в лагере - " + min);

        assertEquals(name, name2);
    }

    @Test
    void sportCampWithNull() {
        Collection<SportsCamp> sport = Arrays.asList(
                new SportsCamp("Ivan", 5),
                new SportsCamp(null, 15),
                new SportsCamp("Petr", 7),
                new SportsCamp("Ira", 10)
        );

        String nameTest =
                sport.stream()
                        .filter((p) -> p.getName() != null)
                        .max((p1, p2) -> p1.getDay().compareTo(p2.getDay()))
                        .get()
                        .getName();
    }

    @Test
    void mapFilter() {
        people.stream()
                .map(s -> {
                    System.out.println("map: " + s.getName());
                    return s.getName().toUpperCase();
                })
                .filter(s -> {
                    System.out.println("filter: " + s);
                    return s.startsWith("P");
                })
                .forEach(s -> System.out.println("forEach: " + s));
    }

    @Test
    void collector() {
        List<SportsCamp> kids = people.stream()
                .filter(p -> p.getName().startsWith("P"))
                .collect(Collectors.toList());
        System.err.println(kids);
    }


    @Test
    void sortObject() {
        Map<Integer, List<SportsCamp>> personsByAge = people
                .stream()
                .collect(Collectors.groupingBy(p -> p.getDay()));
        personsByAge
                .forEach((age, p) -> System.out.format("Дней в лагере - %s: %s\n", age, p));
    }



}


