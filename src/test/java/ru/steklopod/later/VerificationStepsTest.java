package ru.steklopod.later;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.steklopod.entities.mariadb.RegistrationSteps;
import ru.steklopod.entities.mariadb.UserEmailConfirm;
import ru.steklopod.entities.mssql.ClientVerificationStep;
import ru.steklopod.repositories.maria.RegistrtionStepsDAO;
import ru.steklopod.repositories.maria.UserEmailConfirmationCodeDAO;
import ru.steklopod.repositories.ms.VerificationStepDAO;
import ru.steklopod.service.RegStepsService;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@Transactional
@DisplayName("Тестируем стадии верификации")
@Disabled
class VerificationStepsTest {
    @Autowired
    private VerificationStepDAO verificationStepDAO;
    @Autowired
    private UserEmailConfirmationCodeDAO emailConfirmationCodeDAO;
    @Qualifier("jdbcMaria")
    @Autowired
    private JdbcTemplate jdbcTemplateMaria;
    @Qualifier("jdbcMsSql")
    @Autowired
    private JdbcTemplate jdbcTemplateMsSql;
    @Autowired
    private RegistrtionStepsDAO registrtionStepsDAO;
    @Autowired
    private RegStepsService regStepsService;
    private static volatile Set<Integer> idsWith10StateInBet;



    @Test
    void t(){

    }

    private static Stream<Integer> makeIDs() {
        return Stream.of(
                12026821, 12026838, 12889136, 11595571, 11701132, 55308090, 22225320, 11446392,
                11486046, 11523437, 55238717, 11571919, 11591672, 11812258, 11563150
        );
    }

    //    @BeforeAll
    static void before() {
        idsOfCreationData = new HashMap<>();
        idsOfCreationData.put(11593690, random(Timestamp.class));
        idsOfCreationData.put(11607786, random(Timestamp.class));
        idsOfCreationData.put(11567400, random(Timestamp.class));
        idsOfCreationData.put(11564447, random(Timestamp.class));
        idsOfCreationData.put(11525981, random(Timestamp.class));
    }

    @Test
    void NOT_CONFIRMED_EMAILS(){
        Set<Integer> notConfirmedEmails = verificationStepDAO.findNotConfirmedEmails();
        System.err.println(notConfirmedEmails);
    }

    @Rollback(false)
    @ParameterizedTest(name = "Тест #{index} для [{arguments}]")
    @ValueSource(ints = {
            11484387, 11593690, 11607786, 11567400, 11564447, 11525981, 11499428, 11500803, 11500827, 11500858, 11501008, 11511380, 11515531
    })
    void idsFromEmailChecking(int id) {
        ClientVerificationStep clientVerificationStep = verificationStepDAO.findAllByClientId(id).get(0);
        System.err.println(clientVerificationStep);
        UserEmailConfirm userEmailConfirm = new UserEmailConfirm(id);

        if (idsWith10StateInBet.contains(id)) {
            userEmailConfirm.setChecked(true);
        }
        Optional<String> code = Optional.ofNullable(clientVerificationStep.getCode());
        Timestamp creationDate = idsOfCreationData.get(id);
        if (code.isPresent() && creationDate != null) {
            String c = code.get();
            userEmailConfirm.setCreationDate(creationDate);

            System.err.println("CODE: " + c);

            userEmailConfirm.setCode(c);
            emailConfirmationCodeDAO.save(userEmailConfirm);
//            idsFromEmailChecking.add(id);
        }

    }

    @DisplayName("Максимальный VerificationStep [Object]")
    @ParameterizedTest(name = "Тест #{index} для [{arguments}]")
    @ValueSource(ints = {11484387, 12026821, 12026838, 22225320, 11499428,
            11500803, 11500827, 11500827, 11500858, 11501008, 11511380, 11515531,
    })
    void getMaxObj(int id) {
        List<ClientVerificationStep> allByClientId = verificationStepDAO.findAllByClientId(id);
        System.out.println("КОЛ-ВО: " + allByClientId.size());
        allByClientId.forEach(x -> {
            System.err.println("STEP: " + x.getStep() + ", STATE: " + x.getState());
            System.err.println("PassDate: " + x.getPassDate() + '\n');
        });
        ClientVerificationStep max = regStepsService.getMaxVerificationStepObject(id);
        if (max != null) {
            System.out.println("max STEP: " + max.getStep() + ", max STATE: " + max.getState() + ", date: " + max.getPassDate());
        }
    }

