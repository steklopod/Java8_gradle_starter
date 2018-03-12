package ru.steklopod.java8;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@DisplayName("Паралель - Послед.")
class IntegerStreams {
    private static Logger logger = LoggerFactory.getLogger(IntegerStreams.class);
    private static final Integer COUNT = Integer.getInteger("count", 1_0);
    private static List<Integer> list;

    @BeforeAll
    static void setup() {
        list = new ArrayList<>(COUNT);
        for (int c = 1; c <= COUNT; c++) {
            list.add(c);
        }
        Collections.shuffle(list);
        System.out.println(list);
    }

    @SuppressWarnings("Не безопасный")
    @Test
    @DisplayName("Последовательный стрим")
    void testSeq() throws NoSuchElementException {
        logger.warn("COUNT = " + COUNT);
        Integer integer = list.stream().reduce(Math::max).get();
        logger.info("integer = " + integer);
        assertEquals(COUNT, integer);
    }

    @Test
    @SuppressWarnings("Безопасный")
    void maxOptionalEmptyList() {
        List<Integer> list = new ArrayList<>();
//          Будет пустой список [].
        logger.info("list: " + list);
        Optional<Integer> maxValue = list.stream().max(Integer::compareTo);
        Integer integer = maxValue.orElse(null);
//        Integer integer = maxValue.orElse(2018);
//          Будет null.
        logger.info("maxValue.orElse(null) = " + integer);

        List<Integer> list2 = new ArrayList<>();
        maxValue.ifPresent(System.err::println);
        maxValue.ifPresent(list2::add);
//          Будет Optional.empty.
        logger.info("list2: " + maxValue);
        assertNotEquals(integer, maxValue);
    }

    @Test
    void maxOptionalNotEmpty() {
        Optional<Integer> max = list.stream().max(Integer::compareTo);
        logger.info("max from Stream = " + max.toString());
        logger.info("max.get() from Stream = " + max.get());
        Integer integer = max.orElse(666);
        assertEquals(max.get(), integer);
        logger.info("max.orElse = " + max);
        ArrayList arrayList = new ArrayList();
        max.ifPresent(arrayList::add);
        logger.info("arrayList: " + arrayList);
    }

    @Test
    @SuppressWarnings("Безопасный")
    @DisplayName("Исключаем null")
    void maxWithNull() {
        ArrayList<Integer> testValuesNull = new ArrayList();
        testValuesNull.add(0, null);
        testValuesNull.add(1, 1);
        testValuesNull.add(2, 2);
        testValuesNull.add(3, 70);
        testValuesNull.add(4, 50);

        Optional<Integer> maxValueNotNull = testValuesNull
                .stream()
                .filter((p) -> p != null && p != 70)
                .max(Integer::compareTo);
        logger.info("maxValueNotNull= " + maxValueNotNull);
    }

    @Test
    @DisplayName("Паралельный стрим")
    void testParallel() {
        System.err.println(COUNT);
        Integer integer = list.parallelStream().reduce(Math::max).get();
        logger.info("max = " + integer);
        assertEquals(COUNT, integer);
    }

}
