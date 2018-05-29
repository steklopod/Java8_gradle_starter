package ru.steklopod.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.steklopod.Starter;
import ru.steklopod.entities.mariadb.RegStep2Fields;
import ru.steklopod.entities.mariadb.RegistrationSteps;
import ru.steklopod.entities.mariadb.UserEmailConfirm;
import ru.steklopod.entities.mssql.ClientVerificationStep;
import ru.steklopod.repositories.maria.RegStep2FieldsDAO;
import ru.steklopod.repositories.maria.RegistrtionStepsDAO;
import ru.steklopod.repositories.maria.UserEmailConfirmationCodeDAO;
import ru.steklopod.repositories.maria.UserOutDAO;
import ru.steklopod.repositories.ms.VerificationStepDAO;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static ru.steklopod.Starter.*;

@Service
public class RegStepsService {
    @Autowired
    private RegistrtionStepsDAO registrtionStepsDAO;
    @Autowired
    private UserEmailConfirmationCodeDAO emailConfirmationCodeDAO;
    @Autowired
    private VerificationStepDAO verificationStepDAO;
    @Autowired
    private UserOutDAO userOutDAO;
    @Qualifier("jdbcMaria")
    @Autowired
    private JdbcTemplate jdbcTemplateMaria;
    @Qualifier("jdbcMsSql")
    @Autowired
    private JdbcTemplate jdbcTemplateMsSql;
    @Autowired
    private RegStep2FieldsDAO regStep2FieldsDAO;

    public void convertAndSave(ClientVerificationStep clientVerificationStep) {
        RegistrationSteps currentRegistrationStep = RegistrationSteps.builder()
                .clientId(clientVerificationStep.getClientId())
                .passDate(clientVerificationStep.getPassDate())
                .build();
        setVerificationSteps(currentRegistrationStep, clientVerificationStep);
        Integer verificationStepId = clientVerificationStep.getId();
        if (idsOfCreationData.containsKey(verificationStepId)) {
            Timestamp timestamp = idsOfCreationData.get(verificationStepId);
            currentRegistrationStep.setPassDate(timestamp);
        }
        Integer clientIdToConvert = currentRegistrationStep.getClientId();
        Optional<Integer> id = Optional.ofNullable(Starter.savedIds.get(clientIdToConvert.longValue()));

        final Integer clientId;

        if (id.isPresent()) {
            clientId = id.get();
            currentRegistrationStep.setClientId(clientId);
            Integer currentRegistrationStageId = currentRegistrationStep.getRegistrationStageId();
            RegStep2Fields currentStepWithId = new RegStep2Fields(clientId, currentRegistrationStageId);
            if (!steps.contains(currentStepWithId) && currentRegistrationStep.getPassDate() != null) {
                steps.add(currentStepWithId);

                if (isNeedToBeSavedDAO(clientId, currentRegistrationStageId)) {
                    registrtionStepsDAO.saveRegistrationStep(currentRegistrationStep);
                    Starter.countOfExistingSteps++;
                }
            }
        }
        convertAndSaveToEmailConfirmCode(clientVerificationStep);
    }


