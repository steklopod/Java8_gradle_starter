package ru.steklopod;

import io.github.glytching.junit.extension.random.RandomBeansExtension;
import lombok.extern.slf4j.Slf4j;
import name.falgout.jeffrey.testing.junit.mockito.MockitoExtension;
import org.junit.jupiter.api.Disabled;
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

import java.util.stream.IntStream;

import static io.github.benas.randombeans.api.EnhancedRandom.random;

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
    @Disabled
    void savePerson() {
        User user = random(User.class);
        userDao.saveAndFlush(user);
        System.err.println("OK. Сохранено успешно.");
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

}
