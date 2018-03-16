package ru.stoloto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.stoloto.entities.mssql.Client;
import ru.stoloto.repositories.ms.ClientInDAO;

import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;

import static io.github.benas.randombeans.api.EnhancedRandom.random;

@ExtendWith(SpringExtension.class)
@SpringBootTest //(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(JUnitPlatform.class)
@Transactional
@DisplayName("MS_SQL: берем из [Input]")
//@Disabled
class MsSqlServerTest {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
//    @Autowired TestRestTemplate restTemplate;
//    @Autowired private TestEntityManager entityManager;
//    @Autowired private GenericWebApplicationContext context; http://www.baeldung.com/spring-5-functional-beans

    @Autowired
    private ClientInDAO repositoryMsSql;

    @Test
    @DisplayName("MS_SQL: кол-во записей в таблице")
    @Tag("MsSql")
    void testConnection(){
        Long aLong = repositoryMsSql.selectCount();
        System.out.println("+ С О Е Д И Н Е Н И Е  c Базой Данных У С Т А Н О В Л Е Н О +");
        System.err.println("Количество записей (строк) в таблице: " + aLong);
    }

    @Test
    @DisplayName("MS_SQL: сохранение > 😱")
    void saveInMSSQL(){
        Client person = random(Client.class);
        repositoryMsSql.saveAndFlush(person);
        System.err.println("OK. Сохранено успешно.");
    }




//    @Test
//    @Disabled
//    void testAbout() {
//        String message = this.restTemplate.getForObject("/about", String.class);
//        assertEquals("TEST SUCCESFUL", message);
//    }

}
