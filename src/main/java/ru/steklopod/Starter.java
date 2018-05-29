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
import ru.steklopod.entities.mariadb.RegStep2Fields;
import ru.steklopod.entities.mariadb.RegistrationSteps;
import ru.steklopod.entities.mariadb.UserRebased;
import ru.steklopod.entities.mssql.Client;
import ru.steklopod.entities.mssql.ClientVerificationStep;
import ru.steklopod.entities.mssql.Region;
import ru.steklopod.repositories.maria.RegStep2FieldsDAO;
import ru.steklopod.repositories.maria.RegistrtionStepsDAO;
import ru.steklopod.repositories.maria.UserOutDAO;
import ru.steklopod.repositories.ms.ClientInDAO;
import ru.steklopod.repositories.ms.RegionDao;
import ru.steklopod.repositories.ms.VerificationStepDAO;
import ru.steklopod.service.*;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.steklopod.service.AfterRebase.toHumanReadableDuration;
import static ru.steklopod.service.Checker.*;
import static ru.steklopod.service.Converter.regStepsOfTsupisHashMap;

@Component
public class Starter
        implements CommandLineRunner {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static volatile long count;
    public static volatile long countOfExistingSteps;
    public static volatile long countOfUsersWithoutPersonalData;
    public static long countOfUserRollbacks;
    public static long countOfStepsRollbacks;
    public static AtomicInteger countOfNotRusPassports = new AtomicInteger(0);

    private static long countOfExistingUsers;
    private static long countOfUsersToRebase;
    private static long countOfTotalStepsToConvert;
    private static final long TRUTH_COUNT_OF_STEPS_TO_REBASE = 224146L;

    private volatile boolean canRebaseSteps = false;
    private volatile boolean isContinue = true;
    private volatile boolean isContinueStageId = true;
    private volatile boolean isContinueCalculateSteps = true;

    private static volatile Set<String> generalEmails;
    private static volatile Set<String> generalLogins;
    private static volatile Set<String> notRusPassports;
    private static volatile Set<Integer> idsSet;
    private static volatile Set<Integer> allIDSAfterRebase;

    public static volatile Set<String> rusPassports;
    public static volatile Set<RegStep2Fields> steps;
    public static volatile Set<Long> idsFromBET;
    public static volatile Set<Integer> betIdsWith98;
    public static volatile Set<Integer> notConfirmedEmails;
    public static volatile Set<Integer> idsWith10StateInBet;
    public static volatile Set<Integer> idsFromEmailChecking;

    private static volatile Map<String, Integer> generalPhones;
    public static volatile Map<Integer, Timestamp> idsOfCreationData;
    public static volatile Map<Integer, String> regionsAlpha3Diction;
    public static volatile Map<Integer, Integer> regStepConvertDictionary;
    public static volatile Map<Long, Integer> savedIds = new HashMap<>();

    private static volatile Map<Integer, Set<Integer>> allRegStepsByCustomerId;

    public static long startTime;

    @Autowired
    private Converter converter;
    @Autowired
    private UserService userService;
    @Autowired
    private UserOutDAO userOutDAO;
    @Autowired
    private VerificationStepDAO verificationStepDAO;
    @Autowired
    private ClientInDAO repositoryMsSql;
    @Autowired
    private RegStepsService regStepsService;
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
    @Autowired
    private AfterRebase afterRebase;
    @Autowired
    private RegStep2FieldsDAO regStep2FieldsDAO;

    @PostConstruct
    @SneakyThrows
    void init() {
        startTime = System.currentTimeMillis();

        ExecutorService executor = new ForkJoinPool();
        List<Callable<Void>> taskList = getCallablesInitTasks();
        makeInitLogging();
        new Thread(this::makeSetOfNullPassDate).start();
        executor.invokeAll(taskList);
        executor.shutdown();
        logger.info("* Prepearing is done.");
    }

    //    @Override
    public void run(String... args) throws InterruptedException {
        checkCSVFile();

        rebaseUsersStream();


        ExecutorService executor = new ForkJoinPool();
        Callable<Void> logRebasedWork = () -> {
            Thread.currentThread().setName("LOG users->");
            logRebasedWork();
            return null;
        };
        Callable<Void> initStepMap = () -> {
            Thread.currentThread().setName("INIT steps");
            initStepMap();
            return null;
        };
        List<Callable<Void>> taskList = Arrays.asList(logRebasedWork, initStepMap);
        executor.invokeAll(taskList);
        executor.shutdown();

        new Thread(this::clearData).start();

        rebaseSteps();

        afterRebase.makeAfterRebaseWork();
    }

    private void makeSetOfNullPassDate() {
        Set<Integer> a10null = new HashSet<>();
        Set<Integer> a4Notnull = new HashSet<>();
        Set<Integer> nullPassdateState10 = verificationStepDAO.findNullPassdateState10();
        idsOfCreationData = new HashMap<>();

        nullPassdateState10
                .forEach(id -> {
                    List<ClientVerificationStep> allStepsWithNull
                            = verificationStepDAO.findAllByClientId(id);
                    allStepsWithNull.stream()
                            .filter(a -> a.getState() == 10)
                            .filter(x -> x.getPassDate() == null)
                            .forEach(o -> a10null.add(o.getClientId()));
                    allStepsWithNull.stream()
                            .filter(a -> a.getState() == 10)
                            .filter(x -> x.getPassDate() != null)
                            .forEach(o -> a4Notnull.add(o.getClientId()));
                });
        a10null.retainAll(a4Notnull);
        a10null
                .forEach(x -> {
                            List<ClientVerificationStep> verificationSteps = verificationStepDAO.findAllByClientId(x)
                                    .stream()
                                    .sorted((o1, o2) -> Integer.compare(o2.getStep(), o1.getStep()))
                                    .filter(s -> s.getState() == 10)
                                    .collect(Collectors.toList());

                            Optional<ClientVerificationStep> clientVStepNotNullDate = verificationSteps
                                    .stream()
                                    .filter(o -> o.getPassDate() != null)
                                    .findFirst();

                            clientVStepNotNullDate.ifPresent(stepVer -> {
                                Timestamp maxPassDate = stepVer.getPassDate();
                                Integer step = stepVer.getStep();
                                verificationSteps
                                        .forEach(el -> {
                                            if (el.getPassDate() == null) {
                                                if (el.getStep() > step) {
                                                    el.setPassDate(maxPassDate);
                                                } else {
                                                    int indexOfPrevElWithNullDate = verificationSteps.indexOf(el);
                                                    el.setPassDate(verificationSteps.get(indexOfPrevElWithNullDate - 1).getCreated());
                                                }
                                                idsOfCreationData.put(el.getId(), el.getPassDate());
                                            }
                                        });
                            });
                        }
                );
        a10null.clear();
        a4Notnull.clear();
        nullPassdateState10.clear();
        canRebaseSteps = true;
    }

    private void checkCSVFile() {
        int size = regStepsOfTsupisHashMap.size();
        if (size < 1) {
            throw new RuntimeException("Ooops... CAN'T READ CSV FILE");
        }
        /*else{
            System.err.println(">Ok. Size of regStepsOfTsupisHashMap: " + size + "\n First 5 rows: \n");
            regStepsOfTsupisHashMap.entrySet().stream().limit(5).forEach(System.out::println);
        }*/
    }

    /**
     * Инит задания. @return List<Callable<Void>>
     */
    private List<Callable<Void>> getCallablesInitTasks() {
        Callable<Void> betIDS = () -> {
            List<Long> idsList = jdbcTemplateMsSql.queryForList("SELECT DISTINCT ClientId FROM Bet", Long.class);
            idsFromBET = new HashSet<>(idsList);
            idsList.clear();
            return null;
        };
        Callable<Void> betIdsPPS = () -> {
            List<Integer> idsList = jdbcTemplateMsSql.queryForList(
                    "SELECT DISTINCT ClientId\n" +
                            "  FROM Bet\n" +
                            "  WHERE Source IN (98, 99)"
                    , Integer.class);

            List<Integer> idsListIn = jdbcTemplateMsSql.queryForList(
                    "SELECT DISTINCT ClientId\n" +
                            "  FROM Bet\n" +
                            "  WHERE Source NOT IN (98, 99)"
                    , Integer.class);

            idsList.removeAll(idsListIn);

            betIdsWith98 = new HashSet<>(idsList);
            idsList.clear();
            idsListIn.clear();

            return null;
        };
        Callable<Void> idsWith10 = () -> {
            List<Integer> idsWith10State
                    = jdbcTemplateMsSql.queryForList("SELECT DISTINCT ClientId FROM ClientVerificationStep WHERE  State = 10", Integer.class);
            idsWith10StateInBet = new HashSet<>(idsWith10State);
            idsWith10State.clear();
            return null;
        };
        Callable<Void> idsFromEmailCheck = () -> {
            List<Integer> idsFromEmail
                    = jdbcTemplateMaria.queryForList("SELECT DISTINCT user_id FROM useremailconfirmationcode", Integer.class);
            idsFromEmailChecking = new HashSet<>(idsFromEmail);
            idsFromEmail.clear();
            return null;
        };
        Callable<Void> idsOfCreation = () -> {
            idsOfCreationData = new HashMap<>();
            verificationStepDAO.findAll().stream()
                    .filter(e -> e.getStep() == 1)
                    .forEach(z -> idsOfCreationData.put(z.getClientId(), z.getPassDate()));
            return null;
        };
        Callable<Void> emailsTask = () -> {
            generalEmails = new HashSet<>(userOutDAO.findAllEmails());
            return null;
        };
        Callable<Void> phonesTask = () -> {
            generalPhones = new HashMap<>();
            userOutDAO.findAllPhones().forEach(p -> generalPhones.put(p, 1));
            return null;
        };
        Callable<Void> loginTask = () -> {
            generalLogins = new HashSet<>(userOutDAO.findAllLogins());
            return null;
        };
        Callable<Void> sameIds = () -> {
            idsSet = new HashSet<>(userOutDAO.findAllIds());
            return null;
        };
        Callable<Void> notRusPass = () -> {
            notRusPassports = new HashSet<>(repositoryMsSql.findPassportNumberNotRus());
            return null;
        };
        Callable<Void> rusPass = () -> {
            rusPassports = new HashSet<>(repositoryMsSql.findRussPassports());
            return null;
        };
        Callable<Void> notConfirmedEmail = () -> {
            notConfirmedEmails = new HashSet<>(verificationStepDAO.findNotConfirmedEmails());
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
        return Arrays.asList(idsOfCreation, notConfirmedEmail, notRusPass, idsFromEmailCheck, idsWith10, emailsTask,
                phonesTask, loginTask, ddlTask, betIDS, sameIds, regions, betIdsPPS, rusPass);
    }

    private void makeInitLogging() {
        Thread.currentThread().setName("Init LOG");
        countOfUsersToRebase = repositoryMsSql.selectCount();
        countOfExistingUsers = userOutDAO.selectCountOfUsers();
        countOfTotalStepsToConvert = verificationStepDAO.selectCount();
        logger.info("------------------------------------------");
        logger.info("<<  " + countOfExistingUsers + " of [user] exist in steklopod table;");
        logger.info(">>  " + countOfUsersToRebase + " of [Client] in BetConstract table;");
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
            makeProgressBarForUserRebase();
            return null;
        };
        List<Callable<Void>> taskList = Arrays.asList(userRebase, makeProgressBar /*, makeProgressBarOfSaved*/);
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
                .findAllWith10State()
                .stream()
                .parallel()) {
            clientStream
                    .forEach(x -> {
                        Optional<UserRebased> userRebased = Optional.ofNullable(converter.convertUserForRebase(x));
                        userRebased.ifPresent(o -> userService.saveUser(o));
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
        Integer pps = jdbcTemplateMsSql.queryForObject(
                "SELECT COUNT(1) FROM (SELECT DISTINCT Client.id FROM Client LEFT JOIN BET ON Bet.ClientId = Client.Id  WHERE Source IN(98,99)) q;",
                Integer.class);
        Integer testUserCount = jdbcTemplateMsSql.queryForObject("SELECT COUNT(*) FROM Client WHERE IsTest = 1", Integer.class);

        int notRebased = countOfSameId.get()
                + countOfSameEmails.get()
                + countOfSameLogins.get()
                + countOfTestUsers.get()
                + countOfUsersWith98Source.get()
                + notConfirmedEmailCounter.get()
                + countOfEmptyEmails.get();
        logger.info(">>  " + countOfUsersToRebase + " of [Client] in BetConstract table;");
        logger.info("<<  " + countOfExistingUsers + " of [user]   in steklopod table.");
        logger.info("NOT REBASED USERS: " + notRebased);
        logger.info("   -same ids                          : " + countOfSameId);
        logger.info("   -not confirmed emails without bets : " + notConfirmedEmailCounter);
        logger.info("-------------------------------");
        logger.info("   -[pps] source in Bet 98-99 : " + countOfUsersWith98Source);
        logger.info("   * pps  users  in BetConstr.: " + pps);
        logger.info("-------------------------------");
        logger.info("   -test users   : " + countOfTestUsers);
        logger.info("   *test users in BetCons.: " + testUserCount);
        logger.info("-------------------------------");
        logger.info("   -empty emails : " + countOfEmptyEmails);
        logger.info("   `empty emails with bets: " + countOfEmptyEmailsWithBets);
        logger.info("   -empty phones : " + countOfEmptyPhones);
        logger.info("   `empty phones with bets: " + countOfEmptyPhonesWithBets);
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

//        new Thread(() ->
        Checker.makeLoggingOfNotUnique()
        ;
//        ).start();
    }

    private void clearData() {
        Thread.currentThread().setName("Clear Data");
        idsSet.clear();
        idsFromBET.clear();
        generalEmails.clear();
        generalPhones.clear();
        generalLogins.clear();
        betIdsWith98.clear();
        regionsAlpha3Diction.clear();
        rusPassports.clear();
        notConfirmedEmails.clear();
        notRusPassports.clear();
        logger.info("Resources is cleared.");
    }

    @SneakyThrows
    private void initStepMap() {
        steps = new HashSet<>();
        regStepConvertDictionary = new HashMap<Integer, Integer>() {{
            put(4, 1);
            put(5, 2);
            put(9, 3);
            put(17, 4);
            put(18, 5);
            put(19, 6);
            put(2, 7);
        }};

        try {
            jdbcTemplateMaria.update("ALTER TABLE useremailconfirmationcode  DROP FOREIGN KEY fk_useremailconfirmationcode__user");
            jdbcTemplateMaria.update("ALTER TABLE useremailconfirmationcode  DROP INDEX fk_useremailconfirmationcode__user");
        } catch (Exception e) {
            logger.warn("Can't delete foreign key");
        }

        ExecutorService executor = new ForkJoinPool();

        Callable<Void> makeTable = () -> {
            Thread.currentThread().setName("STEPS TABLE");
            makeStageIdTable();
            if (isContinueStageId) {
                isContinueStageId = false;
            }
            return null;
        };
        Callable<Void> makeMap = () -> {
            Thread.currentThread().setName("STEPS MAP  ");
            makeStepsMap();
            return null;
        };
        Callable<Void> progressBar = () -> {
            makeProgressBarOfStageId();
            return null;
        };
        List<Callable<Void>> taskList = Arrays.asList(makeMap, makeTable, progressBar);
        executor.invokeAll(taskList);
        executor.shutdown();
    }

    private void makeStepsMap() {
        try (Stream<RegistrationSteps> s = registrtionStepsDAO.findAll().parallelStream()) {
            s.forEach(x -> addSteps(new RegStep2Fields(x.getClientId(), x.getRegistrationStageId())));
        }
        logger.info(">OK< |Finished initialisation of registration steps map| ");
    }

    private void makeStageIdTable() {
        logger.info("Filling [rebased_stages] table of registration steps... ");
        logger.info("Please, be passion. It could take some minutes... ");
        try (Stream<UserRebased> userRebasedStream = userOutDAO.findAll().parallelStream()) {
            userRebasedStream.forEach(user ->
                    regStep2FieldsDAO.save(new RegStep2Fields(user.getCustomerId().intValue(), user.getRegistrationStageId()))
            );
        } finally {
            isContinueStageId = false;
        }
        logger.info(">OK< |Filled [rebased_stages] table| " + steps.size());
    }

    /***************
     * REBASE  STEPS
     * Перенос шагов
     ***************/
    private void rebaseSteps() throws InterruptedException {
        ExecutorService executorSteps = new ForkJoinPool();
        Callable<Void> registrationSteps = () -> {
            Thread.currentThread().setName("< -  Steps  -> ");
            convertAndSaveSteps();
            return null;
        };
        Callable<Void> makeProgressBarOfSTEPS = () -> {
            Thread.currentThread().setName("BAR steps->");
            makeProgressBarForSTEPS();
            return null;
        };
        List<Callable<Void>> taskListSteps = Arrays.asList(registrationSteps, makeProgressBarOfSTEPS);
        while (!canRebaseSteps) {
            Thread.sleep(1500);
        }
        executorSteps.invokeAll(taskListSteps);
        executorSteps.shutdown();
    }

    /**
     * REBASE  REGISTRATION STEPS
     * Перенос шагов регистрации
     */
    void convertAndSaveSteps() {
        Thread.currentThread().setName("Steps Of Reg.");
        logger.info("\n--------------------------------------------");
        logger.info(">>> Starting rebase steps of registration...");
        logger.info(countOfTotalStepsToConvert + " - count of registration stage to convert;  ");
        logger.info("--------------------------------------------");
        Integer countBefore = jdbcTemplateMaria.queryForObject("SELECT COUNT(*) FROM registrationstageupdatedate", Integer.class);
        long startTime = System.currentTimeMillis();

        allIDSAfterRebase = new HashSet<>(jdbcTemplateMaria.queryForList(
                "SELECT DISTINCT customer_id FROM user", Integer.class));

        try (Stream<Integer> idsStream = allIDSAfterRebase.parallelStream()) {
            idsStream
                    .forEach(id -> verificationStepDAO.findStepObjWithPassDateAnd10Status(id)
                            .forEach(regStepsService::convertAndSave));
        }
        isContinueCalculateSteps = false;
        long endTime = System.currentTimeMillis();
//        long duration = (endTime - startTime) / 1000;
        long duration = endTime - startTime;
        Integer countAfter = jdbcTemplateMaria.queryForObject("SELECT COUNT(*) FROM registrationstageupdatedate", Integer.class);
        logger.info("Duration of rebasing steps & user email conf. code: " + toHumanReadableDuration(duration));
        logger.info(countAfter + " - of final  registration stage rows;");
        logger.info((countAfter - countBefore) + " - registration stage rebased;");
        logger.info(">>> rollbacks: " + countOfStepsRollbacks);
    }


    /**
     * Progress bar for users.
     */
    void makeProgressBarForUserRebase() throws InterruptedException {
        Thread.currentThread().setName("ProgressBar_1");
        ProgressBar pb = new ProgressBar("CONVERTED USERS:", countOfUsersToRebase);
        pb.start();
        while (isContinue) {
            Thread.sleep(1000);
            pb.stepTo(count);
        }
        pb.stepTo(countOfUsersToRebase);
        pb.setExtraMessage("SUCCESS OK< ");
        pb.stop();
    }

    /**
     * Progress bar for stage_id.
     */
    private void makeProgressBarOfStageId() throws InterruptedException {
        Integer allUsers = jdbcTemplateMaria.queryForObject("SELECT COUNT(*) FROM user", Integer.class);
        Thread.currentThread().setName("ProgressBar_2");

        ProgressBar pb = new ProgressBar("SAVED STAGES_ID:", allUsers);
        pb.start();
        while (isContinueStageId) {
            Thread.sleep(2000);
            Integer ready = jdbcTemplateMaria.queryForObject("SELECT COUNT(*) FROM rebased_stages", Integer.class);
            pb.stepTo(ready);
        }
        pb.stepTo(allUsers);
        pb.setExtraMessage("Preparing of [rebased_stages] table is done");
        pb.stop();
    }

    /**
     * Progress bar for steps.
     */
    void makeProgressBarForSTEPS() throws InterruptedException {
        Thread.currentThread().setName("ProgressBar_3");

        ProgressBar pb = new ProgressBar("STEPS_OF_REG:", TRUTH_COUNT_OF_STEPS_TO_REBASE);
        pb.start();
        while (isContinueCalculateSteps) {
            Thread.sleep(1000);
            pb.stepTo(countOfExistingSteps);
        }
        pb.stepTo(TRUTH_COUNT_OF_STEPS_TO_REBASE);
        pb.setExtraMessage("SUCCESS OK<\n ");
        pb.stop();
    }

    public static synchronized Set<String> getgeneralEmails() {
        return generalEmails;
    }

    public static synchronized Map<String, Integer> getgeneralPhones() {
        return generalPhones;
    }

    public static synchronized void putFirstTimePhone(String phone) {
        generalPhones.put(phone, 1);
    }

    public static synchronized Set<String> getGeneralLogins() {
        return generalLogins;
    }

    public static synchronized Set<Integer> getIdsSet() {
        return idsSet;
    }

    public static synchronized void addgeneralEmails(String email) {
        generalEmails.add(email);
    }

    public static synchronized void addGeneralLogins(String generalLogin) {
        generalLogins.add(generalLogin);
    }

    public static synchronized void addIdToSet(Integer id) {
        idsSet.add(id);
    }

    private static synchronized void addSteps(RegStep2Fields step) {
        Starter.steps.add(step);
    }

    public static synchronized void addIdInEmailChecking(Integer idsFromEmailChecking) {
        Starter.idsFromEmailChecking.add(idsFromEmailChecking);
    }

    static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private void saveRebasedIDs() {
        Thread.currentThread().setName("Saving ID");
        logger.info("ЗАПИСЫВАЕМ ID ПЕРЕНЕСЕННЫХ ПОЛЬЗОВАТЕЛЕЙ...");
        savedIds
                .forEach((key, value) -> jdbcTemplateMaria.update("INSERT INTO ids (customer_id, id) VALUES (?, ?)"
                        , key, value));
        logger.info("ID успешно записаны.");
    }

    public static void addIntoStepsMap(Integer clientId, Integer currentRegistrationStageId) {
        Optional<Set<Integer>> regSteps = Optional.ofNullable(allRegStepsByCustomerId.get(clientId));
        if (regSteps.isPresent()) {
            Set<Integer> currentStages = regSteps.get();
            currentStages.add(currentRegistrationStageId);
        } else {
            Set<Integer> regStepsList = new HashSet<>();
            regStepsList.add(currentRegistrationStageId);
            allRegStepsByCustomerId.put(clientId, regStepsList);
        }
    }


}
