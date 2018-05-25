package ru.steklopod.functional_examples;

import io.github.glytching.junit.extension.random.RandomBeansExtension;
import lombok.extern.slf4j.Slf4j;
import name.falgout.jeffrey.testing.junit.mockito.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.function.Function;

@Slf4j
@SpringBootTest
@RunWith(JUnitPlatform.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class, RandomBeansExtension.class})
class Functions {

    @Test
        //Function<T,R> - переход от объекта типа T к объекту типа R:
    void function() {
        Function<Integer, String> convert = x -> String.valueOf(x) + " долларов";
        System.out.println(convert.apply(5)); // 5 долларов
    }


    @Test
    void цельсий_в_фаренгейт() {
        Function<Integer, Double> celsiyToFahrenheitInt = x -> new Double((x * 9 / 5) + 32);
        Function<String, Integer> stringToInt = Integer::valueOf;

        System.out.println("Centigrade to Fahrenheit: " + celsiyToFahrenheitInt.apply(36));
        System.out.println(" String to Int: " + stringToInt.apply("4"));
    }


}
