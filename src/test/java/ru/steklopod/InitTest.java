package ru.steklopod;

import io.github.glytching.junit.extension.random.RandomBeansExtension;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import name.falgout.jeffrey.testing.junit.mockito.MockitoExtension;
import org.junit.jupiter.api.RepeatedTest;
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

    @RepeatedTest(5)
    @Rollback(false)
//    @Disabled
    void savePerson() {
        User user = random(User.class);
        userDao.saveAndFlush(user);
        System.err.println("OK. Сохранено успешно.");
    }

    @Test
    void daoTest() {
        System.err.println(  userDao.selectUserNativeQueryLimitOne(2)  );

        System.err.println(  userDao.selectCountOfUsers()  );
    }


    @Test
    void jdbcTemplateTest() {
        val example = new ArrayList<Integer>();
        val integer = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user", Integer.class);
        example.add(integer);
        System.err.println(example);
    }



}
