package ru.steklopod.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.steklopod.repositories.maria.UserEmailConfirmationCodeDAO;
import ru.steklopod.repositories.maria.UserOutDAO;
import ru.steklopod.repositories.ms.ClientInDAO;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static ru.steklopod.Starter.startTime;
import static ru.steklopod.Starter.steps;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class AfterRebase {
    @Qualifier("jdbcMaria")
    @Autowired
    private JdbcTemplate jdbcTemplateMaria;

    private final UserOutDAO userOutDAO;
    private final UserEmailConfirmationCodeDAO userEmailConfirmationCodeDAO;
    private final ClientInDAO clientInDAO;

    public void makeAfterRebaseWork() {
        Thread.currentThread().setName("Final work");
        steps.clear();

        log.info("Updating `reg_source` in [user] table...");
        regSourceInUserTableUpdateByCustomerId();
        log.info("Ok. `reg_source` in [user] table is updated.");

        log.info("Updating `user_id` in [useremailconfirmationcode] table...");
        changeCustomerToUserIdInEmailConfCodeTable();
        log.info("Ok. `user_id` is updated.");

        log.info("Updating `email_confirmation_date` in [user] table...");
        emailDate();
        log.info("Ok. `email_confirmation_date` is updated.");

        long endTime = System.currentTimeMillis();
        long howLong = endTime - startTime;
        String humanReadableDuration = toHumanReadableDuration(howLong);

        log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        log.info(">>> F I N I S H E D  in " + humanReadableDuration);
        log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        try {
            jdbcTemplateMaria.update("TRUNCATE TABLE rebased_stages");
            jdbcTemplateMaria.update("ALTER TABLE useremailconfirmationcode ADD CONSTRAINT fk_useremailconfirmationcode__user FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)");
        } catch (Exception e) {
            log.error("Can't add FOREIGN KEY `fk_useremailconfirmationcode__user` for [useremailconfirmationcode] table");
        }

    }

    /**
     * 1. Обновление колонки reg_source в таблице [user]
     */
    void regSourceInUserTableUpdateByCustomerId() {
        List<Integer> idsRegSource
                = jdbcTemplateMaria.queryForList("SELECT DISTINCT customer_id FROM user WHERE reg_source IS NULL", Integer.class);
        Set<Integer> ids = new HashSet<>(idsRegSource);
        idsRegSource.clear();
        try (Stream<Integer> idsStream = ids.parallelStream()) {
            idsStream
                    .forEach(id -> {
                        Integer regSource = clientInDAO.getRegSource(id);
                        updateUserRegSource(regSource, id);
                    });
        }
    }
    void updateUserRegSource(Integer regSource, Integer customerId) {
        jdbcTemplateMaria.update(
                "UPDATE user SET reg_source = ? WHERE customer_id = ?",
                regSource, customerId);
    }

    /**
     * 2. Обновление колонки user_id (customer_id => user_id)в таблице [useremailconfirmationcode]
     */
    void changeCustomerToUserIdInEmailConfCodeTable() {
        try (Stream<Integer> allEmailConf = userEmailConfirmationCodeDAO
                .getAllIds()
                .stream()
                .parallel()) {
            allEmailConf
                    .forEach(customerId -> {
                        Optional<Integer> userIdOpt = userOutDAO.convertCustomerIdToUserId(customerId);
                        userIdOpt.ifPresent(userId -> convertCusIdToUserId(userId, customerId));
                    });
        }
    }
    private void convertCusIdToUserId(Integer userId, Integer customerId) {
        jdbcTemplateMaria.update(
                "UPDATE useremailconfirmationcode SET user_id = ? WHERE user_id = ?",
                userId, customerId);
    }

    /**
     *  3. Обновление колонки email_confirmation_date в таблице [user]
     */
    void emailDate() {
        List<Integer> idsWithEmptyEmailDate
                = jdbcTemplateMaria.queryForList("SELECT DISTINCT id FROM user WHERE email_confirmation_date IS NULL", Integer.class);
        try (Stream<Integer> idsStream = idsWithEmptyEmailDate.parallelStream()) {
            idsStream
                    .forEach(id -> {
                        Timestamp emailDate = userEmailConfirmationCodeDAO.getEmailDate(id);
                        updateDate(emailDate, id);
                    });
        }
    }
    private void updateDate(Timestamp emailDate, Integer id) {
        jdbcTemplateMaria.update(
                "UPDATE user SET email_confirmation_date = ? WHERE id = ?",
                emailDate, id);
    }



    private static final List<TimeUnit> timeUnits = Arrays.asList(TimeUnit.DAYS, TimeUnit.HOURS, TimeUnit.MINUTES,
            TimeUnit.SECONDS);
    
    public static String toHumanReadableDuration(final long millis) {
        final StringBuilder builder = new StringBuilder();
        long acc = millis;
        for (final TimeUnit timeUnit : timeUnits) {
            final long convert = timeUnit.convert(acc, TimeUnit.MILLISECONDS);
            if (convert > 0) {
                builder.append(convert).append(' ').append(WordUtils.capitalizeFully(timeUnit.name())).append(", ");
                acc -= TimeUnit.MILLISECONDS.convert(convert, timeUnit);
            }
        }
        return builder.substring(0, builder.length() - 2);
    }
}
