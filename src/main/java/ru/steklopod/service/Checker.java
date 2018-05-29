package ru.steklopod.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.steklopod.entities.mariadb.UserRebased;
import ru.steklopod.entities.mssql.Client;

import java.math.BigInteger;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.steklopod.Starter.*;

@Service
@Slf4j
public class Checker {
    public static AtomicInteger countOfSameId = new AtomicInteger(0);
    public static AtomicInteger countOfTestUsers = new AtomicInteger(0);
    public static AtomicInteger countOfSamePhones = new AtomicInteger(0);
    public static AtomicInteger countOfSameEmails = new AtomicInteger(0);
    public static AtomicInteger countOfSameLogins = new AtomicInteger(0);
    public static AtomicInteger countOfEmptyEmails = new AtomicInteger(0);
    public static AtomicInteger countOfEmptyPhones = new AtomicInteger(0);
    public static AtomicInteger countOfUsersWith98Source = new AtomicInteger(0);
    public static AtomicInteger notConfirmedEmailCounter = new AtomicInteger(0);
    public static AtomicInteger notConfirmedEmailWithBets = new AtomicInteger(0);
    public static AtomicInteger countOfEmptyEmailsWithBets = new AtomicInteger(0);
    public static AtomicInteger countOfEmptyPhonesWithBets = new AtomicInteger(0);

    static synchronized boolean isTrash(Client client) {
        boolean isTrash = false;
        if (notNullCashDeskId(client)
                || isEmptyEmail(client)
                || isEmailExist(client)
                || isContainsId(client)
                || isTest(client)
                || isLoginExist(client)) {
            isTrash = true;
        }
        return isTrash;
    }

//  Счетчики, не влияющие на перенос:
    static void makeCountList(Client client) {
        notNullCashDeskId2(client);
        isEmptyEmail2(client);
        isContainsId2(client);
        isTest2(client);
        isEmailExist2(client);
        isLoginExist2(client);
    }


    /**
     * НЕПОДТВЕРЖДЕННЫЙ EMAIL:
     */
    static boolean checkNotConfirmedEmail(UserRebased userRebased) {
        boolean isNotConfirmed = false;
        if (notConfirmedEmails.contains(userRebased.getCustomerId().intValue())) {
            notConfirmedEmailCounter.getAndIncrement();
            isNotConfirmed = true;
        }
        return isNotConfirmed;
    }

    /**
     * ПОВТОР EMAIL:
     */
    private static boolean isEmailExist(Client client) {
        boolean isContainsEmail = false;
        Optional<String> email = Optional.ofNullable(client.getEmail());
        if (email.isPresent() && getgeneralEmails().contains(email.get())) {
            countOfSameEmails.getAndIncrement();
            isContainsEmail = true;
        }
        return isContainsEmail;
    }

    /**
     * ПУСТОЙ EMAIL:
     */
    private static boolean isEmptyEmail(Client client) {
        boolean isEmpty = false;
        Integer id = client.getId();
        Optional<String> email = Optional.ofNullable(client.getEmail());
        if (!email.isPresent() || email.get().equals("")) {
            countOfEmptyEmails.getAndIncrement();
            isEmpty = true;
            if (idsFromBET.contains(id.longValue())) {
                countOfEmptyEmailsWithBets.getAndIncrement();
            }
        }
        return isEmpty;
    }


    /**
     * ПОВТОРНЫЙ ID
     */
    private static boolean isContainsId(Client client) {
        boolean isContainsId = false;
        boolean contains =
                getIdsSet()
                        .contains
                                (BigInteger.valueOf(client.getId().intValue()));
        if (contains) {
            isContainsId = true;
            countOfSameId.getAndIncrement();
        }
        return isContainsId;
    }

    /**
     * ПРОВЕРКА ТЕЛЕФОНА:
     */
//    @Synchronized
    static synchronized void checkPhone(Client client) {
        Optional<String> phone = Optional.ofNullable(client.getPhone());
        if (phone.isPresent() && phone.get() != "") {
            String phoneNumber = phone.get();
            Optional<Integer> howManyTimesValue
                    = Optional.ofNullable(getgeneralPhones().get(client.getPhone()));

            howManyTimesValue.ifPresent(howMany -> {
                        countOfSamePhones.getAndIncrement();
                        howMany += 1;
                        getgeneralPhones().put(phoneNumber, howMany);
                        client.setPhone
                                (String.valueOf
                                        (phoneNumber + "(" + (howMany) + ")")
                                );
                    }
            );
            if (!howManyTimesValue.isPresent()) {
                putFirstTimePhone(String.valueOf(phoneNumber));
            }
        } else {
            if (idsFromBET.contains((Long) client.getId().longValue())) {
                emptyPhonesWithBets.getAndIncrement();
            }
            int i = countOfEmptyPhones.incrementAndGet();
            client.setPhone("(" + i + ")");
        }
    }

