package ru.steklopod;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.steklopod.repositories.ms.MssqlDAO;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest //(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(JUnitPlatform.class)
@Transactional
@DisplayName("MS_SQL: читаем из [Input]")
class MsSqlServerTest {

    @Autowired
    private MssqlDAO mssqlDAO;
    @Qualifier("jdbcMsSql")
    @Autowired
    private JdbcTemplate jdbcTemplateMsSql;

    private static Stream<Integer> makeIDs() {
        return Stream.of(11446446, 11446446, 11446446);
    }

    @ParameterizedTest(name = "Тест #{index} для id[{arguments}]")
    @MethodSource("makeIDs")
    @DisplayName("MS_SQL: получение Source по id  > 😱")
    void getPersonFromSqlServer(int id) {
        Integer bet = mssqlDAO.getSourceById(id).stream().findAny().get();
        System.err.println(bet);
    }

    @Test
    void average() {
        Integer average = mssqlDAO.averageSource();
        System.err.println(average);
    }
    @Test
    void jdbcTemp() {
        List<Long> idsList = jdbcTemplateMsSql.queryForList("SELECT DISTINCT ClientId FROM Bet", Long.class);
        Long first = idsList.stream().findFirst().get();
        System.err.println(first);
    }


}
