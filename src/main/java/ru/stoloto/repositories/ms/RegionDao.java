package ru.stoloto.repositories.ms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.entities.mssql.Region;

@Repository
@Transactional("MsSqlTtransactionManager")
public interface RegionDao extends JpaRepository<Region, Integer> {

    String GET_REGISTRATION_STAGE = "SELECT TOP 1 Alpha3Code\n" +
            "FROM Region\n" +
            "WHERE Id = ?1 AND Alpha3Code IS NOT NULL";

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    @Query(value = GET_REGISTRATION_STAGE, nativeQuery = true)
    String getRegion(Integer clientId);


    @Query("SELECT COUNT(c) FROM Region c")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    Integer selectCount();
}
