package ru.steklopod;

import lombok.SneakyThrows;
import me.tongfei.progressbar.ProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.steklopod.entities.mariadb.UserRebased;
import ru.steklopod.entities.mariadb.UserWithException;
import ru.steklopod.entities.mssql.Client;
import ru.steklopod.entities.mssql.Region;
import ru.steklopod.repositories.maria.RegistrtionStepsDAO;
import ru.steklopod.repositories.maria.UserOutDAO;
import ru.steklopod.repositories.ms.ClientInDAO;
import ru.steklopod.repositories.ms.RegionDao;
import ru.steklopod.repositories.ms.VerificationStepDAO;
import ru.steklopod.service.Converter;
import ru.steklopod.service.RegistrtionStepsService;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static ru.steklopod.service.Checker.isInBetTable;
import static ru.steklopod.service.Converter.regStepsOfTsupisHashMap;

@Component
public class Starter
        implements CommandLineRunner
{
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static volatile long count;
    public static volatile long counterOfSaved;
    public static volatile long countOfExistingSteps;
    public static volatile long countOfUsersWithoutPersonalData;
    public static long countOfUserRollbacks;
    public static long countOfStepsRollbacks;
    public static long countOfNotRusPassports;

    public static long notConfirmedEmailWithBets;

    private static long countOfSameId;
    private static long countOfSamePhones;
    private static long countOfTestUsers;
    private static long countOfSameEmails;
    private static long countOfSameLogins;
    private static long countOfExistInBetTable;
    private static long countOfNotNullCashDeskId;
    private static long countOfEmptyEmails;
    private static long countOfEmptyEmailsWithBets;
    private static long countOfEmptyPhones;
    private static long countOfSamePhonesWithBets;

    private long countOfExistingUsers;
    private long countOfUsersToRebase;
    private long countOfTotalStepsToConvert;
    private static final long truthCountOfUsersToRebase = 48059L;

    private volatile boolean isContinue = true;
    private volatile boolean isContinueCalculateSteps = true;

    public static volatile Set<Long> idsFromBET;

    private static volatile Set<String> generalEmails;
    private static volatile Set<String> generalPhones;
    private static volatile Set<String> generalLogins;
    private static volatile Set<Integer> idsSet;

    public static volatile Map<Integer, UserWithException> userWithExceptions;
    public static volatile Map<Integer, String> regionsAlpha3Diction;

    public static volatile Map<Integer, Integer> steps;


    @Autowired
    private Converter converter;
    @Autowired
    private UserOutDAO userOutDAO;
    @Autowired
    private VerificationStepDAO verificationStepDAO;
    @Autowired
    private ClientInDAO repositoryMsSql;
    @Autowired
    private RegistrtionStepsService registrtionStepsService;
    @Autowired
    private RegistrtionStepsDAO registrtionStepsDAO;
    @Autowired
    private RegionDao regionDao;

    @Qualifier("jdbcMsSql")
    @Autowired
    private JdbcTemplate jdbcTemplateMsSql;
    @Qualifier("jdbcMaria")
    @Autowired
    private JdbcTemplate jdbcTemplateMaria;

    @PostConstruct
    @SneakyThrows
    void init() {
        ExecutorService executor = new ForkJoinPool();
        List<Callable<Void>> taskList = getCallablesInitTasks();
        makeInitLogging();
        executor.invokeAll(taskList);
        executor.shutdown();
        logger.info("* Prepearing is done. > OK <");
    }

//    @Override
    public void run(String... args) throws Exception {
        checkCSVFile();
        rebaseUsersStream();
        countTrashUsers();

        ExecutorService executor = new ForkJoinPool();
        Callable<Void> logRebasedWork = () -> {
            Thread.currentThread().setName("LOG users->");
            logRebasedWork();
            return null;
        };
        Callable<Void> initStepMap = () -> {
            Thread.currentThread().setName("INIT users->");
            initStepMap();
            return null;
        };

        List<Callable<Void>> taskList = Arrays.asList(logRebasedWork, initStepMap);
        executor.invokeAll(taskList);
        executor.shutdown();

        clearData();
        rebaseSteps();
    }

    private void checkCSVFile() {
        if (regStepsOfTsupisHashMap.size() < 1) {
            throw new RuntimeException("Ooops... CAN'T READ CSV FILE");
        }
    }


    /**
     * Инит задания. @return List<Callable<Void>>
     */
    private List<Callable<Void>> getCallablesInitTasks() {
        Callable<Void> betIDS = () -> {
            List<Long> idsList = jdbcTemplateMsSql.queryForList("SELECT DISTINCT Id FROM Bet", Long.class);
            idsFromBET = new HashSet<>(idsList);
            idsList.clear();
            return null;
        };
        Callable<Void> emailsTask = () -> {
            generalEmails = new HashSet<>(userOutDAO.findAllEmails());
            return null;
        };
        Callable<Void> phonesTask = () -> {
            generalPhones = new HashSet<>(userOutDAO.findAllPhones());
            return null;
        };
        Callable<Void> loginTask = () -> {
            generalLogins = new HashSet<>(userOutDAO.findAllLogins());
            return null;
        };
        Callable<Void> sameIds = () -> {
            idsSet = new HashSet<>(userOutDAO.findAllIds());
            userWithExceptions = new HashMap<>();
            return null;
        };
        Callable<Void> ddlTask = () -> {
            converter.fillHasMapOfSteps();
            userOutDAO.skipForeignKey();
            return null;
        };
        Callable<Void> regions = () -> {
            regionsAlpha3Diction = new HashMap<>();
            try (Stream<Region> regionStream = regionDao
                    .findAll()
                    .stream()
                    .filter(x -> x.getAlpha2Code() != null)) {
                regionStream
                        .forEach(o -> regionsAlpha3Diction.put(o.getId(), o.getAlpha3Code()));
            }
            return null;
        };

        return Arrays.asList(emailsTask, phonesTask, loginTask, ddlTask, betIDS, sameIds, regions);
    }


    private void makeInitLogging() {
        Thread.currentThread().setName("Init LOG");
        countOfUsersToRebase = repositoryMsSql.selectCount();
        countOfExistingUsers = userOutDAO.selectCountOfUsers();
        countOfTotalStepsToConvert = verificationStepDAO.selectCount();

        logger.info("------------------------------------------");
        logger.info("<<  " + countOfExistingUsers + " of [user] exist in steklopod table;");
        logger.info(">   " + countOfUsersToRebase + " of [Client] in BetConstract table;");
    }

    private void rebaseUsersStream() throws InterruptedException {
        ExecutorService executor = new ForkJoinPool();
        Callable<Void> userRebase = () -> {
            Thread.currentThread().setName("< -  User  -> ");
            rebaseUsers();
            return null;
        };
        Callable<Void> makeProgressBar = () -> {
            Thread.currentThread().setName("BAR users->");
            makeProgressBar();
            return null;
        };
        Callable<Void> makeProgressBarOfSaved = () -> {
            Thread.currentThread().setName("BAR saved->");
            makeProgressBarOfSavedUsers();
            return null;
        };
        List<Callable<Void>> taskList = Arrays.asList(userRebase, makeProgressBar, makeProgressBarOfSaved);
        executor.invokeAll(taskList);
        executor.shutdown();
    }

    private void countTrashUsers() throws InterruptedException {
        ExecutorService executor = new ForkJoinPool();
        Callable<Void> sameId = () -> {
            countOfSameId = userWithExceptions.entrySet().stream()
                    .filter(x -> x.getValue().getIsSameId())
                    .count();
            return null;
        };
        Callable<Void> samePhone = () -> {
            countOfSamePhones = userWithExceptions.entrySet().stream()
                    .filter(x -> x.getValue().getIsPhoneExist())
                    .count();
            countOfSamePhonesWithBets = userWithExceptions.entrySet().stream()
                    .filter(x -> x.getValue().getIsPhoneExist())
                    .filter(o -> isInBetTable(o.getValue().getId()))
                    .count();
            return null;
        };
        Callable<Void> isTest = () -> {
            countOfTestUsers = userWithExceptions.entrySet().stream()
                    .filter(x -> x.getValue().getIsTest())
                    .count();
            return null;
        };
        Callable<Void> sameEmails = () -> {
            countOfSameEmails = userWithExceptions.entrySet().stream()
                    .filter(x -> x.getValue().getIsEmailExist())
                    .count();
            return null;
        };
        Callable<Void> sameLogins = () -> {
            countOfSameLogins = userWithExceptions.entrySet().stream()
                    .filter(x -> x.getValue().getIsLoginExist())
                    .count();
            return null;
        };
        Callable<Void> existInBetTable = () -> {
            countOfExistInBetTable = userWithExceptions.entrySet().stream()
                    .filter(x -> x.getValue().getIsCustomerIdInBetTable())
                    .count();
            return null;
        };
        Callable<Void> notNullCashDeskId = () -> {
            countOfNotNullCashDeskId = userWithExceptions.entrySet().stream()
                    .filter(x -> x.getValue().getNotNullCashDeskId())
                    .count();
            return null;
        };
        Callable<Void> emptyEmail = () -> {
            countOfEmptyEmails = userWithExceptions.entrySet().stream()
                    .filter(x -> x.getValue().getIsEmptyEmail())
                    .count();
            countOfEmptyEmailsWithBets = userWithExceptions.entrySet().stream()
                    .filter(x -> x.getValue().getIsEmptyEmail())
                    .filter(o -> isInBetTable(o.getValue().getId()))
                    .count();
            return null;
        };
        Callable<Void> emptyPhones = () -> {
            countOfEmptyPhones = userWithExceptions.entrySet().stream()
                    .filter(x -> x.getValue().getIsEmptyPhone())
                    .count();
            return null;
        };
        List<Callable<Void>> taskList
                = Arrays.asList(sameId, samePhone, isTest, sameEmails, sameLogins, existInBetTable,
                notNullCashDeskId, emptyEmail, emptyPhones);
        executor.invokeAll(taskList);
        executor.shutdown();
    }

    /**
     * REBASE USERS.
     * Перенос пользователей.
     */
    private void rebaseUsers() {
        logger.info(">>> STARTING REBASE USERS...");
        long startTime = System.currentTimeMillis();
        try (Stream<Client> clientStream = repositoryMsSql
//                .findAllClientsDistinctByPhone()
                .findAll()
                .stream()
                .parallel()
        ) {
            clientStream
                    .forEach(x -> {
                        Optional<UserRebased> userRebased = Optional.ofNullable(converter.convertUserForRebase(x));
                        userRebased.ifPresent(o -> userOutDAO.saveUser(o));
                    });
        } finally {
            isContinue = false;
        }
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;
        logger.info("\r Time: " + duration + " seconds");

    }

    private void logRebasedWork() {
        Long countOfUsersAfterRebase = userOutDAO.selectCountOfUsers();
        Integer pps = jdbcTemplateMsSql.queryForObject("SELECT COUNT(*) FROM Client  WHERE CashDeskId IS NOT NULL", Integer.class);
        Integer testUserCount = jdbcTemplateMsSql.queryForObject("SELECT COUNT(*) FROM Client WHERE IsTest = 1", Integer.class);

        Integer withoutPersonalData = jdbcTemplateMaria.queryForObject("SELECT count (*)FROM user WHERE migration_state = 2", Integer.class);
        Integer emptyPassport = jdbcTemplateMaria.queryForObject("SELECT COUNT(*) FROM user  WHERE passport_number IS NULL ", Integer.class);
        Integer notRus = jdbcTemplateMaria.queryForObject("SELECT COUNT(*) FROM user  WHERE document_type_id =2", Integer.class);

        long checkSum = countOfSameId + countOfNotNullCashDeskId + countOfSamePhones + countOfSameEmails + countOfSameLogins +
                countOfTestUsers + countOfEmptyEmails + countOfEmptyPhones;
//        logger.info("Not rebased rows : " + (countOfUsersToRebase - (countOfUsersAfterRebase - countOfExistingUsers)));
        logger.info("NOT REBASED USERS: " + userWithExceptions.size());
        logger.info("CHECK SUM: " + checkSum);
        logger.info("DIFFERENCE: " + (checkSum - userWithExceptions.size()));

        logger.info("   -same ids     : " + countOfSameId);
        logger.info("   -not confirmed emails without bets : " + countOfExistInBetTable);
        logger.info("-------------------------------");
        logger.info("   -[pps] cahDesk not null    : " + countOfNotNullCashDeskId);
        logger.info("   * pps  users  in BetConstr.: " + pps);
        logger.info("-------------------------------");
        logger.info("   -test users   : " + countOfTestUsers);
        logger.info("   *test users in BetCons.: " + testUserCount);
        logger.info("-------------------------------");
        logger.info("   -empty emails : " + countOfEmptyEmails);
        logger.info("   `empty emails with bets: " + countOfEmptyEmailsWithBets);
        logger.info("   -empty phones : " + countOfEmptyPhones);
        logger.info("   `empty phones with bets: " + countOfSamePhonesWithBets);
        logger.info("-------------------------------");
        logger.info("   >same phones  : " + countOfSamePhones);
        logger.info("   >same emails  : " + countOfSameEmails);
        logger.info("   >same logins  : " + countOfSameLogins);

        logger.info("-------------------------------");
        logger.info("TOTAL   USERS: " + countOfUsersAfterRebase);
        logger.info("REBASED USERS: " + (countOfUsersAfterRebase - countOfExistingUsers));
        logger.info("-not confirmed email with bets: " + notConfirmedEmailWithBets);
        logger.info("~deleted personal data  : " + countOfUsersWithoutPersonalData);
        logger.info("~not rus passport(only for rebased users): " + countOfNotRusPassports);


//        logger.info("without personal data  : " + withoutPersonalData);
//        logger.info("without passport number (total): " + emptyPassport);
//        logger.info(">>> rollbacks: " + countOfUserRollbacks);

    }

    private void clearData() {
        idsSet.clear();
        idsFromBET.clear();
        generalEmails.clear();
        generalPhones.clear();
        generalLogins.clear();
        userWithExceptions.clear();
        regionsAlpha3Diction.clear();
    }

    private void initStepMap() {
        steps = new HashMap<>();
        registrtionStepsDAO.findAll().forEach(x -> steps.put(x.getClientId(), x.getRegistrationStageId()));
    }

    /***************
     * REBASE  STEPS
     * Перенос шагов
     ***************/
    private void rebaseSteps() throws InterruptedException {
        ExecutorService executorSteps = new ForkJoinPool();
        Callable<Void> registrationSteps = () -> {
            Thread.currentThread().setName("< -  Steps  -> ");
            makeTableOfRegistrationSteps();
            return null;
        };
        Callable<Void> makeProgressBarOfSTEPS = () -> {
            Thread.currentThread().setName("BAR steps->");
            makeProgressBarForSTEPS();
            return null;
        };
        List<Callable<Void>> taskListSteps = Arrays.asList(
                registrationSteps, makeProgressBarOfSTEPS);
        executorSteps.invokeAll(taskListSteps);


        executorSteps.shutdown();
    }

    /**
     * REBASE  REGISTRATION STEPS
     * Перенос шагов регистрации
     */
    void makeTableOfRegistrationSteps() {
        System.out.println("-----------------------------------");
        logger.info(">>> Starting rebase steps of registration...");
        logger.info(countOfTotalStepsToConvert + " - count of registration stage to convert;  ");
        Integer countBefore = jdbcTemplateMaria.queryForObject("SELECT COUNT(*) FROM registrationstageupdatedate", Integer.class);

        long startTime = System.currentTimeMillis();

        try (Stream<Integer> ids = repositoryMsSql
                .findAllIds()
                .stream()
                .parallel()
        ) {
            ids.forEach(o -> verificationStepDAO.findStepObjWithPassDateAnd10Status(o)
                    .forEach(x -> registrtionStepsService.convertAndSave(x)));
        }
        isContinueCalculateSteps = false;

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;

        Integer countAfter = jdbcTemplateMaria.queryForObject("SELECT COUNT(*) FROM registrationstageupdatedate", Integer.class);
        logger.info("Time: " + duration + " seconds.");
        logger.info(countAfter + " - of final  registration stage rows;");
        logger.info((countAfter - countBefore) + " - registration stage rebased;");
        logger.info(">>> rollbacks: " + countOfStepsRollbacks);
    }


    /**
     * Make progress bar for steps.
     */
    @SneakyThrows
    void makeProgressBarForSTEPS() {
        ProgressBar pb = new ProgressBar("STEPS_OF_REG:", countOfTotalStepsToConvert);
        pb.start();
        while (isContinueCalculateSteps) {
            Thread.sleep(1000);
            pb.stepTo(countOfExistingSteps);
        }
        pb.stepTo(countOfTotalStepsToConvert);
        pb.setExtraMessage("SUCCESS OK<\n ");
        pb.stop();
    }

    @SneakyThrows
    void makeProgressBar() {
        ProgressBar pb = new ProgressBar("CONVERTED:", countOfUsersToRebase);
        pb.start();
        while (isContinue) {
            Thread.sleep(1000);
            pb.stepTo(count);
        }
        pb.stepTo(countOfUsersToRebase);
        pb.setExtraMessage("SUCCESS OK< ");
        pb.stop();
    }

    @SneakyThrows
    void makeProgressBarOfSavedUsers() {
        ProgressBar pb = new ProgressBar("SAVED:", truthCountOfUsersToRebase);
        pb.start();
        while (isContinue) {
            pb.stepTo(counterOfSaved);
            Thread.sleep(1000);
            pb.maxHint(truthCountOfUsersToRebase);
        }
        long countOfRebasedUsers = userOutDAO.selectCountOfUsers();
        long total = countOfRebasedUsers - countOfExistingUsers;
        pb.maxHint(total);
        pb.stepTo(total);
        pb.setExtraMessage("SUCCESS OK<");
        pb.stop();
    }

    private List<Callable<Void>> getCallablesForUniques() {
        logger.info(">>>Computed counts of users with not unique Phone, Emails Login:");
//        long startTime = System.currentTimeMillis();

        Callable<Void> sameIds = () -> {
            Thread.currentThread().setName("ID's  counter");
            HashSet<Integer> idsTemp = new HashSet<>(idsSet);
            HashSet<Integer> idsSqlServer = repositoryMsSql.findAllIds();
            idsSqlServer.retainAll(idsTemp);
            int countOfSameIds = idsTemp.size();
            idsTemp.clear();
            idsSqlServer.clear();
            logger.info("  -same ids: " + countOfSameIds);
            return null;
        };

        Callable<Void> sameEmails = () -> {
            Thread.currentThread().setName("Emails counter");
            HashSet<String> emailsTemp = new HashSet<>(generalEmails);
            HashSet<String> strings = repositoryMsSql.findAllEmails();
            emailsTemp.retainAll(strings);
            int countOfSameEmails = emailsTemp.size();
            emailsTemp.clear();
            strings.clear();
            logger.info("  -same emails: " + countOfSameEmails);
            return null;
        };
        Callable<Void> genPhones = () -> {
            Thread.currentThread().setName("Phones counter");
            HashSet<String> phonesTemp = new HashSet<>(generalPhones);
            HashSet<String> strings = repositoryMsSql.findAllPhones();
            phonesTemp.retainAll(strings);
            int countOfSamePhones = phonesTemp.size();
            phonesTemp.clear();
            strings.clear();
            logger.info("  -same phones: " + countOfSamePhones);
            return null;
        };
        Callable<Void> sameLogin = () -> {
            Thread.currentThread().setName("Login  counter");
            HashSet<String> loginsTemp = new HashSet<>(generalLogins);
            HashSet<String> strings = repositoryMsSql.findAllLogins();
            loginsTemp.retainAll(strings);
            int countOfSameLogins = loginsTemp.size();
            loginsTemp.clear();
            strings.clear();
            logger.info("  -same logins: " + countOfSameLogins);
            return null;
        };
        System.gc();
//        long endTime = System.currentTimeMillis();
//        long duration = (endTime - startTime) / 1000;
//        logger.info(">>> in " + duration + " seconds.");
        return Arrays.asList(sameLogin, genPhones, sameEmails, sameIds);
    }

    public static synchronized Set<String> getgeneralEmails() {
        return generalEmails;
    }
    public static synchronized Set<String> getgeneralPhones() { return generalPhones; }
    public static synchronized Set<String> getGeneralLogins() {
        return generalLogins;
    }
    public static synchronized Set<Integer> getIdsSet() {
        return idsSet;
    }

    public static synchronized void addgeneralEmails(String email) {
        generalEmails.add(email);
    }
    public static synchronized void addgeneralPhones(String phone) {
        generalPhones.add(phone);
    }
    public static synchronized void addGeneralLogins(String generalLogin) {
        generalLogins.add(generalLogin);
    }
    public static synchronized void addIdToSet(Integer id) {
        idsSet.add(id);
    }

    static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
