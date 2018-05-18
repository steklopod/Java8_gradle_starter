package ru.steklopod.repositories.ms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.steklopod.entities.mssql.MsEntity;

import java.util.List;

@Repository
@Transactional("MsSqlTtransactionManager")
public interface MssqlDAO extends JpaRepository<MsEntity, Integer> {

    String AVG_SORCE = "SELECT AVG(Source) FROM Bet WHERE Source IS NOT NULL";
    String GET_SOURCE_BY_ID = "SELECT Source from Bet where ClientId = ?1";

    @Query(value = AVG_SORCE, nativeQuery = true)
    Integer averageSource();

    @Query(value = GET_SOURCE_BY_ID, nativeQuery = true)
    List<Integer> getSourceById(@Param("ClientId") Integer clientId);

    @Query("SELECT COUNT(b) FROM MsEntity b")
    Integer selectCount();

}
