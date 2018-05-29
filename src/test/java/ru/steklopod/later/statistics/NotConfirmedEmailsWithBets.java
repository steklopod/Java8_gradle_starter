package ru.steklopod.later.statistics;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.entities.mssql.Client;
import ru.stoloto.repositories.maria.UserOutDAO;
import ru.stoloto.repositories.ms.ClientInDAO;
import ru.stoloto.service.Converter;

import java.lang.invoke.MethodHandles;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@Transactional
@DisplayName("Тесты конвертации типов")
@Disabled
class NotConfirmedEmailsWithBets {
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

    private static volatile Set<Long> idsFromBET;
    private static volatile Set<Integer> betIdsWith98;

    private static volatile Set<String> generalEmails;
    private static volatile Set<String> generalLogins;
    private static volatile Set<Integer> idsSet;
    private static volatile Set<Integer> idsWith10StateInBet;
    private static volatile Set<Integer> idsFromEmailChecking;

    private static volatile Map<String, Integer> generalPhones;
    private static volatile Map<Integer, Timestamp> idsOfCreationData;
    private static volatile Map<Integer, String> regionsAlpha3Diction;
    private static volatile Map<Integer, Integer> steps;

    @BeforeEach
    void init() throws InterruptedException {
        ExecutorService executor = new ForkJoinPool();
        List<Callable<Void>> taskList = getCallablesInitTasks();
        System.err.println("* ИДЕТ ПОДГОТОВКА *");
        executor.invokeAll(taskList);
        executor.shutdown();
        System.err.println("* Подготовка закончена > OK <");
    }

    @Test
    void idsWith10StateInBet() {
        System.err.println("Клиенты со статусом 10 и ставками:");
        List<Client> collect =
                clientInDAO
                        .findAll()
                        .stream()
                        .filter(NotConfirmedEmailsWithBets::isContainsId)
                        .limit(10)
                        .peek(System.out::println)
                        .collect(Collectors.toList());
        assertEquals(collect.size(), 10);
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
            System.out.println("Собран список клиентов Source IN(98,99)");
            return null;
        };
        Callable<Void> idsWith10 = () -> {
            List<Integer> idsWith10State
                    = jdbcTemplateMsSql.queryForList("SELECT DISTINCT ClientId FROM ClientVerificationStep WHERE  State = 10", Integer.class);
            idsWith10StateInBet = new HashSet<>(idsWith10State);
            idsWith10State.clear();
            System.out.println("Собран список клиентов WHERE  State = 10");
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
