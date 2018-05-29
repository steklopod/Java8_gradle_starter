package ru.steklopod.later.statistics;

import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.entities.mariadb.UserRebased;
import ru.stoloto.entities.mssql.Client;
import ru.stoloto.repositories.maria.UserOutDAO;
import ru.stoloto.repositories.ms.ClientInDAO;
import ru.stoloto.service.Converter;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@Transactional
@DisplayName("Тесты конвертации типов")
@Disabled
class Statistic {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private Converter converter;
    @Autowired
    private UserOutDAO userOutDAO;
    @Autowired
    private ClientInDAO clientInDAO;
    @Qualifier("jdbcMsSql")
    @Autowired
    private JdbcTemplate jdbcTemplateMsSql;
    @Qualifier("jdbcMaria")
    @Autowired
    private JdbcTemplate jdbcTemplateMaria;

    @Value("${csv.filename}")
    private String filename;

    private static volatile Set<Long> idsFromBET;
    private static volatile Set<Long> idsFromClient;
    private static volatile Set<Integer> betIdsWith98;

    private static volatile Set<String> generalEmails;
    private static volatile Set<String> generalLogins;
    private static volatile Set<Integer> idsSet;
    private static volatile Set<Integer> idsWith10StateInBet;

    private static volatile Map<String, Integer> generalPhones;
    private static HashMap<Long, String> regStepsOfTsupisHashMap = new HashMap<>();


    private void checkAndPrint10(Predicate predicate) {
        System.err.println("test users: ");
        clientInDAO
                .findAll()
                .stream()
                .filter(predicate)
                .limit(10)
                .forEach(System.out::println);
    }

    @BeforeEach
    void подготовка() throws InterruptedException {
//        ExecutorService executor = new ForkJoinPool();
//        List<Callable<Void>> taskList = getCallablesInitTasks();
        System.err.println("* ИДЕТ ПОДГОТОВКА *");
//        executor.invokeAll(taskList);
//        executor.shutdown();
        fillHasMapOfSteps();
        System.err.println("* Подготовка закончена > OK <");
    }

    @Test
    @Rollback(false)
    void считаемКлиентовКоторымНеобходимоУдалитьПерсональныеДанные() {
        List<Long> idsBet = jdbcTemplateMsSql.queryForList("SELECT DISTINCT ClientId FROM Bet", Long.class);
        idsFromBET = new HashSet<>(idsBet);
        System.err.println("Клиентов в таблице BET: " + idsFromBET.size());

        List<Long> idsClient = jdbcTemplateMsSql.queryForList("SELECT DISTINCT Id FROM Client", Long.class);
        idsFromClient = new HashSet<>(idsClient);
        System.err.println("Клиентов всего: " + idsFromClient.size());

        idsFromClient.removeAll(idsFromBET);
        System.err.println("Клиентов без ставок: " + idsFromClient.size());

        HashSet<Long> tsupisIds = new HashSet<>();
        regStepsOfTsupisHashMap.forEach((key, value) -> tsupisIds.add(key));

        idsFromClient.removeAll(tsupisIds);

        System.err.println("Клиентов без ставок, не содержатся в выгрузке цупис: " + idsFromClient.size());

        idsFromClient
                .forEach(id ->
                        jdbcTemplateMaria
                                .update("INSERT INTO test.ids_to_delete_person (id) VALUES (?)", id));
    }


    public void fillHasMapOfSteps() {
        File file = new File(MethodHandles
                .lookup()
                .lookupClass()
                .getClassLoader().getResource(filename).getFile());
        try (Stream<String> stream = Files.lines(file.toPath())) {
            stream
                    .skip(1)
                    .forEach(row -> {
                        String[] strings = row.split(";");
                        Long customerId = Long.parseLong(strings[0]);
                        String step = strings[1];
                        regStepsOfTsupisHashMap.put(customerId, step);
                    });
        } catch (NullPointerException | IOException e) {
            throw new RuntimeException("Ooops... CSV File >>> " + filename + "<<< NOT FOUND \n :-(. " +
                    "\n Check the filename in application.yml \n look at csv.filename: ...");
        }
    }

