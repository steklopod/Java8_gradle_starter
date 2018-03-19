package ru.stoloto.service;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.stoloto.entities.mariadb.UserRebased;
import ru.stoloto.entities.mssql.Client;

import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@Service
public class Converter {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static UserRebased convertReplacaUser(Client client) {
        String email = client.getEmail();
        String phone = client.getPhone();
        String password = "salt:" + client.getPasswordSalt().toString() + ", " + " hash: " + client.getPasswordHash();
//        TODO - password
//        TODO - registration_stage_id
        String login = client.getLogin();
//        TODO - ???Islocked -> active
        boolean isActive = client.isActive();
        Timestamp registrationDate = client.getRegistrationDate();
        Timestamp lastModify = client.getLastModify();
        Date birthDate = client.getBirthDate();
        String firstName = client.getFirstName();
        String patronymic = client.getPatronymic();
        String surname = client.getSurname();
        String addressString = client.getAddressString();
        String birthPlace = client.getBirthPlace();
        String city = client.getCity();
        String passportIssuer = client.getPassportIssuer();
        String passportIssuerCode = client.getPassportIssuerCode();
        Integer id = client.getId();
        Integer notificationOptions = client.getNotificationOptions();

        boolean subscribedToNewsletter = client.isSubscribedToNewsletter();

        UserRebased userToSave = UserRebased.builder()
                .email(email)
                .phone(phone)
                .password(password)
//                TODO - registration_stage_id
                .login(login)
                .registrationDate(registrationDate)
                .lastModify(lastModify)
                .firstName(firstName)
                .patronymic(patronymic)
                .surname(surname)
                .birthDate(birthDate)
//                .region(region)
//                .gender(gender)
//                .kladrCode(null)
//                .newEmail(null)
//                .citizenship(citizenship)
                .addressString(addressString)
                .birthPlace(birthPlace)
                .city(city)
                .passportIssuer(passportIssuer)
                .passportIssuerCode(passportIssuerCode)
                .customerId(new Long(id))
                .swarmUserId(new Long(id))
                .localeID(638) // ???
                .offerState((byte) 0)
                .isSubscribedToNewsletter(subscribedToNewsletter)
                .registrationSource(42)
                .notificationOptions(notificationOptions)
                .build();

//      TODO - сделать конвертер регионов
        convertRegionIdToString(client.getRegion(), userToSave);

        convertPassport(client.getPassport(), userToSave);
        convertCitizenship(client.isCitizen(), userToSave);
        convertAddressToStreet(addressString, userToSave);
        convertGender(client.getGender(), userToSave);

        return userToSave;
    }

    private static void convertPassport(@Nullable String passport, UserRebased userToSave) {
        if (passport != null) {
            String[] split = passport.trim().split("[\\s,;-]");
            String join = Joiner.on("").join(split);
            if (join.length() == 10 && join.matches("[0-9]+")) {
                String seria = join.substring(0, 4);
                String passportNumber = join.substring(4, join.length());
//            String passportRus = seria + " " + passportNumber;
                userToSave.setPassportSeries(seria);
                userToSave.setPassportNumber(passportNumber + " passportRus");
//                TODO - ??? Тип документа удостоверяющего личность
                userToSave.setDocumentTypeId(1);
            } else {
                userToSave.setPassportNumber(passport);
//                TODO - ??? Тип документа удостоверяющего личность
                userToSave.setDocumentTypeId(0);
            }
        }
    }

    private static void convertAddressToStreet(@Nullable String addressString, UserRebased userToSave) {
        if (addressString != null && addressString != "") {
            String[] addressArr = addressString.split(";", 6);
            if (addressArr.length == 6) {
                userToSave.setStreet(addressArr[0]);
                userToSave.setHouseNumber(addressArr[2]);
                userToSave.setBuilding(addressArr[3]);
                userToSave.setHousing(addressArr[1]);
                userToSave.setApartment(addressArr[4]);
            }
        }
    }

    // TODO - 98шт регионов
    private static void convertRegionIdToString(@Nullable Integer region, UserRebased userToSave) {
        if (region != null) {
            userToSave.setRegion(String.valueOf(region));
        }
//        String region;
//        switch (region) {
//            case 245:
//                region = "Moscow";
//                break;
//            case 239:
//                region = "Peterburg";
//            case 237:
//                region = "Peterburg";
//            case 235:
//                region = "Peterburg";
//            case 234:
//                region = "Peterburg";
//            default:
//                region = "";
//                break;
//        }
//        userToSave.setRegion(region);
    }


    private static void convertCitizenship(boolean citizenship, UserRebased userToSave) {
        String s = null;
        if (citizenship) {
            s = "RUS";
        }
        userToSave.setCitizenship(s);
    }

    private static void convertGender(@Nullable Integer gender, UserRebased user) {
        int answer;
        if (gender == null) {
            user.setGender(1);
        } else {
            switch (gender) {
                case 2:
                    user.setGender(1);
                    break;
                case 3:
                    user.setGender(2);
                    break;
            }
        }
    }

    private static Calendar convertCalendarToDate(Date registrationDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(registrationDate);
        return calendar;
    }
}
