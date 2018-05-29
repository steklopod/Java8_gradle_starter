package ru.steklopod.repositories.maria;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.steklopod.entities.mariadb.UserEmailConfirm;

import java.sql.Timestamp;
import java.util.Set;

@Repository
@Transactional("transactionManager")
public interface UserEmailConfirmationCodeDAO extends JpaRepository<UserEmailConfirm, Integer> {

    String GET_CONF_DATE = "SELECT email_confirm_date from useremailconfirmationcode where user_id = ?1";
    @Query(value = GET_CONF_DATE, nativeQuery = true)
    Timestamp getEmailDate(@Param("user_id") Integer userId);

    @Query(value = "SELECT DISTINCT user_id from useremailconfirmationcode", nativeQuery = true)
    Set<Integer> getAllIds();



}
