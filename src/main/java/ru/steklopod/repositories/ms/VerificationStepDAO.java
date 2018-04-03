package ru.steklopod.repositories.ms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.steklopod.entities.mssql.ClientVerificationStep;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Repository
@Transactional("MsSqlTtransactionManager")
public interface VerificationStepDAO extends JpaRepository<ClientVerificationStep, Integer> {

    String GET_MAX_REGISTRATION_STAGE = "SELECT * FROM ClientVerificationStep WHERE ClientId = ?1 AND PassDate IS NOT NULL";

    @Query(value = GET_MAX_REGISTRATION_STAGE, nativeQuery = true)
    List<ClientVerificationStep> findStepObjWithPassDateAnd10Status(@Param("ClientId") Integer clientId);

    List<ClientVerificationStep> findAllByClientId(@Param("ClientId") Integer clientId);

    List<ClientVerificationStep> findAll();

    default ClientVerificationStep getMaxVerificationStepObject(Integer clientId) {
        List<ClientVerificationStep> registrationStages = findStepObjWithPassDateAnd10Status(clientId);
        if (registrationStages.size() == 1) {
            return registrationStages.get(0);
        } else if (registrationStages.size() > 1) {
            return Collections.max(registrationStages, Comparator.comparingInt(ClientVerificationStep::getStep));
        } else {
            return null;
        }
    }

    @Query("SELECT COUNT(c) FROM ClientVerificationStep c")
    Long selectCount();

    String GET_REGISTRATION_STAGE = "SELECT ClientVerificationStep.PartnerKYCStepId " +
            "FROM ClientVerificationStep WHERE ClientId = ?1";

    @Query(value = GET_REGISTRATION_STAGE, nativeQuery = true)
    List<Integer> getRegistrationStages(Integer clientId);

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
}
