package ru.stoloto.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.stoloto.entities.mariadb.UserRebased;
import ru.stoloto.entities.mssql.Client;

import java.lang.invoke.MethodHandles;
import java.util.Calendar;
import java.util.Date;

@Service
public class Converter {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static UserRebased convertReplacaUser(Client client) {
        UserRebased userToSave = new UserRebased();

        String email = client.getEmail();
        String phone = client.getPhone();
//        TODO - password
//        TODO - registration_stage_id
        String login = client.getLogin();

//        TODO - ???Islocked -> active
        boolean isActive = client.isActive();
        Calendar registrationDate = convertCalendarToDate(client.getRegistrationDate());

        Calendar lastModify = convertCalendarToDate(client.getLastModify());
        int gender = convertGender(client.getGender());
//        TODO - new email
        Date birthDate = client.getBirthDate();
        String firstName = client.getFirstName();
        String patronymic = client.getPatronymic();
        String surname = client.getSurname();
        String citizenship = convertCitizenship(client.getCitizenship());
        String addressString = client.getAddressString();
//        TODO - kladr_code
        String birthPlace = client.getBirthPlace();
        String region = convertRegionIdToString(client.getRegion());
        String city = client.getCity();


        userToSave.setEmail(email);
        userToSave.setPhone(phone);
        userToSave.setPassword("password");
        userToSave.setRegistrationStageId((byte) 4);
        userToSave.setLogin(login);
//        TODO
        userToSave.setActive(isActive);
        userToSave.setRegistrationDate(registrationDate);
        userToSave.setLastModify(lastModify);
        userToSave.setGender(gender);
        userToSave.setBirthDate(birthDate);
        userToSave.setFirstName(firstName);
        userToSave.setPatronymic(patronymic);
        userToSave.setSurname(surname);
        userToSave.setCitizenship(citizenship);
        userToSave.setAddressString(addressString);
//        TODO
        userToSave.setKladrCode(null);
        userToSave.setBirthPlace(birthPlace);
        userToSave.setRegion(region);


//        TODO - расскоментировать
//        String[] addressArray = convertAddressToStreet(addressString, userToSave);
        userToSave.setStreet("ул. Окская ");
        userToSave.setHouseNumber("дом 3");
        userToSave.setBuilding("стр. 2");
        userToSave.setHousing("корпус 2");
        userToSave.setApartment("кв. 254");

        return userToSave;
    }

    private static String[] convertAddressToStreet(String addressString, UserRebased userToSave) {
        if (addressString != null) {
            String[] addressArr = addressString.split("", 6);
            if (addressArr.length == 6) {
                userToSave.setStreet(addressArr[0]);
                userToSave.setHouseNumber(addressArr[2]);
                userToSave.setBuilding(addressArr[3]);
                userToSave.setHousing(addressArr[1]);
                userToSave.setApartment(addressArr[4]);
            }
            return addressArr;
        }
        return null;
    }

    private static String convertRegionIdToString(Integer region) {

        return null;
    }

    private static Calendar convertCalendarToDate(Date registrationDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(registrationDate);
        return calendar;
    }


    private static String convertCitizenship(Integer citizenship) {
        if (citizenship == 1) {
            return "RUS";
        } else {
            return null;
        }
    }

    //    TODO - 1 / 2 / NULL -> [1 - NOT_SPECIFIED, 2- MALE, 3- FEMALE"]
    private static int convertGender(Integer gender) {

        return 1;
    }
}
