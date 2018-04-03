package ru.stoloto.service;

import com.google.common.base.Joiner;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.stoloto.Starter;
import ru.stoloto.entities.mariadb.UserRebased;
import ru.stoloto.entities.mssql.Client;
import ru.stoloto.entities.mssql.ClientVerificationStep;
import ru.stoloto.repositories.ms.VerificationStepDAO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.*;

import static ru.stoloto.Starter.*;
import static ru.stoloto.service.Checker.isNotInBetTable;

@Service
public class Converter {

    @Value("${csv.filename}")
    private String filename;
    @Autowired
    private VerificationStepDAO verificationStepDAO;

    public static HashMap<Long, String> regStepsOfTsupisHashMap = new HashMap<>();

    @SneakyThrows
    public UserRebased convertUserForRebase(Client client) {
        Starter.count++;
        if (Checker.isTrash(client)) {
            return null;
        } else {
            Integer id = client.getId();
            String email = client.getEmail();
            String phone = client.getPhone();
            String login = client.getLogin();
                addGeneralLogins(login);
                addgeneralPhones(phone);
                addgeneralEmails(email);
                addIdToSet(id);

            Optional<String> region = Optional.ofNullable(regionsAlpha3Diction.get(client.getRegion()));
            Date birthDate = client.getBirthDate();
            Timestamp lastModify = client.getLastModify();
            Timestamp registrationDate = client.getRegistrationDate();
            Long duration = (long) (3600 * 1000);
            registrationDate.setTime(registrationDate.getTime() + duration);
            String city = client.getCity();

            String surname = client.getSurname();
            String firstName = client.getFirstName();
            String patronymic = client.getPatronymic();
            String password = client.getPasswordHash();
            String birthPlace = client.getBirthPlace();
            String addressString = client.getAddressString();
            String passportIssuer = client.getPassportIssuer();
            String passportIssuerCode = client.getPassportIssuerCode();
            Integer registrationSource = client.getRegistrationSource();
            boolean isActive = client.isActive();
            Optional<Integer> notificationOptions = Optional.ofNullable(client.getNotificationOptions());

            ExecutorService executor = new ForkJoinPool();

            Callable<ClientVerificationStep> getVerificationStepTask = () -> verificationStepDAO
                    .getMaxVerificationStepObject(client.getId());
            Future<ClientVerificationStep> futureMaxStepObj = executor.submit(getVerificationStepTask);

            Callable<UserRebased> createUserToSaveTask = () -> {
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
                        .countryId(1)
                        .offerState((byte) 0)
                        .registrationSource(registrationSource)
                        .registrationStageId(1)
                        .region(region.orElse(null))
                        .migrationState((byte) 1)
                        .phoneConfirmed(false)
                        .build();
                convertCitizenship(client.isCitizen(), userToSave);
                convertAddressToStreet(addressString, userToSave);
                convertPassport(client.getPassport(), userToSave);
                convertGender(client.getGender(), userToSave);
                notificationOptions.ifPresent(o -> convertNotificationOptions(notificationOptions.get(), userToSave));
                return userToSave;
            };
            Future<UserRebased> userToSaveInFuture = executor.submit(createUserToSaveTask);
            UserRebased userRebased = userToSaveInFuture.get(3, TimeUnit.SECONDS);

            Optional<ClientVerificationStep> clientVerificationStep
                    = Optional.ofNullable(futureMaxStepObj.get(3, TimeUnit.SECONDS));
            clientVerificationStep.ifPresent(x -> setVerificationStepAndOtherParameters(userRebased, clientVerificationStep.get()));

            executor.shutdown();

            if (isNotInBetTable(userRebased)) {
                return null;
            } else {
                compareUserWithTsupisCSV(userRebased);
                return userRebased;
            }
        }
    }

    private void convertNotificationOptions(Integer notificationOptions, UserRebased userRebased) {
        switch (notificationOptions) {
            //Все кроме email // т.е. только sms:
            case 1:
                setNotifications(userRebased, false, true);
                break;
            case 2:
                setNotifications(userRebased, true, false);
                break;
            default:
                setNotifications(userRebased, false, false);
                break;
        }
    }

    private void setNotifications(UserRebased userRebased, Boolean email, Boolean phone) {
        userRebased.setNotifyEmail(email);
        userRebased.setNotifyPhone(phone);
    }

    private void compareUserWithTsupisCSV(UserRebased userRebased) {
        if (userRebased.isIdentifiedInTsupis()) {
            String valueOfStepInCSV = regStepsOfTsupisHashMap.get(userRebased.getCustomerId());
            if (valueOfStepInCSV != null) {
                switch (valueOfStepInCSV) {
                    case "":
                        setTsupisStatuses(userRebased, (byte) 3, null, (byte) 2, (byte) 4);
                        break;
                    case "FULL":
                        setTsupisStatuses(userRebased, (byte) 3, null, (byte) 2, (byte) 4);
                        break;
                    case "LIMITED":
                        setTsupisStatuses(userRebased, (byte) 2, (byte) 1, (byte) 2, (byte) 0);
                        break;
                    case "NOTIDENTIFIED":
                        setTsupisStatuses(userRebased, (byte) 3, null, (byte) 2, (byte) 0);
                        break;
                    //email подтвержден:
                    default:
                        if (!idsFromBET.contains(userRebased.getCustomerId())) {
                            clearPersonalData(userRebased);
                        }
                        setTsupisStatuses(userRebased, null, null, (byte) 2, null);
                        break;
                }
            }
        }
    }