    @Test
    void passports() {
        Function<UserRebased, String> f = UserRebased::getPassportSeries;
//        Predicate isTest = (client -> isTest((Client) client));
        user10(f);
    }

    private void user10(Function f) {
        System.err.println("test users: ");
        List<Long> ids
                = jdbcTemplateMaria.queryForList("SELECT DISTINCT customer_id FROM ids", Long.class);

        ids.stream()
                .map(id -> userOutDAO.findByIdMy(id.intValue()))
//                .map(user -> user.getPassportSeries())
                .map(f)
//                .filter(x -> x.length)
                .limit(10)
                .forEach(System.out::println);
    }

    @Test
    void sameId() {
        System.err.println("same ids: ");
        Predicate sameId = (client -> isContainsId((Client) client));
        checkAndPrint10(sameId);
    }

    @Test
    @DisplayName("-[pps] source in Bet 98-99 :")
    void countOfUsersWith98Source() {
        System.err.println("-[pps] source in Bet 98-99 : ");
        Predicate notNullCashDeskId = (client -> notNullCashDeskId((Client) client));
        checkAndPrint10(notNullCashDeskId);
    }

    @Test
    @DisplayName("TEST CLIENTS")
    void testUsers() {
        Predicate isTest = (client -> isTest((Client) client));
        checkAndPrint10(isTest);
    }

    @Test
    @DisplayName("empty emails")
    void emptyEmails() {
        System.err.println("empty emails:  ");
        Predicate emptyEmails = (client -> isEmptyEmail((Client) client));
        checkAndPrint10(emptyEmails);
    }

    @Test
    @DisplayName("EMPTY EMAILS with bets")
    void emptyEmailsWithBets() {
        System.err.println("empty emails with bets:  ");
        Predicate isEmptyEmail = (client -> isEmptyEmail((Client) client));
        checkAndPrint10(isEmptyEmail);
    }

    @Test
    @DisplayName("EMPTY PHONES")
    void emptyPhone() {
        System.err.println("empty phone:  ");
        Predicate emptyPhone = (client -> isEmptyPhone((Client) client));
        checkAndPrint10(emptyPhone);
    }

    @Test
    @DisplayName("*EMPTY PHONES With Bets")
    void emptyPhoneWithBets() {
        System.err.println("empty phone:  ");
        Predicate isEmptyPhoneWithBets = (client -> isEmptyPhoneWithBets((Client) client));
        checkAndPrint10(isEmptyPhoneWithBets);
    }

    @Test
    @DisplayName("*same phones")
    void samePhones() {
        System.err.println("same phones:  ");
        Predicate isPhoneExist = (client -> isPhoneExist((Client) client));
        checkAndPrint10(isPhoneExist);
    }

    @Test
    @DisplayName("*not confirmed email with bets")
    void notConfirmedEmailWithBets() {
        Stream.of(12823130, 12715060, 11563105, 11563143, 11563150, 11563174, 11563679, 11563686, 11563693, 1156374)
                .map(x -> userOutDAO.findByIdMy(x))
                .forEach(System.out::println);
    }

    @Test
    @DisplayName("*deleted personal data")
    void deletedPersonalData() {
        Stream.of(20973964, 16097865, 14531255, 15071729, 12059065)
                .map(x -> userOutDAO.findByIdMy(x))
                .forEach(System.out::println);
    }


    private static boolean isPhoneExist(Client client) {
        boolean isContains = false;
        Optional<String> phone = Optional.ofNullable(client.getPhone());
        if (phone.isPresent()) {
            if (getgeneralPhones().containsKey(phone.get())) {
                isContains = true;
            }
        }
        return isContains;
    }

    private static synchronized Map<String, Integer> getgeneralPhones() {
        return generalPhones;
    }

    private static boolean isEmptyPhone(Client client) {
        boolean isContains = false;
        Optional<String> phone = Optional.ofNullable(client.getPhone());
        if (!phone.isPresent() || phone.get().equals("")) {
            isContains = true;
        }
        return isContains;
    }

