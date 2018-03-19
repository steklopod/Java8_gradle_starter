package ru.stoloto;

import com.google.common.base.Joiner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.entities.mariadb.UserRebased;
import ru.stoloto.repositories.maria.UserOutDAO;
import ru.stoloto.repositories.ms.ClientInDAO;
import ru.stoloto.service.Converter;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@Transactional
@DisplayName("Тесты конвертации типов")
class Converters {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    Converter converter;
    @Autowired
    UserOutDAO userOutDAO;
    @Autowired
    ClientInDAO repositoryMsSql;

    private static final Integer id = 11563150;


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
