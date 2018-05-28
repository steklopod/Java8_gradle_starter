package ru.steklopod.functional_examples.interfaces;

import io.github.glytching.junit.extension.random.RandomBeansExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@SpringBootTest
@RunWith(JUnitPlatform.class)
@ExtendWith({SpringExtension.class, RandomBeansExtension.class})
class FunctionalInterfaceExample {

    @FunctionalInterface
    interface Processor {
        int getStringLength(String str);
    }

    @Test
    void старт() {
        Processor stringProcessor = String::length;
        String name = "Java Lambda";
        Integer length = stringProcessor.getStringLength(name);

        System.out.println(length); //11
    }


    /**
     *  Функция на 4 аргумента:
     */
    @FunctionalInterface               //Function<T,R> - переход от объекта типа T к объекту типа R:
    interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }

    class Sum {
        Integer doSum(String s1, String s2) {
            return Integer.parseInt(s1) + Integer.parseInt(s2);
        }
    }

    @Test
    void doSum() {
        TriFunction<Sum, String, String, Integer> anon = (s, arg1, arg2) -> s.doSum(arg1, arg1);

        System.err.println(anon.apply(new Sum(), "1", "4"));
    }

    @Test
    void doSum_короче() {
        TriFunction<Sum, String, String, Integer> mRef = Sum::doSum;

        System.err.println(mRef.apply(new Sum(), "1", "4"));
    }

}