    private static boolean isEmptyPhoneWithBets(Client client) {
        boolean isContains = false;
        Integer id = client.getId();
        Optional<String> phone = Optional.ofNullable(client.getPhone());
        if (!phone.isPresent() || phone.get().equals("")) {
            if (idsFromBET.contains((Long) id.longValue())) {
                isContains = true;
            }
        }
        return isContains;
    }


    private static boolean isEmptyEmail(Client client) {
        boolean isContains = false;
        Integer id = client.getId();
        Optional<String> email = Optional.ofNullable(client.getEmail());
        if (!email.isPresent() || email.get().equals("")) {
            isContains = true;
            if (idsFromBET.contains(id.longValue())) {
                System.err.println("WITH BETS: \n" + client); //со ставками
            }
        }
        return isContains;
    }


    private static boolean isContainsId(Client client) {
        boolean isContains = false;
        boolean contains =
                getIdsSet()
                        .contains
                                (BigInteger.valueOf(client.getId().intValue()));
        if (contains) {
            isContains = true;
        }
        return isContains;
    }

    private static synchronized Set<Integer> getIdsSet() {
        return idsSet;
    }


    private static boolean notNullCashDeskId(Client client) {
        boolean isContains = false;
        Integer id = client.getId();
        if (betIdsWith98.contains(id) && id != null) {
            isContains = true;
        }
        return isContains;
    }


    private static boolean isTest(Client client) {
        boolean isContains = false;
        if (client.isTest()) {

            isContains = true;
//            System.out.println("isTest - " + client.getId());

        }
        return isContains;
    }

    /**
     * @note Проверка на Email
     */

    @Test
    @Ignore
    @DisplayName("not confirmed emails without bets")
    void notConfirmedEmails() {
        System.err.println("same ids: ");
// TODO
    }


    private List<Callable<Void>> getCallablesInitTasks() {
        Callable<Void> betIDS = () -> {
            List<Long> idsList = jdbcTemplateMsSql.queryForList("SELECT DISTINCT ClientId FROM Bet", Long.class);
            idsFromBET = new HashSet<>(idsList);
            idsList.clear();
            System.out.println("Собран список клиентов из BET");
            return null;
        };
        Callable<Void> betIdsPPS = () -> {
            List<Integer> idsList = jdbcTemplateMsSql.queryForList("SELECT DISTINCT ClientId FROM Bet WHERE Source IN(98,99)", Integer.class);
            betIdsWith98 = new HashSet<>(idsList);
            idsList.clear();
            System.out.println("Собран список клиентов Source IN(98,99) - " + betIdsWith98.size() + " записей");
            return null;
        };
        Callable<Void> idsWith10 = () -> {
            List<Integer> idsWith10State
                    = jdbcTemplateMsSql.queryForList("SELECT DISTINCT ClientId FROM ClientVerificationStep WHERE  State = 10", Integer.class);
            idsWith10StateInBet = new HashSet<>(idsWith10State);
            idsWith10State.clear();
            System.out.println("Собран список клиентов WHERE  State = 10 - " + idsWith10State.size() + " записей");
            return null;
        };
        Callable<Void> emailsTask = () -> {
            generalEmails = new HashSet<>(userOutDAO.findAllEmails());
            System.out.println("Собран список Emails");

            return null;
        };
        Callable<Void> phonesTask = () -> {
            generalPhones = new HashMap<>();
            userOutDAO.findAllPhones().stream().forEach(p -> generalPhones.put(p, 1));
            System.out.println("Собран список Phones");
            return null;
        };
        Callable<Void> loginTask = () -> {
            generalLogins = new HashSet<>(userOutDAO.findAllLogins());
            System.out.println("Собран список Logins");
            return null;
        };
        Callable<Void> sameIds = () -> {
            idsSet = new HashSet<>(userOutDAO.findAllIds());
            System.out.println("Собран список ids");
            return null;
        };
        Callable<Void> ddlTask = () -> {
            userOutDAO.skipForeignKey();
            return null;
        };
        return Arrays.asList(idsWith10, emailsTask, phonesTask, loginTask, ddlTask, betIDS, sameIds, betIdsPPS);
    }
}
