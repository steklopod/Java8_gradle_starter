package ru.steklopod.repositories.maria;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.steklopod.entities.mariadb.MariaEntity;

@Repository
@Transactional("transactionManager")
public interface MariaDAO extends JpaRepository<MariaEntity, Integer> {

}
