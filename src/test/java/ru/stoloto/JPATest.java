package ru.stoloto;

import org.junit.jupiter.api.DisplayName;
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
import ru.stoloto.entities.mybatis.UserRebased;
import ru.stoloto.repositories.ms.MSSqlDAO;
import ru.stoloto.repositories.mybatis.MyBatisDAO;

import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(JUnitPlatform.class)
@Transactional
class JPATest {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

//    @Autowired
//    private TestRestTemplate restTemplate;

    @Autowired
    private MyBatisDAO myBatisDAO;

    @Autowired
    private MSSqlDAO repositoryMsSql;

    @Test
    @DisplayName("😱 Сохранение в MSsql")
    void saveInMSSQL(){
        Client person = new Client("Vasiliy Petrov", true);
        repositoryMsSql.saveAndFlush(person);
    }


    @Test
    @DisplayName("MyBatis GET")
    void getPersonFromMyBatis() {
//        String nullName = "Ничего не найдено";
        logger.info("Начинаем текстовый поиск...");
        Optional<UserRebased> person = myBatisDAO.findById(1L);
        person.ifPresent((x) -> System.err.println("Найденное значение - " + x));
    }

    @Test
    void savePerson(){
        UserRebased person = new UserRebased("Peter", "Djesyude7Ydbdcjd**ed4");
        Object save = myBatisDAO.saveAndFlush(person);
        System.err.println(save);
    }


    @Test
    @DisplayName("MyBatis асинхронный GET")
    void testAsync(){
        CompletableFuture<UserRebased> oneById = myBatisDAO.findOneById(1L);
        UserRebased now = oneById.getNow(null);
        System.err.println(now);
    }



//    @Test
//    void testAbout() {
//        String message = this.restTemplate.getForObject("/about", String.class);
//        assertEquals("TEST SUCCESFUL", message);
//    }

}
