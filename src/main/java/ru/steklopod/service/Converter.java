package ru.steklopod.service;

import com.google.common.base.Joiner;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.steklopod.Starter;
import ru.steklopod.entities.mariadb.UserRebased;
import ru.steklopod.entities.mssql.Client;
import ru.steklopod.entities.mssql.ClientVerificationStep;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static ru.steklopod.Starter.*;
import static ru.steklopod.service.Checker.checkPhone;
import static ru.steklopod.service.Checker.makeCountList;

@Service
public class Converter {
    @Value("${csv.filename}")
    private String filename;
    @Autowired
    private RegStepsService regStepsService;
    public static Map<Long, String> regStepsOfTsupisHashMap = new HashMap<>();

    @SneakyThrows
    public UserRebased convertUserForRebase(Client client) {
        Starter.count++;
        checkPhone(client);
        makeCountList(client);
        if (Checker.isTrash(client)) {
            return null;
        } else {
            Integer id = client.getId();
            String email = client.getEmail();
            String phone = client.getPhone();
            String login = client.getLogin();
            addGeneralLogins(login);
            addgeneralEmails(email);
            addIdToSet(id);

            Optional<String> region = Optional.ofNullable(regionsAlpha3Diction.get(client.getRegion()));

            Timestamp lastModify = client.getLastModify();
            Timestamp registrationDate = client.getRegistrationDate();
            long duration = (long) (3600 * 1000);
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
            Optional<Timestamp> passportDateOption = Optional.ofNullable(client.getPassportDate());
            ExecutorService executor = new ForkJoinPool();

            Callable<ClientVerificationStep> getVerificationStepTask = () -> regStepsService
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
                        .birthDate(client.getBirthDate())
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
                notificationOptions.ifPresent(o -> convertNotificationOptions(o, userToSave));
                passportDateOption.ifPresent(userToSave::setPassportDate);
                return userToSave;
            };
            Future<UserRebased> userToSaveInFuture = executor.submit(createUserToSaveTask);

            UserRebased userRebased = userToSaveInFuture.get(3, TimeUnit.SECONDS);

            Optional<ClientVerificationStep> clientVerificationStep
                    = Optional.ofNullable(futureMaxStepObj.get(3, TimeUnit.SECONDS));

            clientVerificationStep
                    .ifPresent(x -> setVerificationStepAndOtherParameters(userRebased, x));

            executor.shutdown();

            if (Checker.checkNotConfirmedEmail(userRebased)) {
                return null;
            } else {
                compareUserWithTsupisCSV(userRebased);
                return userRebased;
            }
        }
    }


    private void convertNotificationOptions(Integer notificationOptions, UserRebased userRebased) {
        if (notificationOptions == null) {
            setNotifications(userRebased, false, false);
            return;
        }
        switch (notificationOptions) {
            //Все кроме email // т.е. только sms:
            case 0:
                setNotifications(userRebased, true, true);
                break;
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
        Long customerId = userRebased.getCustomerId();
        if (!idsFromBET.contains(customerId)
                && !regStepsOfTsupisHashMap.containsKey(customerId)) {
            clearPersonalData(userRebased);
        }

        if (userRebased.isIdentifiedInTsupis()) {
            String valueOfStepInCSV = regStepsOfTsupisHashMap.get(customerId);

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
                        setTsupisStatuses(userRebased, null, null, (byte) 2, null);
                        break;
                }
            }
        }
    }

    private static void setFullVerifiedStage(UserRebased userRebased) {
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

    private static void setVerificationStepsToUser(UserRebased userRebased, Integer registrationStageId,
                                                   boolean emailConfirmed, boolean phoneConfirmed, boolean registeredInTsupis,
                                                   boolean identifiedInTsupis, boolean personalityConfirmed) {
        userRebased.setRegistrationStageId(registrationStageId);
        userRebased.setEmailConfirmed(emailConfirmed);
        userRebased.setPhoneConfirmed(phoneConfirmed);
        userRebased.setRegisteredInTsupis(registeredInTsupis);
        userRebased.setIdentifiedInTsupis(identifiedInTsupis);
        userRebased.setPersonalityConfirmed(personalityConfirmed);
    }

    private static void setVerificationStepAndOtherParameters(UserRebased userRebased, ClientVerificationStep clientVerificationStep) {
        Integer partnerMAXStepId = clientVerificationStep.getStep();

        switch (partnerMAXStepId) {
            //email:
            case 1:
                setVerificationStepsToUser(userRebased, 5,
                        true, false, false, false, false);
                userRebased.setPersonalDataState((byte) 1);
                break;
            //цупис:
            case 2:
                setVerificationStepsToUser(userRebased, 2,
                        true, true, false, false, false);
                userRebased.setPersonalDataState((byte) 1);
                break;
            default:
                setFullVerifiedStage(userRebased);
                userRebased.setPersonalDataState((byte) 2);
                break;
        }
    }

    private static void convertPassport(@Nullable String passport, UserRebased userToSave) {
        if (passport != null) {
            userToSave.setDocumentTypeId(1);
            String seria;
            String passportNumber;
            String[] split = passport.trim().split("[\\s,;]");
            String join = Joiner.on("").join(split);
            passportNumber = passport;
            seria = null;
            if (passport.contains("passportRus")) {
                String[] rusPassport = passport.trim().split(";");
                seria = rusPassport[1];
                passportNumber = rusPassport[0];
            } else if (join.length() == 10 && join.matches("[0-9]+")) {
                seria = join.substring(0, 4);
                passportNumber = join.substring(4, join.length());
            } else if (rusPassports.contains(passport)) {
                if (!passport.contains("passportRus")
                        || !(join.length() == 10 && join.matches("[0-9]+"))
                )
                    if (passport.trim().length() == 11) {
                        String[] split2 = passport.split("-");
                        if (split2.length > 1) {
                            seria = split2[0];
                            passportNumber = split2[1];
                        } else {
                            String[] splitSpace = passport.trim().split("\\s+");
                            if (splitSpace.length > 1) {
                                seria = splitSpace[0];
                                passportNumber = splitSpace[1];
                            } else {
                                String[] trimT = passport.trim().split(";");
                                if (trimT.length > 1) {
                                    seria = trimT[1];
                                    passportNumber = trimT[0];
                                }
                            }
                        }
                    } else {
                        String[] trimC = passport.trim().split(";");
                        if (trimC.length > 1) {
                            seria = trimC[1];
                            passportNumber = trimC[0];
                        }
                        String trim = passport.replaceAll("\\s+", "").trim();
                        if (trim.length() == 10) {
                            seria = join.substring(0, 4);
                            passportNumber = join.substring(4, join.length());
                        }
                    }
            } else {
                userToSave.setDocumentTypeId(2);
                Starter.countOfNotRusPassports.getAndIncrement();
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
        userToSave.setResident(citizenship);
        if (citizenship) {
            s = "RUS";
        }
        userToSave.setCitizenship(s);
    }

    private static void convertGender(Integer gender, UserRebased user) {
        if (gender == null) {
            user.setGender(1);
        } else {
            switch (gender) {
                case 1:
                    user.setGender(1);
                    break;
                case 2:
                    user.setGender(2);
                    break;
                default:
                    user.setGender(1);
            }
        }
    }

    public void fillHasMapOfSteps() {
//        TODO - изменить при построении JAR-файла
//            Resource resource = new FileSystemResource(filename);
//            try (InputStream is = resource.getInputStream()) {
//                new BufferedReader(new InputStreamReader(is, "UTF-8")).lines()

        File file = new File(MethodHandles
                .lookup()
                .lookupClass()
                .getClassLoader().getResource(filename).getFile());
        try (Stream<String> stream = Files.lines(file.toPath())) {
            stream
                    .skip(1)
                    .forEach(row -> {
                        String[] strings = row.split(";");
                        Long customerId = Long.parseLong(strings[0]);
                        String step = strings[1];
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
