package ru.steklopod.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.steklopod.entities.User;

import java.util.Optional;

import static ru.steklopod.entities.User.TABLE_NAME;
import static ru.steklopod.entities.User.USER_ID_COLUMN_NAME;

@Repository
@Transactional("transactionManager")
public interface UserDao extends JpaRepository<User, Integer> {

    String SELECT_1_USER = "SELECT * FROM " + TABLE_NAME + " WHERE " + USER_ID_COLUMN_NAME + " > ?1 limit 1";

    @Query("SELECT COUNT(u) FROM User u")
    Long selectCountOfUsers();

    @Query(value = SELECT_1_USER, nativeQuery = true)
    Optional<User> selectUserNativeQueryLimitOne(@Param("id") Integer id);

}