    private boolean isNeedToBeSavedDAO(Integer clientId, Integer currentRegistrationStageId) {
        Optional<List<RegStep2Fields>> regSteps = Optional.ofNullable(regStep2FieldsDAO.findByIdMy(clientId));
        if (regSteps.isPresent()) {
            List<RegStep2Fields> currentStages = regSteps.get();
            for (RegStep2Fields stageId : currentStages) {
                if (regStepConvertDictionary.get(currentRegistrationStageId)
                        >
                        regStepConvertDictionary.get(stageId.getRegistrationStageId())) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }


    private void convertAndSaveToEmailConfirmCode(ClientVerificationStep clientVerificationStep) {
        Integer clientId = clientVerificationStep.getClientId();
        if (!idsFromEmailChecking.contains(clientId)) {
            UserEmailConfirm userEmailConfirm = new UserEmailConfirm(clientId);
            if (idsWith10StateInBet.contains(clientId)) {
                userEmailConfirm.setChecked(true);
            }
            Optional<String> code = Optional.ofNullable(clientVerificationStep.getCode());
            Timestamp passDate = idsOfCreationData.get(clientId);

            if (code.isPresent() && passDate != null) {
                userEmailConfirm.setCreationDate(clientVerificationStep.getCreated());
                userEmailConfirm.setCode(code.get());
                userEmailConfirm.setEmailConfirmDate(passDate);

                emailConfirmationCodeDAO.save(userEmailConfirm);
                Starter.addIdInEmailChecking(clientId);
            }
        }
    }

    public static void setVerificationSteps(RegistrationSteps registrationStepsObject, ClientVerificationStep clientVerificationStep) {
        Integer partnerMAXStepId = clientVerificationStep.getStep();

        boolean isCompleted = false;
        if (clientVerificationStep.getState() == 10) {
            isCompleted = true;
        }
        switch (partnerMAXStepId) {
            //email подтвержден:
            case 1:
                if (isCompleted) {
                    registrationStepsObject.setRegistrationStageId(5);
                }
                break;
            //цупис подтвержден:
            case 2:
                if (isCompleted) {
                    registrationStepsObject.setRegistrationStageId(18);
                } else {
                    registrationStepsObject.setRegistrationStageId(19);
                }
                break;
            //скайп подтвержден:
            case 3:
                if (isCompleted) {
                    registrationStepsObject.setRegistrationStageId(2);
                }
                break;
            //18+ подтвержден:
            case 4:
                if (isCompleted) {
                    registrationStepsObject.setRegistrationStageId(2);
                }
                break;
            //депозит подтвержден:
            case 6:
                if (isCompleted) {
                    registrationStepsObject.setRegistrationStageId(2);
                }
                break;
        }
    }

    public ClientVerificationStep getMaxVerificationStepObject(Integer clientId) {
        ClientVerificationStep step = null;

        ArrayList<ClientVerificationStep> registrationStages
                = getClientVerificationSteps(verificationStepDAO.findStepObjWithPassDateAnd10Status(clientId));

        if (registrationStages.size() == 1) {
            step = registrationStages.get(0);
        } else if (registrationStages.size() > 1) {
            step = Collections.max(registrationStages, Comparator.comparingInt(ClientVerificationStep::getStep));
        }
        return step;
    }

    private ArrayList<ClientVerificationStep> getClientVerificationSteps(List<ClientVerificationStep> registrationInList) {

        ArrayList<ClientVerificationStep> registrationStages = new ArrayList<>(registrationInList);

        Long count = registrationInList
                .stream()
                .map(ClientVerificationStep::getPassDate)
                .filter(Objects::isNull)
                .count();
        if (count == 0L) {
            registrationStages.forEach(s -> {
                if (s.getPassDate() == null && s.getState() == 10 && s.getCreated() != null) {
                    Timestamp created = s.getCreated();
                    s.setPassDate(created);
                }
            });
        }
        return registrationStages;
    }


    public List<Integer> getCustomerIDSThatWasEarlierInDB() {
        List<Integer> idsList = jdbcTemplateMsSql.queryForList("SELECT DISTINCT Id FROM dbo.Client", Integer.class);
        List<Integer> allUserIds = jdbcTemplateMaria.queryForList("SELECT DISTINCT customer_id FROM user", Integer.class);

        allUserIds.removeAll(idsList);
        if (allUserIds.isEmpty()) {
            throw new RuntimeException("Ooops idsList is empty");
        }
        return allUserIds;
    }


    public List<Integer> getIDSFromUserEmailConfirmAfterRebase() {
        List<Integer> allUserIds = jdbcTemplateMaria.queryForList(
                "SELECT DISTINCT id FROM user", Integer.class);

        List<Integer> allUsEmCoCodeIDS = jdbcTemplateMaria.queryForList(
                "SELECT DISTINCT user_id FROM  useremailconfirmationcode", Integer.class);

        allUserIds.removeAll(allUsEmCoCodeIDS);

        return allUserIds
                .parallelStream()
                .map(x -> userOutDAO.convertUserIdToCustomerId(x))
                .collect(Collectors.toList());
    }


}
