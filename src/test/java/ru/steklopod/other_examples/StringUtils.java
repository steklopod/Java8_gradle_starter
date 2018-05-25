package ru.steklopod.other_examples;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
class StringUtils {

    //http://www.baeldung.com/string-processing-commons-lang
    @Test
    void stringUtils() {
        String string = "abba.com";
        boolean contained1 = containsAny(string, 'a', 'b', 'c');
        boolean contained2 = containsAny(string, 'x', 'y', 'z');
        boolean contained3 = containsAny(string, "abc");
        boolean contained4 = containsAny(string, "xyz");

        assertTrue(contained1);
        assertFalse(contained2);
        assertTrue(contained3);
        assertFalse(contained4);

        String s2 = "welcome to www.steklopod.com";
        int charNum = countMatches(s2, 'w');
        int stringNum = countMatches(s2, "com");

        assertEquals(4, charNum);
        assertEquals(2, stringNum);

        String originalString = "www.baeldung.com";
        String reversedString = reverseDelimited(originalString, '.');

        assertEquals("com.baeldung.www", reversedString);
    }

    @Test
    void поискДлиннойСтроки() {
        List<String> strings = Arrays.asList("aaa", "bbb", "ccc", "ddd", "ffff");

        String s = strings.stream()
                .reduce("", (left, right) -> left.length() > right.length() ? left : right);

        System.out.println(s); //output ffff
    }


    @Test
    @SneakyThrows
    void pattern() {
        Pattern pattern = Pattern.compile(".*@gmail\\.com");
        long count = Stream.of("bob@gmail.com", "alice@hotmail.com")
                .filter(pattern.asPredicate())
                .count();
        System.err.println(count);
    }

}
