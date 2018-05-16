package ru.steklopod;

import io.github.glytching.junit.extension.random.RandomBeansExtension;
import lombok.extern.slf4j.Slf4j;
import name.falgout.jeffrey.testing.junit.mockito.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@RunWith(JUnitPlatform.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class, RandomBeansExtension.class})
@Transactional
class StringUtils {

    @Test
    void stringUtils() {
        String string = "abba.com";
        boolean contained1 = org.apache.commons.lang3.StringUtils.containsAny(string, 'a', 'b', 'c');
        boolean contained2 = org.apache.commons.lang3.StringUtils.containsAny(string, 'x', 'y', 'z');
        boolean contained3 = org.apache.commons.lang3.StringUtils.containsAny(string, "abc");
        boolean contained4 = org.apache.commons.lang3.StringUtils.containsAny(string, "xyz");

        assertTrue(contained1);
        assertFalse(contained2);
        assertTrue(contained3);
        assertFalse(contained4);

        String s2 = "welcome to www.steklopod.com";
        int charNum = org.apache.commons.lang3.StringUtils.countMatches(s2, 'w');
        int stringNum = org.apache.commons.lang3.StringUtils.countMatches(s2, "com");

        assertEquals(4, charNum);
        assertEquals(2, stringNum);
    }

}
