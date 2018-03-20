package ru.stoloto.repositories.maria;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.entities.mariadb.RegistrtionSteps;

@Repository
@Transactional("transactionManager")
public interface RegistrtionStepsDAO extends JpaRepository<RegistrtionSteps, Integer>{


}
