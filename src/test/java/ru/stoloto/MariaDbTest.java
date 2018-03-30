package ru.stoloto;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.entities.mariadb.UserRebased;
import ru.stoloto.repositories.maria.UserOutDAO;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static io.github.benas.randombeans.api.EnhancedRandom.random;

//@DataJpaTest
@ExtendWith(SpringExtension.class)
@SpringBootTest //(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(JUnitPlatform.class)
@Transactional
@DisplayName("MariaDb: пишем в [Output]")
@Disabled
class MariaDbTest {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private UserOutDAO userOutDAO;

    @Qualifier("jdbcMaria")
    @Autowired
    private  JdbcTemplate jdbcTemplateMaria;

    @Test
    @DisplayName("MariaDb: добавление колонок")
    void addColumns() {
        userOutDAO.addColumns();

    }

    @Test
    @DisplayName("MariaDb: получение по id = 1")
    void getPersonFromMyBatis() {
        Optional<UserRebased> person = userOutDAO.findById(1);
        person.ifPresent((x) -> System.err.println("OK. Найденное значение - " + x));
    }

    @Test
    @DisplayName("MariaDb: получение по id = 1 (АСИНХР.)")
    void testAsync() {
        CompletableFuture<UserRebased> oneById = userOutDAO.findOneById(1);
        UserRebased now = oneById.getNow(null);
        System.err.println("Ok. Ответ от БД получен:");
        System.err.println(now);
    }

    @Test
    @DisplayName("\uD83D\uDD25 MariaDb: сохранение ")
    @Disabled
    void savePerson() {
        UserRebased user = random(UserRebased.class);
        userOutDAO.saveAndFlush(user);
        System.err.println("OK. Сохранено успешно.");
    }

}
