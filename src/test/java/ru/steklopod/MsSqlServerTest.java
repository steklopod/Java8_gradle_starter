package ru.steklopod;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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
import ru.steklopod.entities.mssql.Client;
import ru.steklopod.repositories.ms.ClientInDAO;

import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static io.github.benas.randombeans.api.EnhancedRandom.random;

@ExtendWith(SpringExtension.class)
@SpringBootTest //(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(JUnitPlatform.class)
@Transactional
@DisplayName("MS_SQL: читаем из [Input]")
@Disabled
class MsSqlServerTest {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private ClientInDAO repositoryMsSql;

    @Qualifier("jdbcMsSql")
    @Autowired
    private JdbcTemplate jdbcTemplateMsSql;

    private static Stream<Integer> makeIDs() {
        return Stream.of(11571919, 11591672
//                , 11595571, 11812258, 55308090, 22225320, 11563150, 11701132,
//                11486046, 11523437, 55238717, 11446392
        );
    }

    @ParameterizedTest(name = "Тест #{index} для id[{arguments}]")
    @MethodSource("makeIDs")
    @DisplayName("MS_SQL: получение по id  > 😱")
    void getPersonFromSqlServer(int id) {
        Optional<Client> person = repositoryMsSql.findById(id);
        boolean present = person.isPresent();
        if (present) {
            System.out.println("OK. Найденное значение: \n");
            System.out.println(person.get() + "\n");
        } else {
            throw new IllegalArgumentException("Пользователь с id " + id + " не найден.");
        }
    }

    @ParameterizedTest(name = "Тест #{index} для id[{arguments}]")
    @MethodSource("makeIDs")
    @DisplayName("MS_SQL: TIME")
    void timeStamp(int id) {
        Optional<Client> person = repositoryMsSql.findById(id);
        boolean present = person.isPresent();
        if (present) {
            Timestamp registrationDate = person.get().getRegistrationDate();
            System.err.println(registrationDate);

            Long duration = (long) (3600 * 1000);
            registrationDate.setTime(registrationDate.getTime() + duration);
            System.out.println(registrationDate);

        } else {
            throw new IllegalArgumentException("Пользователь с id " + id + " не найден.");
        }
    }

    @Test
    void cashDesk() {
        repositoryMsSql.findAll().parallelStream()
                .forEach(x -> {
                    if (x.getCashDeskId() != null) {
                        System.err.println(x.getId());
                    }
                });
    }


    @Test
    @DisplayName("Проверка кол-ва записей  [jdbcTemplate} - \uD83C")
    @Disabled
    void getCount_from_ms_sql_jdbc() {
        Integer countOfRecords = jdbcTemplateMsSql.queryForObject("SELECT COUNT(*) FROM dbo.Client", Integer.class);
        System.err.println("Соединениу с БД установлено. Кол-во записей: " + countOfRecords);
    }

    @Test
    @DisplayName("MS_SQL: кол-во записей в таблице")
    @Tag("MsSql")
    void testConnection() {
        Long aLong = repositoryMsSql.selectCount();
        System.out.println("+ С О Е Д И Н Е Н И Е  c Базой Данных У С Т А Н О В Л Е Н О +");
        System.err.println("Количество записей (строк) в таблице: " + aLong);
    }

    @Test
    @DisplayName("MS_SQL: сохранение")
    @Disabled
    void saveInMSSQL() {
        Client person = random(Client.class);
        repositoryMsSql.saveAndFlush(person);
        System.err.println("OK. Сохранено успешно.");
    }

    @Test
    @DisplayName("Получаем все регионы")
    @Disabled
    void getAllRegions() {
        Set<Integer> allRegions = repositoryMsSql.getAllRegions();
        System.out.println(">>> Кол-во найденных регионов: " + allRegions.size() + " шт");
        System.err.println(allRegions);
    }


}
