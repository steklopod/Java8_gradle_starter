package ru.steklopod.later;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.entities.mariadb.RegStep2Fields;
import ru.stoloto.entities.mariadb.UserEmailConfirm;
import ru.stoloto.entities.mariadb.UserRebased;
import ru.stoloto.entities.mssql.ClientVerificationStep;
import ru.stoloto.repositories.maria.RegStep2FieldsDAO;
import ru.stoloto.repositories.maria.UserEmailConfirmationCodeDAO;
import ru.stoloto.repositories.maria.UserOutDAO;
import ru.stoloto.repositories.ms.ClientInDAO;
import ru.stoloto.repositories.ms.VerificationStepDAO;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@Transactional
@Disabled
class RegStepsAfter {
    @Autowired
    private UserOutDAO userOutDAO;
    @Qualifier("jdbcMsSql")
    @Autowired
    private JdbcTemplate jdbcTemplateMsSql;
    @Qualifier("jdbcMaria")
    @Autowired
    private JdbcTemplate jdbcTemplateMaria;
    @Autowired
    private VerificationStepDAO verificationStepDAO;

    @Autowired
    private ClientInDAO repositoryMsSql;

    @Autowired
    private RegStep2FieldsDAO regStep2FieldsDAO;

    @Autowired
    private UserEmailConfirmationCodeDAO emailConfirmationCodeDAO;
    private static volatile Map<Integer, Timestamp> idsOfCreationData;
    private static volatile Set<Integer> idsWith10StateInBet;
    private static volatile List<Integer> allUserIds;
    private static volatile List<Integer> allUsEmCoCodeIDS;


    @Test
    @Rollback(false)
    void makeTableOfSteps() {
        try (Stream<UserRebased> userRebasedStream = userOutDAO.findAll().parallelStream()) {
            userRebasedStream.forEach(user ->
                    regStep2FieldsDAO.save(new RegStep2Fields(user.getCustomerId().intValue(), user.getRegistrationStageId())));
        }
    }


    @Test
    void newIDS() {
        HashSet<Integer> allIdsFromBet = repositoryMsSql.findAllIds();
        System.err.println("all Ids From Bet before: " + allIdsFromBet.size());

        List<Integer> allUserIds
                = jdbcTemplateMaria.queryForList("SELECT DISTINCT customer_id FROM test.user", Integer.class);
        System.err.println("all user.id before: " + allUserIds.size());

        allIdsFromBet.addAll(allUserIds);

        System.err.println("all general Ids after merge: " + allIdsFromBet.size());
    }


    @Test
    @Rollback(false)
    void newIDS2() {
        List<Integer> allUserIds = jdbcTemplateMaria.queryForList(
                "SELECT DISTINCT customer_id FROM test.user", Integer.class);
        System.err.println("all User Ids before: " + allUserIds.size());

        List<Integer> allUsEmCoCodeIDS = jdbcTemplateMaria.queryForList(
                "SELECT DISTINCT user_id FROM  test.useremailconfirmationcode", Integer.class);

        allUserIds.removeAll(allUsEmCoCodeIDS);

        allUserIds.forEach(custID -> {
            Integer userId = userOutDAO.convertCustomerIdToUserId(custID).get();
            jdbcTemplateMaria.update("INSERT INTO test.ids_of_creation_data (customer_id, user_id) VALUES (?, ?)"
                    , custID, userId);
        });
        System.err.println("all User Ids after: " + allUserIds.size());
    }


    @Test
    @Rollback(false)
    void rebaseUECC() {
        allUserIds = jdbcTemplateMaria.queryForList(
                "SELECT DISTINCT id FROM test.user", Integer.class);

        allUsEmCoCodeIDS = jdbcTemplateMaria.queryForList(
                "SELECT DISTINCT user_id FROM  test.useremailconfirmationcode",
                Integer.class);

        System.err.println("Id в базе: " + allUserIds.size() + " шт;");
        allUserIds.removeAll(allUsEmCoCodeIDS);
        System.err.println("Id для обработки: " + allUserIds.size() + " шт.");

        fillListsAndSet();
        System.err.println("Необходимые списки для переноса сформированы. idsOfCreationData: " + idsOfCreationData.size() + " шт.");

        try (Stream<Integer> userIDStoSetEmCoCode = allUserIds.stream()
                .parallel()) {

            userIDStoSetEmCoCode.forEach(usID -> {
                Integer cusID = (Integer) userOutDAO.convertUserIdToCustomerId(usID);

                verificationStepDAO
                        .findStepObjWithPassDateAnd10Status(cusID)
                        .forEach(this::convertAndSave);
            });
        }
    }


    @Rollback(false)
    private void convertAndSave(ClientVerificationStep clientVerificationStep) {
        Integer clientId = clientVerificationStep.getClientId();

        Integer userId = userOutDAO.convertCustomerIdToUserId(clientId).get();

        if (!allUsEmCoCodeIDS.contains(userId)) {
            UserEmailConfirm userEmailConfirm = new UserEmailConfirm(clientId);
            if (idsWith10StateInBet.contains(clientId)) {
                userEmailConfirm.setChecked(true);
            }
            Optional<String> code = Optional.ofNullable(clientVerificationStep.getCode());
            Timestamp creationDate = idsOfCreationData.get(clientId);

            if (code.isPresent() && creationDate != null) {
                userEmailConfirm.setCreationDate(creationDate);
                userEmailConfirm.setCode(code.get());
                Timestamp emailConfCode = idsOfCreationData.get(clientVerificationStep.getClientId());

                userEmailConfirm.setEmailConfirmDate(emailConfCode);

                emailConfirmationCodeDAO.save(userEmailConfirm);

                allUsEmCoCodeIDS.add(userId);
            }
        } else {

        }
    }

    @Rollback(false)
    private void fillListsAndSet() {
        Set<Integer> a10null = new HashSet<>();
        Set<Integer> a4Notnull = new HashSet<>();
        Set<Integer> nullPassdateState10 = verificationStepDAO.findNullPassdateState10();
        idsOfCreationData = new HashMap<>();

        System.err.println("nullPassdateState10: " + nullPassdateState10.size());

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

        System.err.println("a10null after retain: " + a10null.size());

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
//                                                creationDataTimeDAO.save(new IdsOfExistingEserEmConfCode(el.getId(), el.getPassDate()));
                                            }
                                        });
                            });
                        }
                );
        a10null.clear();
        a4Notnull.clear();
        nullPassdateState10.clear();

        System.out.println("Список idsOfCreationData готов " + idsOfCreationData.size() + " шт.");


        /**
         * idsWith10StateInBet
         */
        List<Integer> idsWith10State
                = jdbcTemplateMsSql.queryForList("SELECT DISTINCT ClientId FROM ClientVerificationStep WHERE  State = 10", Integer.class);
        idsWith10StateInBet = new HashSet<>(idsWith10State);
        idsWith10State.clear();
        System.out.println("Список idsWith10StateInBet готов");
    }


}


