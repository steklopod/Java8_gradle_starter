package ru.stoloto;

import org.junit.jupiter.api.DisplayName;
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
import ru.stoloto.entities.mariadb.UserRebased;
import ru.stoloto.entities.mariadb.UserWithException;
import ru.stoloto.entities.mssql.Client;
import ru.stoloto.repositories.maria.UserOutDAO;
import ru.stoloto.repositories.ms.ClientInDAO;
import ru.stoloto.repositories.ms.VerificationStepDAO;
import ru.stoloto.service.Converter;

import java.lang.invoke.MethodHandles;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Stream;

import static org.springframework.test.util.AssertionErrors.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@Transactional
@DisplayName("Тестируем Service-слой  \uD83D\uDC7F")
//@Disabled
class ServiceTest {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private Converter converter;
    @Autowired
    private UserOutDAO userOutDAO;
    //    @Autowired
//    private BetDAO betDao;
    @Autowired
    private VerificationStepDAO verificationStepDAO;
    @Autowired
    private ClientInDAO repositoryMsSql;
    @Qualifier("jdbcMsSql")
    @Autowired
    private JdbcTemplate jdbcTemplateMsSql;

    public static volatile long count = 0;
    private static volatile boolean isContinue = true;
    public static volatile long countOfExistingUsers;
    public static volatile long countOfUsersToRebase;

    private volatile static HashSet<String> generalEmails;
    private volatile static HashSet<String> generalPhones;

    private volatile static HashSet<Integer> idsFromBetRetained;

    int countOfS = 0;
    int countOfSs = 0;
    int countOfSss = 0;

    private static HashSet<String> allPhonesToRebase;
    private static HashSet<String> allEmailsToRebase;

    private static Stream<Integer> makeIDs() {
        return Stream.of(
                55308090, 22225320, 55238717,
                11595571, 11701132, 11446392
                , 11486046, 11523437, 11571919, 11591672, 11812258, 11563150
        );
    }

    private static Stream<Integer> makeIDExistInBet() {
        return Stream.of(
                188578, 191343, 191358, 191688, 198913, 198988, 200023, 211158, 220130, 220135, 223525, 236095, 236100, 237105, 237145, 237425, 241045, 241090
        );
    }

    private static Stream<String> makeEmails() {
        return Stream.of(
                "ollabergan.muratov@mail.ru", "stephen.volkov@yandex.ru", "stephen.volkov@yandex.ru"
        );
    }

    //        HashSet<Integer> finalIds = repositoryMsSql.findAllIds();
//        System.err.println("ids BetCon. ID - " + finalIds.size() + " шт");
//        idsMaria.retainAll(finalIds);
//        System.err.println("ВСЕГО общих: " + finalIds.size());


    @Test
    void uniqueIds() {
        Integer id = 35393009;
        Set<Integer> idsMaria
                = new HashSet<>(userOutDAO.findAllIds());

        System.out.println("ids Maria ID - " + idsMaria.size() + " шт");

        Optional<Client> client = repositoryMsSql.findById(id);
        Optional<UserRebased> user = Optional.ofNullable(userOutDAO.findByIdMy(id));

        assertTrue("НАШЛИ КЛИЕНТА: ", client.isPresent());
        assertTrue("НАШЛИ USER: ", user.isPresent());

        Integer clientID = client.get().getId();
        Long customerId = user.get().getCustomerId();


        assertNotEquals("Не ноль clientID", clientID, null);
        assertNotEquals("Не  customerId", customerId, null);
        assertEquals("РАВНЫ ", clientID, (int) (long) customerId);

        System.err.println("clientID: " + clientID);
        System.err.println("customerId: " + customerId);

//     idsMaria.stream().limit(1).forEach(x -> System.out.println("idsMaria customer_id: " + x.getClass()));
//     System.out.println("clientID: " + clientID.getClass());

        if (idsMaria.contains(BigInteger.valueOf(clientID))) {
            System.out.println("Найдено clientID: " + clientID);
        } else {
            throw new RuntimeException("НЕ найдено");
        }
    }

