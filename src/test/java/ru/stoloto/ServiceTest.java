package ru.stoloto;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
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
import ru.stoloto.entities.mssql.Client;
import ru.stoloto.entities.mssql.ClientVerificationStep;
import ru.stoloto.repositories.maria.UserOutDAO;
import ru.stoloto.repositories.ms.ClientInDAO;
import ru.stoloto.repositories.ms.VerificationStepDAO;
import ru.stoloto.service.Converter;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@Transactional
@DisplayName("Тестируем Service-слой  \uD83D\uDC7F")
//@Disabled
class ServiceTest {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    Converter converter;
    @Autowired
    UserOutDAO userOutDAO;
    @Autowired
    ClientInDAO repositoryMsSql;
    @Autowired
    VerificationStepDAO verificationStepDAO;


    @Qualifier("jdbcMaria")
    @Autowired
    JdbcTemplate jdbcTemplateMaria;
    @Qualifier("jdbcMsSql")
    @Autowired
    JdbcTemplate jdbcTemplateMsSql;

    private static Client client;
    private static boolean isSaved;

    private static Stream<Integer> makeIDsForClientTableToGet() {
        return Stream.of(11571919, 11591672, 11595571, 11812258, 55308090, 22225320, 11563150, 11701132);
    }

//    @BeforeAll
//    static void initClient_for_Ms_Sql_DB() {
//        logger.info("Перед каждым тестом создаем Клиента с id = 1. \n");
//        Client clientToSave = random(Client.class);
//        clientToSave.setId(1);
//        client = clientToSave;
//    }

//    @BeforeEach
//    void insertClient_for_Ms_Sql_DB_with_id__1__() {
//        if (!isSaved) {
//            System.out.println("Сохраняем Юзера с id = 1");
//            repositoryMsSql.saveAndFlush(client);
//            isSaved = true;
//        }
//    }


    @DisplayName("\uD83D\uDD25 Конверсия Client -> user")
    @ParameterizedTest(name = "Тест #{index} для ID № [{arguments}]")
    @MethodSource("makeIDsForClientTableToGet")
    void firstConvert(int id) {
        Optional<Client> person = repositoryMsSql.findById(id);
        Client client = person.get();
        System.err.println(client);

        UserRebased userRebased = converter.convertReplacaUser(client);
        userOutDAO.saveAndFlush(userRebased);
        System.out.println("Успешная конвертация и сохранение в БД.");
    }

    @Test
    @DisplayName("Получаем все регионы")
    void getAllRegions() {
        Set<Integer> allRegions = repositoryMsSql.getAllRegions();
        System.out.println(">>> Кол-во найденных регионов: " + allRegions.size() + " шт");
        System.err.println(allRegions);
//        98шт регионов
        //  null, 1, 2, 8, 10, 11, 12, 13, 14, 15, 19, 21, 22, 23, 27, 28, 30, 31, 42, 45, 48, 53, 66,
        //  68, 73, 78, 83, 89, 90, 91, 93, 100, 108, 110, 112, 113, 114, 116, 117, 118, 122, 124, 125, 128,
        //  130, 136, 149, 153, 154, 155, 159, 161, 168, 169, 175, 179, 188, 189, 190, 191, 195, 204, 205, 213,
        //  219, 224, 225, 233, 234, 235, 237, 239, 240, 242, 245, 246, 248, 633, 1638, 1639, 1640, 1648, 1707,
        //  1713, 1720, 1722, 1723, 1726, 1727, 1728, 1729, 1730, 1732, 1734, 1735, 1737, 1787, 1877
    }

    @Test
    @DisplayName("Проверка кол-ва записей  [jdbcTemplate} - \uD83C")
    void getCount_from_ms_sql_jdbc() {
        Integer countOfRecords = jdbcTemplateMsSql.queryForObject("SELECT COUNT(*) FROM dbo.Client", Integer.class);
        System.err.println("Соединениу с БД установлено. Кол-во записей: " + countOfRecords);
    }

    @ParameterizedTest(name = "Тест #{index} для ID № [{arguments}]")
    @MethodSource("makeIDsForClientTableToGet")
    @DisplayName("SELECT User from Sql-Server By ID")
    void getPersonFromMaria(int id) {
        Optional<Client> person = repositoryMsSql.findById(id);
        Client client = person.get();
        System.out.println("Найденный User: \n");
        person.ifPresent((x) -> System.err.println("OK. Найденное значение - " + x));
    }

    @Test
    @DisplayName("Проверка кол-ва записей JPA")
    void getCount_from_ms_sql_jpa() {
        Long countOfRecords = repositoryMsSql.selectCount();
        System.err.println("Соединениу с БД установлено. Кол-во записей: " + countOfRecords);
    }

    @ParameterizedTest(name = "Тест #{index} для ID № [{arguments}]")
    @ValueSource(ints = {11486046, 11523437, 55238717, 11446392, 11701132})
    @DisplayName("Проверка таблицы clientVerificationStep")
    void clientVerificationStep(int id) {
        int count = verificationStepDAO.selectCount();
        System.err.println("Соединение с базой ClientVerificationStep Установлено. Кол-во записей = " + count);

        List<Integer> registrationStage = verificationStepDAO.getRegistrationStages(id);
        System.err.println(registrationStage);

        Integer maxRegistrationStages = verificationStepDAO.getMaxRegistrationStages(id);
        System.err.println(maxRegistrationStages);

    }

    @ParameterizedTest(name = "Тест #{index} для ID № [{arguments}]")
    @ValueSource(ints = {11486046, 11523437, 55238717, 11446392, 11701132})
    @Disabled
    @SuppressWarnings("Нерабочий")
    void getVerificationStepDao(int id) {

        ClientVerificationStep maxVerificationStepObject = verificationStepDAO.getMaxVerificationStepObject(id);
        System.err.println(maxVerificationStepObject);

        Optional<Integer> partnerKycStepId = Optional.ofNullable(maxVerificationStepObject.getPartnerKycStepId());
        System.err.println(maxVerificationStepObject.getPartnerKycStepId());
    }


//    @AfterAll
//    static void afterAll() {
//        isSaved = false;
//    }
}
