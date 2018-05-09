package ru.steklopod;

import lombok.extern.slf4j.Slf4j;
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

import static io.github.benas.randombeans.api.EnhancedRandom.random;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@Transactional
@Slf4j
class MariaDbTest {

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

}
