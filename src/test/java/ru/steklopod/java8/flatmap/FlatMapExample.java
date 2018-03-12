package ru.steklopod.java8.flatmap;

import name.falgout.jeffrey.testing.junit5.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
@RunWith(JUnitPlatform.class)
class FlatMapExample {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
//    Stream<String[]>		-> flatMap ->	Stream<String>
//    Stream<Set<String>>	-> flatMap ->	Stream<String>
//    Stream<List<String>>	-> flatMap ->	Stream<String>
//    Stream<List<Object>>	-> flatMap ->	Stream<Object>

//    Как flatMap() работает:
//    { {1,2}, {3,4}, {5,6} }             -> flatMap -> {1,2,3,4,5,6}
//    { {'a','b'}, {'c','d'}, {'e','f'} } -> flatMap -> {'a','b','c','d','e','f'}

    private String[][] data = new String[][]{{"a", "b"}, {"c", "d"}, {"e", "f"}};

    @Test
    void notToDoLikeThis_wrong() {
        Stream<String[]> temp = Arrays.stream(data);

        Stream<String[]> stream = temp.filter(x -> "a".equals(x.toString()));
        List<String[]> afterFilter = new ArrayList<>();

        stream.forEach(afterFilter::add);
        System.err.println("afterFilter.size: " + afterFilter.size());
        assertEquals(afterFilter.size(), 0);
    }

    @Test
    void doLikeThis_Ok() {
        Stream<String[]> temp = Arrays.stream(data);
//      Stream<String> stringStream = temp.flatMap(x -> Arrays.stream(x));
        Stream<String> stringStream = temp.flatMap(Arrays::stream);

//      Stream<String> stream = stringStream.filter(x -> "a".equals(x.toString()));
        Stream<String> stream = stringStream.filter("a"::equals);
        List<String> afterFilter = new ArrayList<>();

        stream.forEach(afterFilter::add);
        System.err.println("afterFilter.size: " + afterFilter.size());
        assertNotNull(afterFilter);
    }

    @Test
    void objectFlatMap() {
        Student obj1 = new Student("Иван");
        obj1.addBook("Java 8 in Action");
        obj1.addBook("Spring Boot in Action");
        obj1.addBook("Effective Java (2nd Edition)");

        Student obj2 = new Student("Василий");
        obj2.addBook("Learning Python, 5th Edition");
        obj2.addBook("Effective Java (2nd Edition)");

        List<Student> list = new ArrayList<>();
        list.add(obj1);
        list.add(obj2);

        List<String> collect =
                list.stream()
                        .map(Student::getBook)      //Stream<Set<String>>
                        .flatMap(Collection::stream)   //Stream<String>
                        .distinct()
                        .collect(Collectors.toList());

        collect.forEach(System.out::println);
    }


}
