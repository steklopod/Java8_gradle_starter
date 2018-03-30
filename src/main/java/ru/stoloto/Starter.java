package ru.stoloto;

import lombok.SneakyThrows;
import me.tongfei.progressbar.ProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.stoloto.entities.mariadb.UserRebased;
import ru.stoloto.entities.mssql.Client;
import ru.stoloto.repositories.maria.RegistrtionStepsDAO;
import ru.stoloto.repositories.maria.UserOutDAO;
import ru.stoloto.repositories.ms.ClientInDAO;
import ru.stoloto.repositories.ms.VerificationStepDAO;
import ru.stoloto.service.Converter;
import ru.stoloto.service.RegistrtionStepsService;

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

@Component
public class Starter
        implements CommandLineRunner
{

    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static long count;
    public static volatile int counterOfSaved;

    public static long countOfExistingSteps;
    private volatile long countOfTotalStepsToConvert;

    private volatile boolean isContinue = true;
    private volatile boolean isContinueCalculateSteps = true;

    private static long countOfExistingUsers;
    private static long countOfUsersToRebase;
    public static long truthCountOfUsersToRebase = 46730;
    public static long countOfUsersWithoutPersonalData;

    private volatile static HashSet<String> generalEmails;
    private volatile static HashSet<String> generalPhones;
    private volatile static HashSet<String> generalLogins;
    public volatile static HashSet<Integer> idsFromBET;

    public volatile static Map<Integer, Integer> steps;

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
        makeInitLogging();
        List<Callable<Void>> taskList = getCallablesInitTasks();
        executor.invokeAll(taskList);
        executor.shutdown();
        logger.info("* Prepearing is done. > OK <");
    }

    //    @Override
    public void run(String... args) throws Exception {
        rebaseUsersStream();
        clearData();
        initStepMap();
        rebaseSteps();
    }

    /**
     * Инит задания. @return List<Callable<Void>>
     */
    private List<Callable<Void>> getCallablesInitTasks() {
        Callable<Void> betIDS = () -> {
            List<Integer> idsList = jdbcTemplateMsSql.queryForList("SELECT Id FROM Bet", Integer.class);
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
            generalLogins = new HashSet<>();
            userOutDAO.findAllLogins().forEach(x -> generalLogins.add(x.toLowerCase()));
            return null;
        };
        Callable<Void> ddlTask = () -> {
            converter.fillHasMapOfSteps();
            userOutDAO.addColumns();
            userOutDAO.skipForeignKey();
            return null;
        };
        return Arrays.asList(emailsTask, phonesTask, loginTask, ddlTask, betIDS);
    }

    private void makeInitLogging() {
        countOfUsersToRebase = repositoryMsSql.selectCount();
        countOfExistingUsers = userOutDAO.selectCountOfUsers();
        countOfTotalStepsToConvert = verificationStepDAO.selectCount();
        Integer testUserCount = jdbcTemplateMsSql.queryForObject("SELECT COUNT(*) FROM Client WHERE IsTest = 1", Integer.class);
        Integer pps = jdbcTemplateMsSql.queryForObject("SELECT COUNT(*) FROM Client  WHERE CashDeskId IS NOT NULL", Integer.class);

        logger.info("---------------------------------------------");
        logger.info(">> " + countOfExistingUsers + " of [user] exist in Stoloto table;");
        logger.info("> " + countOfUsersToRebase + " of [Client] in BetConstract table;");
        logger.info("  " + testUserCount + " of test [Client] in BetConstract table;");
        logger.info("  " + pps + " of pps users;");
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

    /**
     * REBASE USERS.
     * Перенос пользователей.
     */
    private void rebaseUsers() {
        logger.info("---------------------------------------------");
        logger.info(">>> STARTING REBASE USERS...");
        long startTime = System.currentTimeMillis();
        try (Stream<Client> clientStream = repositoryMsSql
                .findAllClientsDistinctByPhone()
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
//        isContinue = false;

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;

        logger.info("\r   >>> OK. SUCCESS REBASED: ");
        logger.info("Time: " + duration + " seconds");

        Long countOfRebasedUsers = userOutDAO.selectCountOfUsers();
        Integer withoutPersonalData = jdbcTemplateMaria.queryForObject("SELECT count (*)FROM user WHERE migration_state = 2", Integer.class);

        logger.info("Total   users: " + countOfRebasedUsers);
        logger.info("Rebased users: " + (countOfRebasedUsers - countOfExistingUsers));
        logger.info("Not rebased users: " + (countOfUsersToRebase - (countOfRebasedUsers - countOfExistingUsers)));
        logger.info("Deleted personal data: " + countOfUsersWithoutPersonalData);
        logger.info("Without personal data: " + withoutPersonalData);
        System.out.println("---------------------------");
    }

    private void clearData() {
        generalEmails.clear();
        generalPhones.clear();
        generalLogins.clear();
        System.gc();
        initStepMap();
    }

    private void initStepMap() {
        steps = new HashMap<>();
        registrtionStepsDAO.findAll().forEach(x -> steps.put(x.getClientId(), x.getRegistrationStageId()));
//        System.err.println("steps.size() - " + steps.size());
    }

    /***************
     * REBASE  STEPS
     * Перенос шагов
     ***************/
    private void rebaseSteps() throws InterruptedException {
        ExecutorService executorSteps = new ForkJoinPool();
        Callable<Void> registrationSteps = () -> {
            Thread.currentThread().setName("< -  Steps ->");
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
        System.out.println("----------------------------------------");
        logger.info(">>> Starting rebase steps of registration...");
        logger.info(countOfTotalStepsToConvert + " - count of registration steps TO CONVERT;  ");
        Integer countBefore = jdbcTemplateMaria.queryForObject("SELECT COUNT(*) FROM registrationstageupdatedate", Integer.class);

//        logger.info(idsFromBET.size() + " - count of registration steps TO REBASE.");
        long startTime = System.currentTimeMillis();

        try (Stream<Integer> ids = repositoryMsSql
                .findAllIds()
                .stream()
                .parallel()
//             Если (user_id && stage_id) -> не переносим
        ) {
            ids.forEach(o -> verificationStepDAO.findStepObjWithPassDateAnd10Status(o)
                    .forEach(x -> registrtionStepsService.convertAndSave(x)));
        }
        isContinueCalculateSteps = false;
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;

        Integer countAfter = jdbcTemplateMaria.queryForObject("SELECT COUNT(*) FROM registrationstageupdatedate", Integer.class);
        logger.info("\r" + countAfter + " - of final  registration stage rows;");
//        logger.info(RegistrtionStepsService.countOfSavedSteps + " - registration stage rows saved;");
        logger.info((countAfter - countBefore) + " - registration stage rebased;");
        logger.info("Time: " + duration + " seconds.");

        logger.info("SUCCESS. Ok");
        System.out.println("----------------------------------------");
    }


    /**
     * Make progress bar for steps.
     */
    @SneakyThrows
    void makeProgressBarForSTEPS() {
        ProgressBar pb = new ProgressBar("STEPS_OF_REG:", countOfTotalStepsToConvert);
        pb.start();
        while (isContinueCalculateSteps) {
            Thread.sleep(1500);
            pb.stepTo(countOfExistingSteps);
        }
        pb.stepTo(countOfTotalStepsToConvert);
        pb.setExtraMessage("\r SUCCESS OK<\n ");
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
        pb.setExtraMessage("SUCCESS OK<\n ");
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
        pb.setExtraMessage("SUCCESS OK <\n");
        pb.stop();
    }


    static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public synchronized static HashSet<String> getgeneralEmails() {
        return generalEmails;
    }

    public static void addgeneralEmails(String email) {
        generalEmails.add(email);
    }

    public synchronized static HashSet<String> getgeneralPhones() {
        return generalPhones;
    }

    public static void addgeneralPhones(String phone) {
        generalPhones.add(phone);
    }

    public synchronized static HashSet<String> getGeneralLogins() {
        return generalLogins;
    }

    public static void addGeneralLogins(String generalLogin) {
        generalLogins.add(generalLogin);
    }
}
