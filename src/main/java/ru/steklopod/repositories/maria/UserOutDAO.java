package ru.steklopod.repositories.maria;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.steklopod.entities.mariadb.UserRebased;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@Repository
@Transactional("transactionManager")
public interface UserOutDAO extends JpaRepository<UserRebased, Integer> {
    String SKIP = "SET FOREIGN_KEY_CHECKS=0";
    String FIND_BY_ID = "SELECT * FROM user WHERE customer_ID = ?1";
    String ALL_EMAILS = "select DISTINCT LOWER(email) from user";
    String ALL_PHONES = "select DISTINCT phone from user";
    String ALL_LOGINS = "select DISTINCT LOWER(login) from user";
    String ALL_IDS = "select DISTINCT customer_id from user";

    String GET_REG_SOURCE_BY_ID = "select registration_stage_id from user where customer_id = ?1 LIMIT 1";
    @Query(value = GET_REG_SOURCE_BY_ID, nativeQuery = true)
    Integer getRegSourceById(@Param("customer_id") Integer id);

    @Query("SELECT COUNT(u) FROM UserRebased u")
    Long selectCountOfUsers();

    @Query(value = "select DISTINCT id from user where customer_id =?1 limit 1", nativeQuery = true)
    Optional<Integer> convertCustomerIdToUserId(@Param("customer_id") Integer id);

    @Query(value = "SELECT customer_id FROM user WHERE id =?1 LIMIT 1", nativeQuery = true)
    Integer convertUserIdToCustomerId(@Param("customer_id") Integer customerId);

    @Query(value = SKIP, nativeQuery = true)
    void skipForeignKey();

    @Query(value = ALL_EMAILS, nativeQuery = true)
    HashSet<String> findAllEmails();

    @Query(value = ALL_PHONES, nativeQuery = true)
    HashSet<String> findAllPhones();

    @Query(value = ALL_LOGINS, nativeQuery = true)
    HashSet<String> findAllLogins();

    @Query(value = ALL_IDS, nativeQuery = true)
    Set<Integer> findAllIds();

    Optional<UserRebased> findById(@Param("customer_id") Integer id);

    @Query(value = FIND_BY_ID, nativeQuery = true)
    UserRebased findByIdMy(@Param("customer_id") Integer id);


    String DELETE_TEST_USERS = "DELETE FROM user  WHERE registration_stage_id IS NULL";
    String SELECT_COUNT_OF_TEST_USERS = "SELECT count(*) FROM user WHERE registration_stage_id IS NULL";
    String SELECT_COUNT_OF_USERS = "SELECT count(*) FROM user";
    String ADD_COLUMNS_migration_state = "ALTER TABLE user ADD COLUMN IF NOT EXISTS migration_state TINYINT(4)";
//    String ADD_COLUMNS_registration_source = "ALTER TABLE user ADD COLUMN IF NOT EXISTS registration_source TINYINT(4)";
    String ADD_COLUMNS_last_modify = "ALTER TABLE user ADD COLUMN IF NOT EXISTS last_modify datetime";
    String ADD_COLUMNS_notify_email = "ALTER TABLE user ADD COLUMN IF NOT EXISTS notify_email bit(1)";
    String ADD_COLUMNS_notify_phone = "ALTER TABLE user ADD COLUMN IF NOT EXISTS notify_phone bit(1)";
    String DROP_TABLE_OF_EXEPTIONS = "DROP TABLE IF EXISTS user_with_exception";
    String CREATE_TABLE_OF_EXEPTIONS = "CREATE TABLE IF NOT EXISTS user_with_exception (\n" +
            "  customer_id       INTEGER NOT NULL,\n" +
            "  isEmailExist      BIT,\n" +
            "  isLoginExist      BIT,\n" +
            "  isPhoneExist      BIT,\n" +
            "  isSameId          BIT,\n" +
            "  isTest            BIT,\n" +
            "  notNullCashDeskId BIT,\n" +
            "  isCustomerIdInBetTable BIT,\n" +
            "  PRIMARY KEY (customer_id)\n" +
            ")\n" +
            "  ENGINE = MyISAM";

}