    private void setFullVerifiedStage(UserRebased userRebased) {
        setVerificationStepsToUser(userRebased, 2,
                true, true, true, true, true);
    }

    private void setTsupisStatuses(UserRebased userRebased, Byte tsupisStatus, Byte identState,
                                   Byte identType, Byte tsupisAccountStatus) {
        userRebased.setTsupisStatus(tsupisStatus);
        userRebased.setIdentState(identState);
        userRebased.setIdentType(identType);
        userRebased.setTsupisAccountStatus(tsupisAccountStatus);
    }

    private void setVerificationStepsToUser(UserRebased userRebased, Integer registrationStageId,
                                            boolean emailConfirmed, boolean phoneConfirmed, boolean registeredInTsupis,
                                            boolean identifiedInTsupis, boolean personalityConfirmed) {
        userRebased.setRegistrationStageId(registrationStageId);
        userRebased.setEmailConfirmed(emailConfirmed);
        userRebased.setPhoneConfirmed(phoneConfirmed);
        userRebased.setRegisteredInTsupis(registeredInTsupis);
        userRebased.setIdentifiedInTsupis(identifiedInTsupis);
        userRebased.setPersonalityConfirmed(personalityConfirmed);
    }

    private void setVerificationStepAndOtherParameters(UserRebased userRebased, ClientVerificationStep
            clientVerificationStep) {
        Integer partnerMAXStepId = clientVerificationStep.getStep();

        boolean isCompleted = false;
        if (clientVerificationStep.getState() == 10) {
            isCompleted = true;
        }
        switch (partnerMAXStepId) {
            //email:
            case 1:
                if (isCompleted) {
                    setVerificationStepsToUser(userRebased, 5,
                            true, false, false, false, false);
                } else {
//                    TODO - stage 1 ?
                    setVerificationStepsToUser(userRebased, 1,
                            false, false, false, false, false);
                }
                break;
            //цупис:
            case 2:
                setVerificationStepsToUser(userRebased, 2,
                        true, true, false, false, false);
                if (isCompleted) {
                    userRebased.setPersonalDataState((byte) 1);
                }
                break;
            default:
                setFullVerifiedStage(userRebased);
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
                passportNumber = rusPassport[1];
                userToSave.setDocumentTypeId(1);
            } else {
                String[] split = passport.trim().split("[\\s,;]");
                String join = Joiner.on("").join(split);

                if (join.length() == 10 && join.matches("[0-9]+")) {
                    seria = join.substring(0, 4);
                    passportNumber = join.substring(4, join.length());
                    userToSave.setDocumentTypeId(1);
                } else {
                    passportNumber = passport;
                    seria = null;
                    userToSave.setDocumentTypeId(2);
                    Starter.countOfNotRusPassports++;
                }
            }
            userToSave.setPassportSeries(seria);
            userToSave.setPassportNumber(passportNumber);

//            if (passportNumber.length() < 11) {
//                userToSave.setPassportNumber(passportNumber);
//            } else {
//                userToSave.setDocumentTypeId(2);
//                Starter.countOfNotRusPassports++;
////                userToSave.setDocumentTypeId(2);
//            }
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

    public void fillHasMapOfSteps() {
        Resource resource = new FileSystemResource(filename);
        try (InputStream is = resource.getInputStream()) {
            new BufferedReader(new InputStreamReader(is, "UTF-8")).lines()
                    .skip(1)
                    .forEach(row -> {
                        String[] strings = row.split(";");
                        Long customerId = Long.parseLong(strings[0]);
                        String step = strings[2];
                        regStepsOfTsupisHashMap.put(customerId, step);
                    });
        } catch (NullPointerException | IOException e) {
            throw new RuntimeException("Ooops... CSV File >>> " + filename + "<<< NOT FOUND \n :-(. " +
                    "\n Check the filename in application.yml \n look at csv.filename: ...");
        }
    }

    private void clearPersonalData(UserRebased userRebased) {
        countOfUsersWithoutPersonalData++;

        userRebased.setMigrationState((byte) 2);
        userRebased.setFirstName(null);

        userRebased.setBirthDate(null);
        userRebased.setBirthPlace(null);

        userRebased.setPatronymic(null);
        userRebased.setSurname(null);

        userRebased.setAddressString(null);
        userRebased.setStreet(null);
        userRebased.setHouseNumber(null);
        userRebased.setBuilding(null);
        userRebased.setHousing(null);
        userRebased.setApartment(null);

        userRebased.setPassportNumber(null);
        userRebased.setPassportSeries(null);
        userRebased.setPassportIssuer(null);
        userRebased.setPassportIssuerCode(null);
        userRebased.setPassportDate(null);
        userRebased.setDocumentTypeId(null);

    }

}
