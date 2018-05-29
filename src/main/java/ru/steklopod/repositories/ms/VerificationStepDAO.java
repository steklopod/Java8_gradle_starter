package ru.steklopod.repositories.ms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.steklopod.entities.mssql.ClientVerificationStep;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Repository
@Transactional("MsSqlTtransactionManager")
public interface VerificationStepDAO extends JpaRepository<ClientVerificationStep, Integer> {

    String GET_MAX_REGISTRATION_STAGE = "SELECT * FROM ClientVerificationStep WHERE ClientId = ?1 AND State = 10";
    String NULL_PASSDATE_STATE_10 = "SELECT ClientId FROM ClientVerificationStep  WHERE PassDate IS NULL AND State = 10";

    String GET_REGISTRATION_STAGE = "SELECT ClientVerificationStep.PartnerKYCStepId " +
            "FROM ClientVerificationStep WHERE ClientId = ?1";

    String NOT_CONFIRMED_EMAILS = "Select distinct ClientId from ClientVerificationStep vs\n" +
            "where vs.PartnerKYCStepId = 1\n" +
            "      and State != 10\n" +
            "      and ClientId in (select clientid from Bet where [Source] not in (98,99))";

    @Query(value = GET_MAX_REGISTRATION_STAGE, nativeQuery = true)
    List<ClientVerificationStep> findStepObjWithPassDateAnd10Status(@Param("ClientId") Integer clientId);

    @Query(value = NOT_CONFIRMED_EMAILS, nativeQuery = true)
    Set<Integer> findNotConfirmedEmails();

    @Query(value = NULL_PASSDATE_STATE_10, nativeQuery = true)
    Set<Integer> findNullPassdateState10();

    List<ClientVerificationStep> findAllByClientId(@Param("ClientId") Integer clientId);

    List<ClientVerificationStep> findAll();

    @Query("SELECT COUNT(c) FROM ClientVerificationStep c")
    Long selectCount();

    @Query(value = GET_REGISTRATION_STAGE, nativeQuery = true)
    List<Integer> getRegistrationStages(Integer clientId);

    default Integer getMaxRegistrationStages(Integer clientId) {
        Integer max;
        List<Integer> registrationStages = getRegistrationStages(clientId);
        if (!registrationStages.isEmpty()) {
            max = Collections.max(registrationStages);
        } else {
            max = null;
        }
        return max;
    }

    String NOT_NULL_PASSDATE = "SELECT * FROM ClientVerificationStep WHERE ClientId = ?1 AND PassDate IS NOT NULL";
    @Query(value = NOT_NULL_PASSDATE, nativeQuery = true)
    List<ClientVerificationStep> findAllNotNullPassdate(@Param("ClientId") Integer clientId);
}
