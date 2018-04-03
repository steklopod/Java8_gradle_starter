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
import ru.stoloto.entities.mariadb.UserWithException;
import ru.stoloto.entities.mssql.Region;
import ru.stoloto.repositories.maria.UserOutDAO;
import ru.stoloto.repositories.ms.RegionDao;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.springframework.test.util.AssertionErrors.assertNotEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest //(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(JUnitPlatform.class)
@Transactional
@DisplayName("MariaDb: пишем в [Output]")
//@Disabled
class MariaDbTest {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private UserOutDAO userOutDAO;

    @Autowired
    private RegionDao regionDao;
    @Qualifier("jdbcMaria")
    @Autowired
    private JdbcTemplate jdbcTemplateMaria;

    @Test
    @DisplayName("MariaDb: добавление колонок")
    @Disabled
    void addColumns() {
//        userOutDAO.addColumns();
        Stream.of("ALTER TABLE user ADD COLUMN IF NOT EXISTS migration_state TINYINT(4)",
                "ALTER TABLE user ADD COLUMN IF NOT EXISTS registration_source TINYINT(4)",
                "ALTER TABLE user ADD COLUMN IF NOT EXISTS last_modify datetime",
                "ALTER TABLE user ADD COLUMN IF NOT EXISTS notify_email bit(1)",
                "ALTER TABLE user ADD COLUMN IF NOT EXISTS notify_phone bit(1)")
                .forEach(x -> jdbcTemplateMaria.queryForObject(x, Void.class));
    }

    @Test
    @DisplayName("MariaDb: получение по id = 1")
    void getPersonFromMyBatis() {
        Optional<UserRebased> person = userOutDAO.findById(1);
        person.ifPresent((x) -> System.err.println("OK. Найденное значение - " + x));
    }

    @Test
    void delete() {
        userOutDAO.dropExTable();
    }

    @Test
    void region() {
        Integer regId = 190;
        HashMap<Integer, String> regionsAlpha3Diction = new HashMap<>();
        try (Stream<Region> regionStream = regionDao
                .findAll()
                .stream()
                .filter(x -> x.getAlpha2Code() != null)) {
            regionStream
                    .forEach(o -> regionsAlpha3Diction.put(o.getId(), o.getAlpha3Code()));
        }
        assertNotEquals("SIZE", regionsAlpha3Diction.size() , 0);
        String regionAlpha = regionsAlpha3Diction.get(regId);

        System.err.println(regionAlpha);

        UserRebased random = random(UserRebased.class);
        random.setRegion(regionAlpha);

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
