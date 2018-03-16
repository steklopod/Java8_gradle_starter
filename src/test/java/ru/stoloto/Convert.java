package ru.stoloto;

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
import ru.stoloto.entities.mssql.Client;
import ru.stoloto.repositories.maria.UserOutDAO;
import ru.stoloto.repositories.ms.ClientInDAO;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import static io.github.benas.randombeans.api.EnhancedRandom.random;

//@DataJpaTest
@ExtendWith(SpringExtension.class)
@SpringBootTest //(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(JUnitPlatform.class)
//@Transactional
@DisplayName("Тестируем JPA-конверторы")
//@Disabled
class Convert {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private UserOutDAO userOutDAO;
    @Autowired
    private ClientInDAO repositoryMsSql;

    @Qualifier("jdbcMaria")
    @Autowired
    JdbcTemplate jdbcTemplateMaria;

    @Qualifier("jdbcMsSql")
    @Autowired
    JdbcTemplate jdbcTemplateMsSql;

    @Test
    void getCount_from_ms_sql() {
        jdbcTemplateMsSql.queryForObject("SELECT COUNT(*) FROM Client", Integer.class);
    }


    @Test
    @DisplayName("SELECT User from Sql-Server")
    void getPersonFromMaria() {
        Client clientToSave = random(Client.class);
        clientToSave.setId(1);
        repositoryMsSql.saveAndFlush(clientToSave);
        Optional<Client> person = repositoryMsSql.findById(1);

        Client client = person.get();
        System.err.println(client);


        person.ifPresent((x) -> System.err.println("OK. Найденное значение - " + x));

    }

    @Test
    @DisplayName("😱 \uD83D\uDE28 \uD83D\uDC7F")
    void savePerson() {
        System.err.println("Devil");


    }


}
