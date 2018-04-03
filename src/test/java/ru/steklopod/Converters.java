package ru.steklopod;

import com.google.common.base.Joiner;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.Nullable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.steklopod.entities.mariadb.UserRebased;
import ru.steklopod.entities.mssql.Client;
import ru.steklopod.repositories.maria.UserOutDAO;
import ru.steklopod.repositories.ms.ClientInDAO;
import ru.steklopod.repositories.ms.RegionDao;
import ru.steklopod.service.Converter;

import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@Transactional
@DisplayName("Тесты конвертации типов")
@Disabled
class Converters {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private Converter converter;
    @Autowired
    private UserOutDAO userOutDAO;
    @Autowired
    private ClientInDAO repositoryMsSql;
    @Autowired
    private RegionDao regionDao;

    private static final Integer id = 11563150;
    private static int countsOfNotRussPassport = 0;
    private static int countsOfRussianPassports = 0;

    @ParameterizedTest(name = "Тест #{index} для ID № [{arguments}]")
    @ValueSource(ints = {123, 7})
    void region(int id) {
        String region = regionDao.getRegion(id);
        System.err.println(region);
    }

    @Test
    void milliseconds() {
        UserRebased user = userOutDAO.findByEmail("gaponova@mail.ruu");
        Client client = repositoryMsSql.findByEmail("gaponova@mail.ruu");

        Timestamp lastModifyUser = user.getLastModify();
        Timestamp lastModifyClient = client.getLastModify();

        System.err.println("<- Created  " + lastModifyUser + "; -> last_modify: " + lastModifyClient);
        Long duration = (long) (3600 * 1000);
        lastModifyUser.setTime(lastModifyUser.getTime() + duration);

        System.err.println("С добавкой: " + lastModifyUser);

    }


    @Test
    void passport() {
        List<String> allPassports = repositoryMsSql.findAllPassports();
        allPassports.forEach(Converters::convertPassport);

        System.err.println("русских: " + countsOfRussianPassports);
        System.err.println("не русских: " + countsOfNotRussPassport);
        System.err.println("всего: " + allPassports.size());

        Set<String> notValidPassports = new HashSet<>();


        System.out.println(notValidPassports);
    }


    private static void convertPassport(@Nullable String passport) {
        if (passport != null) {
            String seria;
            String passportNumber;
            if (passport.contains("passportRus")) {
                String[] rusPassport = passport.trim().split(";");
                seria = rusPassport[0];
                passportNumber = rusPassport[1];
                countsOfRussianPassports++;
            } else {
                String[] split = passport.trim().split("[\\s,;]");
                String join = Joiner.on("").join(split);
                if (join.length() == 10 && join.matches("[0-9]+")) {
                    seria = join.substring(0, 4);
                    passportNumber = join.substring(4, join.length());
                    countsOfRussianPassports++;
                } else {
                    passportNumber = passport;
                    countsOfNotRussPassport++;
                }
            }
            if (passportNumber.length() < 11) {
            } else {
//                notValidPassports.add(passportNumber);
                countsOfNotRussPassport++;
                System.err.println(passportNumber);
            }
        }
    }


    @Test
    @DisplayName("Парсинг номера пасспорта")
    void parsePassport() {
        String passport_1 = "8310 049875";
        String passport_2 = "83,13-166218";
        String passport_3 = "8310067149";
        String passport_4 = "82 09 665743";
        String passport_5 = "A2 09 66585743";
        String passport_6 = "82 09 66574G";

        List<String> passports = new ArrayList<>(Arrays.asList(passport_1, passport_2, passport_3, passport_4, passport_5, passport_6));
        for (String s : passports) {
            String[] split = s.trim().split("[\\s,;-]");
            String join = Joiner.on("").join(split);
            if (join.length() == 10 && join.matches("[0-9]+")) {
                String passportRus = join.substring(0, 4) + " " + join.substring(4, join.length()) + " passportRus";
                System.err.println(passportRus);
            } else {
                System.out.println(s);
            }
        }
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