    @Test
    void map() {
        Map<Integer, UserWithException> map = new HashMap<>();
        UserWithException userWithException = new UserWithException(2);
        Long aLong = new Long(2);
        Integer i =  aLong.intValue();

        map.put(i, userWithException);
        UserWithException userInMap = map.get(i);
        assertEquals("ОДИНАКОВЫЕ: ", userWithException, userInMap);

        Boolean isLoginExistBefore = userInMap.getIsLoginExist();

        userInMap.setIsLoginExist(true);

        map.put(userInMap.getId(), userInMap);
        assertEquals("ОДИН ЭЛЕМЕНТ: ", map.size(), 1);

        Boolean isLoginExistAfter = map.get(userInMap.getId()).getIsLoginExist();
        assertEquals("Изменен login: ", isLoginExistAfter, true);

    }


    @ParameterizedTest(name = "Тест #{index} для ID № [{arguments}]")
    @MethodSource("makeEmails")
    void uniqueEmails(String email) {
        HashSet<Integer> idsMaria
                = new HashSet<>(userOutDAO.findAllIds());

        System.out.println("ids Maria ID - " + idsMaria.size() + " шт");
        System.out.println(email);

        Client client = repositoryMsSql.findByEmail(email);
        UserRebased user = userOutDAO.findByEmail(email);

        Integer clientID = client.getId();
        Integer customerId = (int) (long) user.getCustomerId();

        assertNotEquals("Не ноль clientID", clientID, null);
        assertNotEquals("Не  customerId", customerId, null);
        assertEquals("РАВНЫ ", clientID, customerId);

        System.err.println("clientID: " + clientID);
        System.err.println("customerId: " + customerId);


        if (idsMaria.contains(clientID)) {
            System.out.println("Найдено.");
        } else {
            System.err.println("НЕ найдено");
        }

//        assertTrue("НАЙДЕНО ms ?", idsMaria.contains(clientID));
//        assertTrue("НАЙДЕНО my ?", idsMaria.contains(clientID));
    }

    //    @BeforeEach
    void init() {
        Integer count = jdbcTemplateMsSql.queryForObject("SELECT COUNT(*) FROM Bet", Integer.class);
        System.err.println(count);

        List<Integer> idsList = jdbcTemplateMsSql.queryForList("SELECT Id FROM Bet", Integer.class);
        idsFromBetRetained = new HashSet<>(idsList);
        System.err.println("idsFromBetRetained ДО: " + idsFromBetRetained.size());
        Set<Integer> allIds = userOutDAO.findAllIds();
        idsFromBetRetained.retainAll(allIds);

        System.err.println("idsFromBetRetained ПОСЛЕ: " + idsFromBetRetained.size());


        generalEmails = userOutDAO.findAllEmails();
        HashSet<String> allEmailsToRebase = repositoryMsSql.findAllEmails();
        generalEmails.retainAll(allEmailsToRebase);
        logger.info("повторных email: " + generalEmails.size() + " шт.");
    }


    @Test
    void sameLoginsCount() {
        HashSet<String> sList = new HashSet<>();
        HashSet<String> ssList = new HashSet<>();
        HashSet<String> sssList = new HashSet<>();

        List<Client> all = repositoryMsSql.findAll();
        all.forEach(x -> {
            String p = x.getLogin();
            if (!sList.contains(p)) {
                sList.add(p);
            } else {
                countOfS++;
            }
        });
        all.forEach(x -> {
            String p = x.getPhone();
            if (!ssList.contains(p)) {
                ssList.add(p);
            } else {
                countOfSs++;
            }
        });
        all.forEach(x -> {
            String p = x.getEmail();
            if (!sssList.contains(p)) {
                sssList.add(p);
            } else {
                countOfSss++;
            }
        });

        System.err.println(" ЮЗЕРОВ С ПОВТОРНЫМИ ЛОГИНАМИ " + countOfS);
        System.err.println(" ЮЗЕРОВ С ПОВТОРНЫМИ ТЕЛЕФОНАМИ " + countOfSs);
        System.err.println(" ЮЗЕРОВ С ПОВТОРНЫМИ ЕМЕЙЛАМИ " + countOfSss);

    }

