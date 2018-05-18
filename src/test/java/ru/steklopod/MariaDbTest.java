package ru.steklopod;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.steklopod.entities.mariadb.MariaEntity;
import ru.steklopod.repositories.maria.MariaDAO;

import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@SpringBootTest //(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(JUnitPlatform.class)
@Transactional
@DisplayName("MariaDb: пишем в [Output]")
class MariaDbTest {
    @Autowired
    private MariaDAO mariaDAO;
    
    @Qualifier("jdbcMaria")
    @Autowired
    private JdbcTemplate jdbcTemplateMaria;

    @Test
    @DisplayName("MariaDb: добавление колонок")
    @Disabled
    void addColumns() {
        Stream.of("ALTER TABLE maria_init_db ADD COLUMN IF NOT EXISTS migration_state TINYINT(4)",
                "ALTER TABLE maria_init_db ADD COLUMN IF NOT EXISTS registration_source TINYINT(4)",
                "ALTER TABLE maria_init_db ADD COLUMN IF NOT EXISTS last_modify datetime",
                "ALTER TABLE maria_init_db ADD COLUMN IF NOT EXISTS notify_email bit(1)",
                "ALTER TABLE maria_init_db ADD COLUMN IF NOT EXISTS notify_phone bit(1)")
                .forEach(x -> jdbcTemplateMaria.queryForObject(x, Void.class));
    }

    @Test
    void getPersonFromMyBatis() {
        mariaDAO.save(new MariaEntity("Name"));
    }



}
