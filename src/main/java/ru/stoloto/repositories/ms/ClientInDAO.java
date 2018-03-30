package ru.stoloto.repositories.ms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.entities.mssql.Client;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional("MsSqlTtransactionManager")
public interface ClientInDAO extends JpaRepository<Client, Integer> {

    String GET_ALL_REGIONS_SQL = "SELECT RegionId FROM Client ORDER BY RegionId ASC";
    String GET_ALL_IDS_SQL = "SELECT DISTINCT Id FROM Client";
    String GET_ALL_X = "SELECT * FROM Client WHERE Email NOT IN (?1)";
    String GET_ALL_DOC_NUMBER = "SELECT DISTINCT DocNumber FROM Client";

    String GET_ALL_SQL = "SELECT * FROM Client WHERE Phone IN (SELECT DISTINCT Phone FROM Client WHERE Phone IS NOT NULL)";
    String GET_ALL_DIST_EMAIL_AND_PHONE_NOT_NULL_SQL = "SELECT * FROM Client " +
                                    "WHERE Email IN (SELECT DISTINCT Email FROM Client WHERE Email IS NOT NULL) " +
                                    "AND Phone IN (SELECT DISTINCT Phone FROM Client WHERE Phone IS NOT NULL)";
    String ALL_PHONES = "select DISTINCT phone from Client";
    String ALL_LOGINS = "select DISTINCT login from Client";
    String GET_ALL_EMAILS = "SELECT DISTINCT Email FROM Client";
    String ID_BY_PASSPORT = "SELECT ID FROM Client WHERE DocNumber = ?1";

//    @Query(value = GET_ALL_X, nativeQuery = true)
//    List<Client> findAllEmailNotIn(HashSet<String> emails);

    @Query("SELECT COUNT(u) FROM Client u")
    Long selectCount();

    @Query(value = GET_ALL_REGIONS_SQL, nativeQuery = true)
    Set<Integer> getAllRegions();

    List<Client> findAll();

//    @Query(value = GET_ALL_SQL, nativeQuery = true)
    @Query(value = GET_ALL_DIST_EMAIL_AND_PHONE_NOT_NULL_SQL, nativeQuery = true)
    List<Client> findAllClientsDistinctByPhone();

    @Query(value = GET_ALL_EMAILS, nativeQuery = true)
    HashSet<String> findAllEmails();

    @Query(value = GET_ALL_IDS_SQL, nativeQuery = true)
    Set<Integer> findAllIds();

    Optional<Client> findById(@Param("id") Long id);

    @Query(value = ALL_PHONES, nativeQuery = true)
    HashSet<String> findAllPhones();

    @Query(value = GET_ALL_DOC_NUMBER, nativeQuery = true)
    List<String> findAllPassports();

    @Query(value = ALL_LOGINS, nativeQuery = true)
    HashSet<String> findAllLogins();

    @Query(value = ID_BY_PASSPORT, nativeQuery = true)
    Integer findIdByDocNumber(@Param("DocNumber") String passport);

    Client findByEmail(@Param("Email") String email);

}
