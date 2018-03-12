package ru.steklopod;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.steklopod.entities.TestEntity;
import ru.steklopod.repositories.RepositoryForTest;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(JUnitPlatform.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JPATest {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RepositoryForTest repository;

    @Test
    void some() {
        String nullName = "Ничего не найдено";
        logger.info("Начинаем текстовый поиск...");
        Optional<TestEntity> person = repository.findById(1L);
        person.ifPresent((x) -> System.err.println("Найденное значение - " + x));
    }

    @Test
    void testAbout() {
        String message = this.restTemplate.getForObject("/about", String.class);
        assertEquals("TEST SUCCESFUL", message);
    }

}
