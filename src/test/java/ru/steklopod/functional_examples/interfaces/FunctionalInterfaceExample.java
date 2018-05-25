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


}
