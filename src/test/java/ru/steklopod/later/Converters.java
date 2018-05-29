package ru.steklopod.later;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.entities.mariadb.UserRebased;
import ru.stoloto.entities.mssql.Client;
import ru.stoloto.repositories.maria.UserOutDAO;
import ru.stoloto.repositories.ms.ClientInDAO;
import ru.stoloto.repositories.ms.RegionDao;

import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.benas.randombeans.api.EnhancedRandom.random;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@Transactional
@DisplayName("Тесты конвертации типов")
@Disabled
class Converters {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private UserOutDAO userOutDAO;
    @Autowired
    private ClientInDAO repositoryMsSql;
    @Autowired
    private RegionDao regionDao;

    @Qualifier("jdbcMsSql")
    @Autowired
    private JdbcTemplate jdbcTemplateMsSql;
    @Qualifier("jdbcMaria")
    @Autowired
    private JdbcTemplate jdbcTemplateMaria;

    private static Map<String, Integer> generalPhones;
    private static AtomicInteger samePhones = new AtomicInteger(0);


    @Test
    void convertCustomerIdToUserId() {
        Optional<Integer> integer = userOutDAO.convertCustomerIdToUserId(13210878);
        System.err.println(integer);
    }

    @Test
    void state() {
        List<Integer> integers = jdbcTemplateMaria.queryForList(
                "SELECT DISTINCT id\n" +
                        "  FROM user\n" +
                        "  WHERE registration_stage_id IN (18)"
                , Integer.class);

//        List<Integer> integers = jdbcTemplateMaria.queryForList(
//                "SELECT DISTINCT id\n" +
//                        "  FROM user\n" +
//                        "  WHERE registration_stage_id IN (18)"
//                , Integer.class);


    }

    @Test
    void birthDay() {
        Client client = repositoryMsSql.findById(31961851).get();
        Date birthDate = client.getBirthDate();
        System.err.println(birthDate);
        System.err.println(birthDate.getClass());

        UserRebased random = random(UserRebased.class);
        random.setBirthDate(birthDate);

        System.out.println(random.getBirthDate());
        System.out.println(random.getBirthDate().getClass());

    }

    @Test
    void registrationDate() {
        Client client = repositoryMsSql.findById(31961851).get();
        Timestamp registrationDate = client.getRegistrationDate();

        System.err.println(registrationDate);
        System.err.println(registrationDate.getClass());

        UserRebased random = random(UserRebased.class);
        random.setRegistrationDate(registrationDate);

        System.out.println(random.getRegistrationDate());
        System.out.println(random.getRegistrationDate().getClass());

    }

    @ParameterizedTest(name = "Тест #{index} для ID № [{arguments}]")
    @ValueSource(ints = {123, 7})
    void region(int id) {
        String region = regionDao.getRegion(id);
        System.err.println(region);
    }


    @Test
    @DisplayName("Парсинг строки адреса на составляющие")
    void parseAddress() {
        String addres1 = "ул. 800-летия Москвы 7-1-87";
        String addres2 = "ВОлгоградский пр-т";
        String addres3 = "Первомайская;;4;;66;none";
        String addres4 = "Площадь Победы дом 1 корпус Б квартира 78";
        String addres5 = "16a Arshakunyats Avenue";
        String addres6 = null;
        String addres7 = "";
        String addres8 = "adrrasdasd";
        String addres9 = "Спасская;;12;;143;none";

        List<String> strings = new ArrayList<>(Arrays.asList(addres1, addres2, addres3, addres4, addres5, addres6, addres7, addres8, addres9));
        UserRebased userToSave;
        for (String s : strings) {
            String addressString = s;
            if (addressString != null && addressString != "") {
                String[] addressArr = addressString.split(";", 6);
                System.out.println(addressArr);
                if (addressArr.length == 6) {
                    userToSave = new UserRebased(addressArr[0], addressArr[2], addressArr[3], addressArr[1], addressArr[4]);
                    System.err.println("Улица: " + userToSave.getStreet());
                    System.err.println("Дом: " + userToSave.getHouseNumber());
                    System.err.println("Строение: " + userToSave.getBuilding());
                    System.err.println("Корпус: " + userToSave.getHousing());
                    System.err.println("Кв.: " + userToSave.getApartment());
                }
            }
        }
    }

}
