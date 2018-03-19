package ru.stoloto.repositories.ms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.entities.mssql.Client;

import java.util.Set;

@Repository
@Transactional("MsSqlTtransactionManager")
public interface ClientInDAO extends JpaRepository<Client, Integer> {

    static final String GET_ALL_REGIONS_SQL = "SELECT RegionId FROM Client ORDER BY RegionId ASC";

//    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
//    Optional<Client> findById(@Param("id") Long id);

    @Query("SELECT COUNT(u) FROM Client u")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    Long selectCount();

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    @Query(value = GET_ALL_REGIONS_SQL, nativeQuery = true)
    Set<Integer> getAllRegions();
}
