package ru.stoloto.repositories.ms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.entities.mssql.ClientVerificationStep;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Repository
@Transactional("MsSqlTtransactionManager")
public interface VerificationStepDAO extends JpaRepository<ClientVerificationStep, Integer> {

    String GET_REGISTRATION_STAGE = "SELECT ClientVerificationStep.PartnerKYCStepId " +
            "FROM ClientVerificationStep " +
            "WHERE ClientId = ?1 AND PassDate IS NOT NULL";

    String GET_VERIFICATION_STEP_Obj = "SELECT * " +
            "FROM ClientVerificationStep " +
            "WHERE ClientId = ?1 AND PassDate IS NOT NULL";

    @Query("SELECT COUNT(c) FROM ClientVerificationStep c")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    Integer selectCount();

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    @Query(value = GET_REGISTRATION_STAGE, nativeQuery = true)
    List<Integer> getRegistrationStages(Integer clientId);


    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    @Query(value = GET_VERIFICATION_STEP_Obj, nativeQuery = true)
    List<ClientVerificationStep> getVerificationStepObject(Integer clientId);


    default Integer getMaxRegistrationStages(Integer clientId) {
        Integer max;
        List<Integer> registrationStages = getRegistrationStages(clientId);
        if (registrationStages.size() > 0) {
            max = Collections.max(registrationStages);
        } else {
            max = null;
        }
        return max;
    }

    default ClientVerificationStep getMaxVerificationStepObject(Integer clientId) {
        ClientVerificationStep max;
        List<ClientVerificationStep> registrationStages = getVerificationStepObject(clientId);
        if (registrationStages.size() == 1) {
            max = registrationStages.get(0);
        } else if (registrationStages.size() > 1) {
            final Comparator<ClientVerificationStep> comparator
                    = Comparator.comparingInt(ClientVerificationStep::getPartnerKycStepId);
            ClientVerificationStep clientVerificationStep = registrationStages.stream()
                    .max(comparator)
                    .get();
            max = clientVerificationStep;
        } else {
            max = null;
        }
        return max;
    }

}
