package ru.steklopod;

import io.github.glytching.junit.extension.random.RandomBeansExtension;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import name.falgout.jeffrey.testing.junit.mockito.MockitoExtension;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.steklopod.entities.User;
import ru.steklopod.repositories.UserDao;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@RunWith(JUnitPlatform.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class, RandomBeansExtension.class})
@Transactional
class InitTest {

    @Autowired
    private UserDao userDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("MariaDb: сохранение ")
    @Rollback(false)
//    @Disabled
    void savePerson() {
        User user = random(User.class);
        userDao.saveAndFlush(user);
        System.err.println("OK. Сохранено успешно.");
    }

    @Test
    void lombok() {
        val example = new ArrayList<Integer>();
        val integer = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user", Integer.class);
        example.add(integer);
        System.err.println(example);
    }


    @Test
    void intStream() {
        IntStream.iterate(0, i -> i + 2).limit(3);    // > 0, 2, 4

        IntStream.range(1, 5).map(i -> i * i);            // > 1, 4, 9, 16

        IntStream.range(1, 5).anyMatch(i -> i % 2 == 0);  // > true

        IntStream.range(1, 5).allMatch(i -> i % 2 == 0);  // > false
        IntStream.range(1, 5).noneMatch(i -> i % 2 == 0); // > false

        IntStream.range(1, 5)
                .filter(i -> i % 2 == 0)
                .allMatch(i -> i % 2 == 0);               // > true
        IntStream.range(1, 5)
                .filter(i -> i % 2 == 0)
                .noneMatch(i -> i % 2 != 0);              // > true

        IntStream.range(1, 5).max().getAsInt();           // > 4

        IntStream.range(1, 5)
                .reduce(1, (x, y) -> x * y);       // > 24
    }

    @Test
    void stringUtils() {
        String string = "abba.com";
        boolean contained1 = StringUtils.containsAny(string, 'a', 'b', 'c');
        boolean contained2 = StringUtils.containsAny(string, 'x', 'y', 'z');
        boolean contained3 = StringUtils.containsAny(string, "abc");
        boolean contained4 = StringUtils.containsAny(string, "xyz");

        assertTrue(contained1);
        assertFalse(contained2);
        assertTrue(contained3);
        assertFalse(contained4);

        String s2 = "welcome to www.steklopod.com";
        int charNum = StringUtils.countMatches(s2, 'w');
        int stringNum = StringUtils.countMatches(s2, "com");

        assertEquals(4, charNum);
        assertEquals(2, stringNum);
    }

}