    /**
     * ПОВТОР ЛОГИНА:
     */
    private static boolean isLoginExist(Client client) {
        String login = client.getLogin();
        boolean isContains = false;
        if (getGeneralLogins().contains(login)) {
            countOfSameLogins.getAndIncrement();
            isContains = true;
        }
        return isContains;
    }

    /**
     * ТЕСТОВЫЙ ЛИ ЮЗЕР:
     */
    private static boolean isTest(Client client) {
        boolean isContains = false;
        if (client.isTest()) {
            countOfTestUsers.getAndIncrement();
            isContains = true;
        }
        return isContains;
    }

    /**
     * НЕНУЛЕВОЙ CashDeskId ~Source IN(98,99)~:
     */
    private static boolean notNullCashDeskId(Client client) {
        boolean isPPS = false;
        Integer id = client.getId();
        if (betIdsWith98.contains(id)) {
            isPPS = true;
            countOfUsersWith98Source.getAndIncrement();
        }
        return isPPS;
    }

    /**
     * 1. Не подтвердившие емайл
     * 2. Без ставок
     */
    static boolean isNotConfirmedEmailWithBets(UserRebased user) {
        boolean b = false;
        Long customerId = user.getCustomerId();
        if (notConfirmedEmails.contains(customerId)) {
            b = true;
        }
        return b;
    }


    /**
     * С Ч Е Т Ч И К И  *
     */
    private static void isTest2(Client client) {
        Integer id = client.getId();
        if (client.isTest()) {
            testUsers.getAndIncrement();
        }
    }

    private static void isEmptyEmail2(Client client) {
        Integer id = client.getId();
        Optional<String> email = Optional.ofNullable(client.getEmail());
        if (!email.isPresent() || email.get().equals("")) {
            emptyEmails.getAndIncrement();
            if (idsFromBET.contains(id.longValue())) {
                emptyEmailsWithBets.getAndIncrement();
            }
        }
    }

    private static void notNullCashDeskId2(Client client) {
        if (betIdsWith98.contains(client.getId())) {
            usersWith98Source.getAndIncrement();
        }
    }

    private static void isContainsId2(Client client) {
        boolean contains =
                getIdsSet()
                        .contains
                                (BigInteger.valueOf(client.getId().intValue()));
        if (contains) {
            sameId.getAndIncrement();
        }
    }

    private static void isEmailExist2(Client client) {
        Optional<String> email = Optional.ofNullable(client.getEmail());
        if (email.isPresent() && getgeneralEmails().contains(email.get())) {
            sameEmails.getAndIncrement();
        }
    }

    private static void isLoginExist2(Client client) {
        String login = client.getLogin();
        if (getGeneralLogins().contains(login)) {
            sameLogins.getAndIncrement();
        }
    }

    private static AtomicInteger usersWith98Source = new AtomicInteger(0);
    private static AtomicInteger emptyEmails = new AtomicInteger(0);
    private static AtomicInteger testUsers = new AtomicInteger(0);
    private static AtomicInteger sameId = new AtomicInteger(0);
    private static AtomicInteger sameEmails = new AtomicInteger(0);
    private static AtomicInteger sameLogins = new AtomicInteger(0);
    private static AtomicInteger notconfirmedemails = new AtomicInteger(0);
    private static AtomicInteger emptyEmailsWithBets = new AtomicInteger(0);
    private static AtomicInteger emptyPhonesWithBets = new AtomicInteger(0);

    public static void makeLoggingOfNotUnique() {
        Thread.currentThread().setName("Checker");

        log.info("\n");
        log.info("STATISTIC FOR NOT UNIQUES (WITH INTERSECTIONS):");
        log.info("   /same idsWith10StateInBet     : " + sameId);
        log.info("   /not confirmed emails without bets : " + notconfirmedemails);
        log.info("-----------------");
        log.info("   /[pps]  98-99 : " + usersWith98Source);
        log.info("   /test users   : " + testUsers);
        log.info("   /empty emails : " + emptyEmails);
        log.info("   /same emails  : " + sameEmails);
        log.info("   /same logins  : " + sameLogins);
        log.info("-----------------");
        log.info("\n");
    }


}
