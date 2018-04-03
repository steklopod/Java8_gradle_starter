package ru.stoloto.repositories.maria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.Starter;
import ru.stoloto.entities.mariadb.RegistrationSteps;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Repository
@Transactional("transactionManager")
public interface RegistrtionStepsDAO extends JpaRepository<RegistrationSteps, Integer> {
    Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

//    @Transactional(rollbackFor = Throwable.class)
    default void saveRegistrationStep(RegistrationSteps registrationStep){
        try {
            save(registrationStep);
        }catch (Throwable e){
            Starter.countOfStepsRollbacks++;
            System.err.println("CAN'T SAVE REGISTRATION STEP FOR ID ---> " + registrationStep.getClientId());
            e.printStackTrace();
            logger.error(String.valueOf(e.getClass()));
        }
    }

    List<RegistrationSteps> findAll();


}
