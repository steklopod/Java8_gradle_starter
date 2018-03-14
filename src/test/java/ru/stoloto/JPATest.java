package ru.stoloto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.stoloto.entities.mssql.MSPerson;
import ru.stoloto.entities.mybatis.TestEntity;
import ru.stoloto.repositories.ms.MSSqlRepo;
import ru.stoloto.repositories.mybatis.RepositoryForTest;

import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(JUnitPlatform.class)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class JPATest {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RepositoryForTest repository;

    @Autowired
    private MSSqlRepo repositoryMsSql;

    @Test
    void saveInMSSQL(){
        MSPerson person = new MSPerson("Vasiliy Petrov", true);
        repositoryMsSql.saveAndFlush(person);
    }


    @Test
    void getPerson() {
        String nullName = "Ничего не найдено";
        logger.info("Начинаем текстовый поиск...");
        Optional<TestEntity> person = repository.findById(1L);
        person.ifPresent((x) -> System.err.println("Найденное значение - " + x));
    }

    @Test
    void savePerson(){
        TestEntity person = new TestEntity("Peter", true);
        Object save = repository.saveAndFlush(person);
        System.err.println(save);
    }


    @Test
    void testAbout() {
        String message = this.restTemplate.getForObject("/about", String.class);
        assertEquals("TEST SUCCESFUL", message);
    }

    @Test
    void testAsync(){
        CompletableFuture<TestEntity> oneById = repository.findOneById(1L);
        TestEntity now = oneById.getNow(null);
        System.err.println(now);
    }

}