    @DisplayName("State = 10, PassDate = null")
    @ParameterizedTest(name = "Тест #{index} для [{arguments}]")
    @ValueSource(ints = {
            11593690, 11607786, 11593690, 11567400, 11564447, 11525981
//           , 11525981, 11499428, 11500803, 11500827, 11500858, 11501008, 11511380, 11515531
    })
    void rebase(int id) {
        List<ClientVerificationStep> allByClientId =
                verificationStepDAO
                        .findAllByClientId(id)
                        .stream()
                        .filter(o -> o.getPassDate() == null)
                        .filter(o -> o.getState() == 10)
                        .collect(Collectors.toList());
        System.out.println("КОЛ-ВО: " + allByClientId.size());
        allByClientId.forEach(x -> {
            System.err.println("ID: " + x.getClientId() + ", STEP: " + x.getStep() + ", STATE: " + x.getState());
            System.err.println("PassDate: " + x.getPassDate() + '\n');
        });
    }

    @DisplayName("Перенос даты")
    @ParameterizedTest(name = "Тест #{index} для [{arguments}]")
    @ValueSource(ints = {11525981, 11813019
    })
    void reebase(int id) {
        List<ClientVerificationStep> allByClientId = verificationStepDAO.findAllByClientId(id);
        System.out.println("КОЛ-ВО: " + allByClientId.size());
        allByClientId.forEach(x -> {
            System.out.println(x);
            System.err.println("ID: " + x.getClientId() + ", STEP: " + x.getStep() + ", STATE: " + x.getState());
            System.err.println("PassDate: " + x.getPassDate() + '\n');
        });
    }


    //    @ParameterizedTest(name = "Тест #{index} для [{arguments}]")
//    @ValueSource(ints = {11843252,
//    })




    @Test
//    @Rollback(false)
    void nullPassDateState10() {
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
        System.err.println("SIZE start: " + a10null.size());

        /**
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
        System.out.println("Count of steps with null date and 10 state: " + idsOfCreationData.size());

        Integer id = 11843252;


        steps = new HashMap<>();
        registrtionStepsDAO.findAll().forEach(x -> steps.put(x.getClientId(), x.getRegistrationStageId()));


        List<ClientVerificationStep> byId = verificationStepDAO.findStepObjWithPassDateAnd10Status(id);

        byId.forEach(this::convertAndSave);
        System.err.println(byId);
         */
    }


    private static Map<Integer, Integer> steps;
    private static HashMap<Integer, Timestamp> idsOfCreationData;

    private void convertAndSave(ClientVerificationStep clientVerificationStep) {
        RegistrationSteps registrationSteps = RegistrationSteps.builder()
                .clientId(clientVerificationStep.getClientId())
                .passDate(clientVerificationStep.getPassDate())
                .registrationStageId(18)
                .build();
        System.out.println("BEFORE:");
        System.out.println(registrationSteps);

        Integer verificationStepId = clientVerificationStep.getId();
        if (idsOfCreationData.containsKey(verificationStepId)) {

            Timestamp timestamp = idsOfCreationData.get(verificationStepId);
            registrationSteps.setPassDate(timestamp);

            assertNotEquals(timestamp, null);
        }
        if (steps.get(registrationSteps.getClientId()) != registrationSteps.getRegistrationStageId()
                && registrationSteps.getPassDate() != null
                ) {
            registrtionStepsDAO.saveRegistrationStep(registrationSteps);
        }
        System.out.println("AFTER:");
        System.out.println(registrationSteps);
    }


    @Test
    void v() {
        int id = 11843252;
        Map<Integer, Timestamp> idsOfCreationData = new HashMap<>();
        List<ClientVerificationStep> verificationSteps = verificationStepDAO.findAllByClientId(id)
                .stream()
                .sorted((o1, o2) -> Integer.compare(o2.getStep(), o1.getStep()))
                .filter(s -> s.getState() == 10)
                .collect(Collectors.toList());
        System.err.println(verificationSteps);

        verificationSteps.forEach(v -> idsOfCreationData.put(v.getClientId(), v.getPassDate()));

        System.err.println(idsOfCreationData);


//        List<ClientVerificationStep> allByClientId = verificationStepDAO.findAllByClientId(11525981);
//        System.err.println(allByClientId);
    }

    @ParameterizedTest(name = "Тест #{index} для [{arguments}]")
    @MethodSource("makeIDs")
    @DisplayName("Максимальный VerificationStep [Integer]")
    void getMaxInteger(int id) {
        List<Integer> registrationStage = verificationStepDAO.getRegistrationStages(id);
        System.out.println(registrationStage);
        Integer maxRegistrationStages = verificationStepDAO.getMaxRegistrationStages(id);
        System.out.println("maxStep: " + maxRegistrationStages);
    }


}
