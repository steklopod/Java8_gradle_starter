package ru.steklopod.later;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.repositories.maria.UserEmailConfirmationCodeDAO;
import ru.stoloto.repositories.maria.UserOutDAO;
import ru.stoloto.repositories.ms.ClientInDAO;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Stream;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@Transactional
@Disabled
class AfterRebase {
    @Autowired
    private UserOutDAO userOutDAO;
    @Autowired
    private UserEmailConfirmationCodeDAO userEmailConfirmationCodeDAO;
    @Autowired
    private ClientInDAO clientInDAO;
    @Qualifier("jdbcMsSql")
    @Autowired
    private JdbcTemplate jdbcTemplateMsSql;
    @Qualifier("jdbcMaria")
    @Autowired
    private JdbcTemplate jdbcTemplateMaria;


    @Test
    @Rollback(false)
    void fk() {
//        jdbcTemplateMaria.update("ALTER TABLE useremailconfirmationcode  DROP FOREIGN KEY fk_useremailconfirmationcode__user");
//        jdbcTemplateMaria.update("ALTER TABLE useremailconfirmationcode  DROP INDEX fk_useremailconfirmationcode__user");
        jdbcTemplateMaria.update("ALTER TABLE useremailconfirmationcode ADD CONSTRAINT fk_useremailconfirmationcode__user FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)");
    }

    @Test
    void initMap() {
        Map<Integer, Integer> regStepConvertDictionary = new HashMap<Integer, Integer>() {{
            put(4, 1);
            put(5, 2);
            put(9, 3);
            put(17, 4);
            put(18, 5);
            put(19, 6);
            put(2, 7);
        }};
        System.err.println(regStepConvertDictionary.get(2));

    }

    @Test
    @Rollback(false)
    void regSourceInUserTable() {
        List<Integer> idsRegSource
                = jdbcTemplateMaria.queryForList("SELECT DISTINCT customer_id FROM test.user WHERE reg_source IS NULL", Integer.class);
        Set<Integer> ids = new HashSet<>(idsRegSource);
        idsRegSource.clear();
        System.err.println(ids.size());

        try (Stream<Integer> idsStream = ids.parallelStream()) {
//                .limit(5)
            idsStream
                    .forEach(id -> {
                        Integer regSource = clientInDAO.getRegSource(id);
                        updateUserRegSource(regSource, id);
//                        System.err.println("id: " + id + ", regSourceInUserTable: " + regSourceInUserTable);
                    });
        }
    }

    void updateUserRegSource(Integer regSource, Integer customerId) {
        jdbcTemplateMaria.update(
                "UPDATE test.user SET reg_source = ? WHERE customer_id = ?",
                regSource, customerId);
    }


    @Test
    @Rollback(false)
    void emailDate() {
        List<Integer> idsWithEmptyEmailDate
                = jdbcTemplateMaria.queryForList("SELECT DISTINCT id FROM test.user WHERE email_confirmation_date IS NULL", Integer.class);

        System.err.println(idsWithEmptyEmailDate.size());

        try (Stream<Integer> idsStream = idsWithEmptyEmailDate.stream()
                .parallel()
//                .limit(5)
        ) {
            idsStream
                    .forEach(id -> {
                        Timestamp emailDate = userEmailConfirmationCodeDAO.getEmailDate(id);
                        updateDate(emailDate, id);
                    });
        }
    }

    private void updateDate(Timestamp emailDate, Integer id) {
        jdbcTemplateMaria.update(
                "UPDATE test.user SET email_confirmation_date = ? WHERE id = ?",
                emailDate, id);
    }


    @Test
    @Rollback(false)
    void changeCustomerToUserIdEmailConfTable() {
        try (Stream<Integer> allEmailConf = userEmailConfirmationCodeDAO
                .getAllIds()
                .stream()
                .parallel()
//                .limit(5)
        ) {
            allEmailConf
                    .forEach(customerId -> {
                        Optional<Integer> userIdOpt = userOutDAO.convertCustomerIdToUserId(customerId);
                        userIdOpt.ifPresent(userId -> convertCusIdToUserId(userId, customerId));
//                        System.err.println("customerId: " + customerId + ", userId: " + userIdOpt.get());
                    });
        }
    }

    private void convertCusIdToUserId(Integer userId, Integer customerId) {
        jdbcTemplateMaria.update(
                "UPDATE test.useremailconfirmationcode SET user_id = ? WHERE user_id = ?",
                userId, customerId);
    }
}



