package ru.stoloto.repositories.maria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.Starter;
import ru.stoloto.entities.mariadb.UserRebased;
import ru.stoloto.service.Checker;

import javax.persistence.PersistenceException;
import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Repository
@Transactional("transactionManager")
public interface UserOutDAO extends JpaRepository<UserRebased, Integer> {
    Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    String DELETE_TEST_USERS = "DELETE FROM user  WHERE registration_stage_id IS NULL";
    String SELECT_COUNT_OF_TEST_USERS = "SELECT count(*) FROM user WHERE registration_stage_id IS NULL";
    String SELECT_COUNT_OF_USERS = "SELECT count(*) FROM user";
    String SKIP = "SET FOREIGN_KEY_CHECKS=0";
    String FIND_BY_ID = "SELECT * FROM user WHERE customer_ID = ?1";

    String ADD_COLUMNS_migration_state = "ALTER TABLE user ADD COLUMN IF NOT EXISTS migration_state TINYINT(4)";
    String ADD_COLUMNS_registration_source = "ALTER TABLE user ADD COLUMN IF NOT EXISTS registration_source TINYINT(4)";
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

    String ALL_EMAILS = "select DISTINCT LOWER(email) from user";
    String ALL_PHONES = "select DISTINCT phone from user";
    String ALL_LOGINS = "select DISTINCT LOWER(login) from user";
    String ALL_IDS = "select customer_id from user";

    @Query(value = DELETE_TEST_USERS, nativeQuery = true)
    void deleteTestUsers();

    @Query("SELECT COUNT(u) FROM UserRebased u")
    Long selectCountOfUsers();

    @Query(value = SELECT_COUNT_OF_TEST_USERS, nativeQuery = true)
    int selectCountOfTestUsers();

    @Query(value = SKIP, nativeQuery = true)
    void skipForeignKey();

    @Query(value = ADD_COLUMNS_migration_state, nativeQuery = true)
    void addColumnMigrState();

    @Query(value = ADD_COLUMNS_registration_source, nativeQuery = true)
    void addColumnRegSource();

    @Query(value = ADD_COLUMNS_last_modify, nativeQuery = true)
    void addColumnlastModify();

    @Query(value = ADD_COLUMNS_notify_email, nativeQuery = true)
    void addColumnsNotifyEmail();

    @Query(value = ADD_COLUMNS_notify_phone, nativeQuery = true)
    void addColumnsNotifyPhone();

    @Query(value = DROP_TABLE_OF_EXEPTIONS, nativeQuery = true)
    void dropExTable();

    @Query(value = CREATE_TABLE_OF_EXEPTIONS, nativeQuery = true)
    void createTableOfExeptions();

    @Query(value = ALL_EMAILS, nativeQuery = true)
    HashSet<String> findAllEmails();

    @Query(value = ALL_PHONES, nativeQuery = true)
    HashSet<String> findAllPhones();

    @Query(value = ALL_LOGINS, nativeQuery = true)
    HashSet<String> findAllLogins();

    @Query(value = ALL_IDS, nativeQuery = true)
    Set<Integer> findAllIds();

    default void addColumns() {
        try {
//            dropExTable();
//            createTableOfExeptions();
            addColumnlastModify();
            addColumnMigrState();
            addColumnRegSource();
            addColumnsNotifyEmail();
            addColumnsNotifyPhone();
        } catch (Throwable e) {
            System.err.println("CAN'T ADD COLUMNS ---> :-(");
            System.exit(1);
        }
    }


    //    @Transactional(rollbackFor = PersistenceException.class)
    default void saveUser(UserRebased userRebased) {
        Starter.counterOfSaved++;
        try {
            save(userRebased);
            if(Checker.isNotConfirmedEmailWithBets(userRebased)){
                Starter.notConfirmedEmailWithBets++;
            }
        } catch (PersistenceException e) {
//            System.err.println(userRebased);
            Starter.countOfUserRollbacks++;
//            System.out.println(e.getClass());
        }
    }

    Optional<UserRebased> findById(@Param("id") Integer id);


    @Query(value = FIND_BY_ID, nativeQuery = true)
    UserRebased findByIdMy(@Param("id") Integer id);

    @Async
    CompletableFuture<UserRebased> findOneById(@Param("id") Integer id);

    UserRebased findByEmail(@Param("email") String email);
}
