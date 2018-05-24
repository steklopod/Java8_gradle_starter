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
class FunctionInterface {

    @Test
        //Функциональный интерфейс Function<T,R> представляет функцию перехода от объекта типа T к объекту типа R:
    void function() {
        Function<Integer, String> convert = x -> String.valueOf(x) + " долларов";
        System.out.println(convert.apply(5)); // 5 долларов
    }

    @Test
    void functionCompose() {
        Function<Integer, Integer> f1 = i -> i * 4;

        Function<Integer, Integer> f2 = i -> i + 4;

        System.out.println(f2.compose(f1).apply(3)); // 16

        //f2.compose(f1).apply(3) - is equals to below statements
        Integer j1 = f1.apply(3);
        Integer j2 = f2.apply(j1);

        System.out.println(j2); //16
    }

    @Test
    void functionAndThen() {
        Function<Integer, Integer> f1 = i -> i * 4;

        Function<Integer, Integer> f2 = i -> i + 4;

        System.out.println(f2.andThen(f1).apply(3)); // 28

        //f2.andThen(f1).apply(3) - is equals to below statements
        Integer j1 = f2.apply(3);
        Integer j2 = f1.apply(j1);

        System.out.println(j2); //28
    }


    @Test
    void oneMore() {
        //define a function directly
        Function<Integer, Integer> add1 = a -> a + 1;

        class m {
            Integer addTen(Integer a) {
                return a + 10;
            }
        }

        //function via method reference
        Function<Integer, Integer> add10 = new m()::addTen;

        add1.apply(10);//11
        add10.apply(10);//20

        //function composition
        Function<Integer, Integer> add5 = add1.andThen(add1)
                .andThen(add1)
                .andThen(add1)
                .andThen(add1);
    }
}
