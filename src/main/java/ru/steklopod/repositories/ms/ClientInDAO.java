package ru.steklopod.repositories.ms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.steklopod.entities.mssql.Client;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional("MsSqlTtransactionManager")
public interface ClientInDAO extends JpaRepository<Client, Integer> {

    String GET_ALL_REGIONS_SQL = "SELECT RegionId FROM Client ORDER BY RegionId ASC";
    String GET_ALL_IDS_SQL = "SELECT DISTINCT Id FROM Client"
//            + "where WHERE ClientId IN (13326531)"
            ;
    String GET_ALL_X = "SELECT * FROM Client WHERE Email NOT IN (?1)";
    String GET_ALL_DOC_NUMBER = "SELECT DISTINCT DocNumber FROM Client";

    String GET_ALL_SQL = "SELECT * FROM Client WHERE Phone IN (SELECT DISTINCT Phone FROM Client WHERE Phone IS NOT NULL)";

    //    String GET_ALL_DIST_EMAIL_AND_PHONE_NOT_NULL_SQL = "SELECT * FROM Client WHERE Email IS NOT NULL AND Phone IS NOT NULL";
    String GET_ALL_DIST_EMAIL_AND_PHONE_NOT_NULL_SQL = "SELECT * FROM Client WHERE Phone IS NOT NULL";

    String ALL_PHONES = "SELECT DISTINCT LOWER (Phone) from Client";
    String ALL_LOGINS = "SELECT DISTINCT LOWER (Login) from Client";
    String GET_ALL_EMAILS = "SELECT DISTINCT LOWER (Email) FROM Client";
    String ID_BY_PASSPORT = "SELECT ID FROM Client WHERE DocNumber = ?1";
    String GET_REG_SOURCE = "SELECT DISTINCT  RegistrationSource from Client WHERE Id = ?1";

    String sql = "SELECT DISTINCT *\n" +
            "FROM Client\n" +
            "WHERE Phone IS NOT NULL\n" +
            "      AND Id NOT IN (SELECT DISTINCT ClientId\n" +
            "                     FROM Bet\n" +
            "                     WHERE Source NOT IN (98, 99)\n" +
            "                           AND ClientId IS NOT NULL)\n" +
            "      AND Id NOT IN (SELECT DISTINCT c.Id\n" +
            "                     FROM (SELECT phone\n" +
            "                           FROM Client\n" +
            "                           GROUP BY phone\n" +
            "                           HAVING count(*) > 1) c1\n" +
            "                       JOIN Client c ON c.Phone = c1.Phone)\n" +
            "      AND IsTest != 1\n" +
            "      AND Email IS NOT NULL\n" +
            "      AND Id IN (SELECT DISTINCT ClientId\n" +
            "                 FROM ClientVerificationStep\n" +
            "                 WHERE PartnerKYCStepId IN (1)\n" +
            "                       AND [state] = 10\n" +
            "                       AND ClientId IS NOT NULL)";


    String passportNumberRus = "SELECT DISTINCT DocNumber\n" +
            "FROM Client\n" +
            "WHERE Id NOT IN (\n" +
            "  SELECT c.Id\n" +
            "  FROM Client c\n" +
            "  WHERE DocNumber NOT LIKE '%passportRus'\n" +
            "        AND replace(replace(c.DocNumber, ' ', ''), ';', '') LIKE N'%[A-z, А-я]%'\n" +
            "        AND len(replace(c.DocNumber, ' ', '')) != 10\n" +
            "        AND c.DocNumber != '')";

    String passportNumberNotRus = "SELECT DISTINCT DocNumber\n" +
            "FROM Client\n" +
            "WHERE Id IN (\n" +
            "  SELECT c.Id\n" +
            "  FROM Client c\n" +
            "  WHERE DocNumber NOT LIKE '%passportRus'\n" +
            "        AND replace(replace(c.DocNumber, ' ', ''), ';', '') LIKE N'%[A-z, А-я]%'\n" +
            "        AND len(replace(c.DocNumber, ' ', '')) != 10\n" +
            "        AND c.DocNumber != '')";

    String FIND_ALL_WITH_10_STATE = "SELECT *\n" +
            "FROM Client\n" +
            "where ID in (SELECT DISTINCT ClientId\n" +
            "             FROM ClientVerificationStep\n" +
            "             where State = 10) ";
//           + " AND Id in(13210878)";
//            "11944416,\n" +
//            "12018451,\n" +
//            "12077823,\n" +
//            "12383931,\n" +
//            "12495771,\n" +
//            "12626120,\n" +
//            "12973132,\n" +
//            "13210878,\n" +
//            "13269272,\n" +
//            "13595159,\n" +
//            "13622114\n" +

    @Query("SELECT COUNT(u) FROM Client u")
    Long selectCount();

    @Query(value = GET_ALL_REGIONS_SQL, nativeQuery = true)
    Set<Integer> getAllRegions();

    @Query(value = sql, nativeQuery = true)
    Set<Client> sqls();

    @Query(value = FIND_ALL_WITH_10_STATE, nativeQuery = true)
    List<Client> findAllWith10State();

    //    @Query(value = GET_ALL_SQL, nativeQuery = true)
    @Query(value = GET_ALL_DIST_EMAIL_AND_PHONE_NOT_NULL_SQL, nativeQuery = true)
    List<Client> findAllClientsDistinctByPhone();

    @Query(value = GET_ALL_EMAILS, nativeQuery = true)
    HashSet<String> findAllEmails();

    @Query(value = GET_ALL_IDS_SQL, nativeQuery = true)
    HashSet<Integer> findAllIds();

    Optional<Client> findById(@Param("Id") Long id);

    @Query(value = ALL_PHONES, nativeQuery = true)
    HashSet<String> findAllPhones();

    @Query(value = GET_ALL_DOC_NUMBER, nativeQuery = true)
    List<String> findAllPassports();

    @Query(value = ALL_LOGINS, nativeQuery = true)
    HashSet<String> findAllLogins();

    @Query(value = ID_BY_PASSPORT, nativeQuery = true)
    Integer findIdByDocNumber(@Param("DocNumber") String passport);

    @Query(value = GET_REG_SOURCE, nativeQuery = true)
    Integer getRegSource(@Param("RegistrationSource") Integer regSource);

    @Query(value = passportNumberNotRus, nativeQuery = true)
    Set<String> findPassportNumberNotRus();

    @Query(value = passportNumberRus, nativeQuery = true)
    Set<String> findRussPassports();


    Client findByEmail(@Param("Email") String email);

}