    @ParameterizedTest(name = "Тест #{index} для ID № [{arguments}]")
    @MethodSource("makeIDExistInBet")
    void testBetTable(int id) {
        if (idsFromBetRetained.contains(id)) {
            System.err.println("НАШЛИ: " + id);
        } else {
            throw new RuntimeException("Такого id нет в таблице BetConstract");
        }


    }

    private boolean isEmailExist(HashSet<String> generalEmails, String email) {
        boolean b = false;
        if (generalEmails.contains(email.toLowerCase())) {
            b = true;
            System.err.println("СУЩЕСТВУЕТ, " + b);
        }
        return b;
    }


    @Test
    @DisplayName(" 😱 ")
    void Emails() {
        HashSet<String> generalLogins = new HashSet<>();
        userOutDAO.findAllLogins().forEach(x -> generalLogins.add(x.toLowerCase()));

        HashSet<String> allLoginsToRebase = new HashSet<>();
        repositoryMsSql.findAllLogins().forEach(x -> allLoginsToRebase.add(x.toLowerCase()));

        boolean emailExistIn = isEmailExist(generalLogins, "Natasha15021@yandex.ru");
        assertTrue("IN", emailExistIn);

        boolean emailExistOut = isEmailExist(allLoginsToRebase, "Natasha15021@yandex.ru");
        assertTrue("OUT", emailExistOut);

        generalLogins.retainAll(allLoginsToRebase);

        long sameLoginsCount = generalLogins.size();

        System.err.println("Кол-во повторных email: " + sameLoginsCount + " шт.");

        boolean emailExist = isEmailExist(generalLogins, "Natasha15021@yandex.ru");
        assertTrue("Общий", emailExist);


    }


    @DisplayName(" 😱 Конверсия Client -> user")
    @ParameterizedTest(name = "Тест #{index} для ID № [{arguments}]")
    @MethodSource("makeIDs")
    void convert(int id) {
        Optional<Client> person = repositoryMsSql.findById(id);
        Client client = person.get();
        System.err.println(client);

        Optional<UserRebased> userRebased = Optional.ofNullable(converter.convertUserForRebase(client));
        boolean present = userRebased.isPresent();

//        System.err.println("isPresent: " + present);
//        if (present) {
//            UserRebased userRebased2 = userRebased.get();
//            if (isEmailExist(userRebased2.getEmail())) {
//                System.err.println("Сохраняем ...");
//
//                userOutDAO.saveAndFlush(userRebased2);
//                System.err.println("Сохранили");
//            }
//        } else {
//            System.err.println("Не найден");
//        }
    }


    @Test
    void runMain() {
        Long countOfUsersToRebase = repositoryMsSql.selectCount();
        logger.info("Начинается перенос пользователей из реплики БД BetConstruct. ");
        logger.info("Кол-во записей для переноса:  " + countOfUsersToRebase);

        try (Stream<Client> clientStream = repositoryMsSql
                .findAll()
                .stream()
                .parallel()
                .limit(100)
        ) {
            clientStream.forEach(x -> converter.convertUserForRebase(x));
        }

        Long countOfRebasedUsers = userOutDAO.selectCountOfUsers();
        logger.info(">>> Ok. Перенос завершен успешно.");
        logger.info("Кол-во записей для переноса:  " + countOfRebasedUsers);
    }


//    @Autowired TestRestTemplate restTemplate;

//    @Test
//    @Disabled
//    void testAbout() {
//        String message = this.restTemplate.getForObject("/about", String.class);
//        assertEquals("TEST SUCCESFUL", message);
//    }
}
