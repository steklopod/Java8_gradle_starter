package ru.stoloto.service;

import com.google.common.base.Joiner;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.stoloto.entities.mariadb.RegistrtionSteps;
import ru.stoloto.entities.mariadb.UserRebased;
import ru.stoloto.entities.mssql.Client;
import ru.stoloto.entities.mssql.ClientVerificationStep;
import ru.stoloto.repositories.maria.RegistrtionStepsDAO;
import ru.stoloto.repositories.ms.RegionDao;
import ru.stoloto.repositories.ms.VerificationStepDAO;

import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.*;

@Service
public class Converter {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    VerificationStepDAO verificationStepDAO;
    @Autowired
    RegionDao regionDao;
    @Autowired
    RegistrtionStepsDAO registrtionStepsDAO;

    @SneakyThrows
    public UserRebased convertReplacaUser(Client client) {
        ExecutorService executor = new ForkJoinPool();

        Callable<ClientVerificationStep> getVerificationStepTask = () -> {
            Thread.currentThread().setName("Шаг верификации - получение.");
//            Integer maxRegistrationStages = verificationStepDAO.getMaxRegistrationStages(client.getId());
//            return verificationStepDAO.findById(client.getId()).get();
            ClientVerificationStep maxVerificationStepObject
                    = verificationStepDAO.getMaxVerificationStepObject(client.getId());
            return maxVerificationStepObject;
        };
        Future<ClientVerificationStep> futureMaxStepObj = executor.submit(getVerificationStepTask);

        Callable<String> getRegionTask = () -> {
            Thread.currentThread().setName("Регион - получение.");
            return regionDao.getRegion(client.getRegion());
        };
        Future<String> regionFuture = executor.submit(getRegionTask);

        String email = client.getEmail();
        String phone = client.getPhone();
        String password = client.getPasswordHash();
        String login = client.getLogin();
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
        Integer registrationSource = client.getRegistrationSource();
        Integer notificationOptions = client.getNotificationOptions();
        boolean subscribedToNewsletter = client.isSubscribedToNewsletter();
        boolean isActive = client.isActive();

        String region = regionFuture.get(1, TimeUnit.SECONDS);

        Callable<UserRebased> createUserToSaveTask = () -> {
            Thread.currentThread().setName("Создание пользователя для сохранения.");

            UserRebased userToSave = UserRebased.builder()
                    .email(email)
                    .phone(phone)
                    .password(password)
                    .login(login)
                    .registrationDate(registrationDate)
                    .lastModify(lastModify)
                    .firstName(firstName)
                    .patronymic(patronymic)
                    .surname(surname)
                    .birthDate(birthDate)
                    .addressString(addressString)
                    .birthPlace(birthPlace)
                    .city(city)
                    .passportIssuer(passportIssuer)
                    .passportIssuerCode(passportIssuerCode)
                    .customerId(new Long(id))
                    .swarmUserId(new Long(id))
                    .blocked(isActive)
                    .localeID(1)
                    .offerState((byte) 0)
                    .isSubscribedToNewsletter(subscribedToNewsletter)
                    .registrationSource(registrationSource)
                    .notificationOptions(notificationOptions)
                    .region(region)
//                    .registrationStageId(registrationStageId)
                    .build();

            convertAddressToStreet(addressString, userToSave);
            convertPassport(client.getPassport(), userToSave);
            convertCitizenship(client.isCitizen(), userToSave);
            convertGender(client.getGender(), userToSave);

            return userToSave;
        };
        Future<UserRebased> userToSaveInFuture = executor.submit(createUserToSaveTask);

        UserRebased userRebased = userToSaveInFuture.get(1, TimeUnit.SECONDS);
        
        Optional<ClientVerificationStep> clientVerificationStep
                = Optional.ofNullable(futureMaxStepObj.get(1, TimeUnit.SECONDS));
        clientVerificationStep.ifPresent(x -> userRebased.setRegistrationStageId(x.getPartnerKycStepId()));

        return userRebased;
    }

    private void makeRegistrationStepsTable(Integer registrationStageId) {
        RegistrtionSteps registrtionSteps;

        switch (registrationStageId) {
            case 1:
                registrtionSteps = new RegistrtionSteps();
                registrtionStepsDAO.saveAndFlush(registrtionSteps);
                break;
            case 2:
                registrtionSteps = new RegistrtionSteps();
                registrtionStepsDAO.saveAndFlush(registrtionSteps);
                break;
            case 3:
                registrtionSteps = new RegistrtionSteps();
                registrtionStepsDAO.saveAndFlush(registrtionSteps);
                break;
            case 4:
                registrtionSteps = new RegistrtionSteps();
                registrtionStepsDAO.saveAndFlush(registrtionSteps);
                break;
            default:
                break;
        }

    }


    private static void convertPassport(@Nullable String passport, UserRebased userToSave) {
        if (passport != null) {
            String seria;
            String passportNumber;
            if (passport.contains("passportRus")) {
                String[] rusPassport = passport.trim().split(";");
                seria = rusPassport[0];
                passportNumber = rusPassport[1] + ";passportRus";
            } else {
                String[] split = passport.trim().split("[\\s,;]");
                String join = Joiner.on("").join(split);

                if (join.length() == 10 && join.matches("[0-9]+")) {
                    seria = join.substring(0, 4);
                    passportNumber = join.substring(4, join.length()) + ";passportRus";
                    userToSave.setDocumentTypeId(1);
                } else {
                    passportNumber = passport;
                    seria = null;
                    userToSave.setDocumentTypeId(2);
                }
            }
            userToSave.setPassportSeries(seria);
            userToSave.setPassportNumber(passportNumber);
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

    private static void convertCitizenship(boolean citizenship, UserRebased userToSave) {
        String s = null;
        if (citizenship) {
            s = "RUS";
        }
        userToSave.setCitizenship(s);
    }

    private static void convertGender(@Nullable Integer gender, UserRebased user) {
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

}
