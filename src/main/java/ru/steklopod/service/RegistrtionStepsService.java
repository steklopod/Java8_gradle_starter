package ru.steklopod.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.steklopod.Starter;
import ru.steklopod.entities.mariadb.RegistrationSteps;
import ru.steklopod.entities.mssql.ClientVerificationStep;
import ru.steklopod.repositories.maria.RegistrtionStepsDAO;

import java.util.HashMap;

@Service
public class RegistrtionStepsService {
    @Autowired
    RegistrtionStepsDAO registrtionStepsDAO;
    public static int countOfSavedSteps;

    public void convertAndSave(ClientVerificationStep clientVerificationStep) {
        RegistrationSteps registrationSteps = RegistrationSteps.builder()
                .clientId(clientVerificationStep.getClientId())
                .passDate(clientVerificationStep.getPassDate())
                .build();
        setVerificationSteps(registrationSteps, clientVerificationStep);

        if(Starter.steps.get(registrationSteps.getClientId()) != registrationSteps.getRegistrationStageId()) {
            registrtionStepsDAO.saveRegistrationStep(registrationSteps);
//            countOfSavedSteps++;
        }
        Starter.countOfExistingSteps++;
    }

    public boolean isNeddToRebase(HashMap map){
        boolean b = false;

        return b;
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
}
