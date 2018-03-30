package ru.stoloto.repositories.maria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.Starter;
import ru.stoloto.entities.mariadb.UserRebased;

import javax.persistence.PersistenceException;
import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Repository
@Transactional("transactionManager")
public interface UserOutDAO extends JpaRepository<UserRebased, Integer> {
   Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    String DELETE_TEST_USERS = "DELETE FROM user  WHERE registration_stage_id IS NULL";
    String SELECT_COUNT_OF_TEST_USERS = "SELECT count(*) FROM user WHERE registration_stage_id IS NULL";
    String SELECT_COUNT_OF_USERS = "SELECT count(*) FROM user";
    String SKIP = "SET FOREIGN_KEY_CHECKS=0";
    String ADD_COLUMNS_migration_state = "ALTER TABLE user ADD COLUMN IF NOT EXISTS migration_state TINYINT(4)";
    String ADD_COLUMNS_registration_source = "ALTER TABLE user ADD COLUMN IF NOT EXISTS registration_source TINYINT(4)";
    String ADD_COLUMNS_last_modify = "ALTER TABLE user ADD COLUMN IF NOT EXISTS last_modify datetime";
    String ADD_COLUMNS_notify_email = "ALTER TABLE user ADD COLUMN IF NOT EXISTS notify_email bit(1)";
    String ADD_COLUMNS_notify_phone = "ALTER TABLE user ADD COLUMN IF NOT EXISTS notify_phone bit(1)";


    String ALL_EMAILS = "select email from user";
    String ALL_PHONES = "select phone from user";
    String ALL_LOGINS = "select login from user";
    String ALL_IDS = "select id from user";

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


    @Query(value = ALL_EMAILS, nativeQuery = true)
    HashSet<String> findAllEmails();

    @Query(value = ALL_PHONES, nativeQuery = true)
    HashSet<String> findAllPhones();

    @Query(value = ALL_LOGINS, nativeQuery = true)
    HashSet<String> findAllLogins();

    @Query(value = ALL_IDS, nativeQuery = true)
    HashSet<Integer> findAllIds();

    default void addColumns() {
        try {
        addColumnlastModify();
        addColumnMigrState();
        addColumnRegSource();
        addColumnsNotifyEmail();
        addColumnsNotifyPhone();
        } catch (Throwable e) {
        }
    }

    @Transactional(noRollbackFor = Exception.class)
    default void saveUser(UserRebased item, HashSet set) {
        try {
            if (!set.contains(item.getEmail())) {
                save(item);
            }
        } catch (Throwable e) {

        }
    }

//    @Transactional(noRollbackFor = JpaSystemException.class)
    @Transactional(rollbackFor = JpaSystemException.class)
    default void saveUser(UserRebased item) {
        try {
            Starter.counterOfSaved++;
            save(item);
        } catch (PersistenceException | JpaSystemException e) {
//            System.err.println("ОЙ-ой");
//            System.out.println(e.getClass());
        }
    }


    Optional<UserRebased> findById(@Param("id") Integer id);


    @Async
    CompletableFuture<UserRebased> findOneById(@Param("id") Integer id);

    UserRebased findByEmail(@Param("email") String email);
}
