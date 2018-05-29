package ru.steklopod.later;

import com.google.common.base.Joiner;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.Nullable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.stoloto.entities.mssql.Client;
import ru.stoloto.repositories.maria.UserOutDAO;
import ru.stoloto.repositories.ms.ClientInDAO;

import java.lang.invoke.MethodHandles;
import java.util.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@DisplayName("Тесты конвертации типов")
@Disabled
class Passports {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private UserOutDAO userOutDAO;
    @Autowired
    private ClientInDAO repositoryMsSql;

    private static final Integer id = 11563150;
    private static int countsOfNotRussPassport = 0;
    private static int countsOfRussianPassports = 0;

    private static Map<String, Integer> pasportMap = new HashMap<>();

    private static volatile Set<String> notRusPassports;
    private static volatile Set<String> rusPassports;


    @Test
    void passport() {
        rusPassports = new HashSet<>(repositoryMsSql.findRussPassports());
        List<String> allPassports = repositoryMsSql.findAllPassports();

        /**     allPassports
         .stream()
         .limit(300)
         .forEach(Passports::passportRus);
         */

        /**  allPassports
         .stream()
         .limit(300)
         .forEach(Passports::length10);
         */

        /** allPassports
         .stream()
         .forEach(Passports::rusPassportsContainsPasspor);
         */
        allPassports
                .stream()
                .forEach(Passports::others);

//        System.err.println("русских: " + countsOfRussianPassports);
//        System.err.println("не русских: " + countsOfNotRussPassport);
//        System.err.println("всего: " + allPassports.size());
//        Set<String> notValidPassports = new HashSet<>();
//        System.out.println(notValidPassports);
    }

    private static void others(@Nullable String passport) {
        if (passport != null) {
            String seria;
            String passportNumber;
            String[] split = passport.trim().split("[\\s,;]");
            String join = Joiner.on("").join(split);

            if (passport.contains("passportRus")) {
                return;
            } else if (join.length() == 10 && join.matches("[0-9]+")) {
            }
            if (rusPassports.contains(passport)) {
                if (!passport.contains("passportRus")
                        || !(join.length() == 10 && join.matches("[0-9]+"))
                        )
                    if (passport.trim().length() == 11) {
                        String[] split2 = passport.split("-");
                        if (split2.length > 1) {
                        } else {
                            String[] splitSpace = passport.trim().split("\\s+");
                            if (splitSpace.length > 1) {
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
                        } else {
                            System.out.println("ELSE: " + passport);
                        }

                    }
            }
        }
    }


    private static void rusPassportsContainsPasspor(@Nullable String passport) {
        if (passport != null) {
            String seria;
            String passportNumber;
            String[] split = passport.trim().split("[\\s,;]");
            String join = Joiner.on("").join(split);
            if (!passport.contains("passportRus")
                    || !(join.length() == 10 && join.matches("[0-9]+"))
                    ) {
                if (rusPassports.contains(passport)) {

                    if (passport.trim().length() == 11) {
                        String[] split2 = passport.split("-");
                        if (split2.length > 1) {
                            seria = split2[0];
                            passportNumber = split2[1];

                            System.err.println("passport: " + passport);
                            System.err.println("seria: " + seria);
                            System.err.println("passportNumber: " + passportNumber);
                            System.err.println("------------------------------");
                        }
                    }
                }
            }
        }
    }


    private static void passportRus(@Nullable String passport) {
        if (passport != null) {
            String seria;
            String passportNumber;
            String[] split = passport.trim().split("[\\s,;]");
            String join = Joiner.on("").join(split);

            if (passport.contains("passportRus")) {
                String[] rusPassport = passport.trim().split(";");
                seria = rusPassport[1];
                passportNumber = rusPassport[0];

                System.err.println("seria: " + seria);
                System.err.println("passportNumber: " + passportNumber);
            }
        }
    }

    private static void length10(@Nullable String passport) {
        if (passport != null) {
            String seria;
            String passportNumber;
            String[] split = passport.trim().split("[\\s,;]");
            String join = Joiner.on("").join(split);

            if (join.length() == 10 && join.matches("[0-9]+")) {
                seria = join.substring(0, 4);
                passportNumber = join.substring(4, join.length());

                System.err.println("join.length() == 10 && join.matches(\"[0-9]+\")");
                System.err.println("passport: " + passport);
                System.err.println("seria: " + seria);
                System.err.println("passportNumber: " + passportNumber);

                passport = seria + passportNumber;
                System.err.println("passport: " + passport);
                System.err.println(" *************************** ");
            }
        }
    }


    private static void convertPassport(@Nullable String passport) {
        if (passport != null) {
//            userToSave.setDocumentTypeId(1);
            String seria;
            String passportNumber;
            String[] split = passport.trim().split("[\\s,;]");
            String join = Joiner.on("").join(split);

            if (passport.contains("passportRus")) {
                String[] rusPassport = passport.trim().split(";");
                seria = rusPassport[1];
                passportNumber = rusPassport[0];

            } else if (join.length() == 10 && join.matches("[0-9]+")) {
                seria = join.substring(0, 4);
                passportNumber = join.substring(4, join.length());
                System.out.println("============================ join.length() == 10 && join.matches(\"[0-9]+\")");
                System.out.println(passport);

            } else if (rusPassports.contains(passport)) {
                if (!passport.contains("passportRus")
                        || !(join.length() == 10 && join.matches("[0-9]+"))
                        )
//                    TODO - перепроверить
                    if (passport.trim().length() == 11) {
                        String[] split2 = passport.split("-");
                        if (split2.length > 1) {
                            seria = split2[0];
                            passportNumber = split2[0] + split2[1];

                            System.err.println("*** split2.length > 1 ***");
                            System.err.println("passport: " + passport);
                            System.err.println("seria: " + seria);
                            System.err.println(" split2[1]: " + split2[1]);
                            System.err.println("passportNumber: " + passportNumber);
                            System.err.println("------------------------------");

                        }
                    }
            } else
//                if (notRusPassports.contains(passport))
            {
                passportNumber = passport;
                System.out.println(">>>>>>>>>>>else:");
                System.out.println(passport);
                System.out.println(">>>>>>>>>>>");
//                userToSave.setDocumentTypeId(2);
//                Starter.countOfNotRusPassports.getAndIncrement();

            }

//            userToSave.setPassportSeries(seria);
//            userToSave.setPassportNumber(passportNumber);
        }
    }

    @Test
    void passCheck() {
        notRusPassports = new HashSet<>(repositoryMsSql.findPassportNumberNotRus());
        rusPassports = new HashSet<>(repositoryMsSql.findRussPassports());
        repositoryMsSql.findAll().stream()
                .map(Client::getPassport)
                .forEach(Passports::convertPassForCount);
//      NOT RUS:
        long count = pasportMap.entrySet().stream()
                .filter(x -> x.getValue() > 1)
                .limit(5)
                .count();
//      RUS:
        long countRus = pasportMap.entrySet().stream()
                .filter(x -> x.getValue() == 1)
                .limit(5)
                .count();
        System.err.println("Русских: " + countRus);
    }

    private static void convertPassForCount(@Nullable String passport) {
        if (passport != null) {
            String seria;
            String passportNumber;
            String[] split = passport.trim().split("[\\s,;]");
            String join = Joiner.on("").join(split);
            passportNumber = passport;
            seria = null;

            if (passport.contains("passportRus")) {
                String[] rusPassport = passport.trim().split(";");
                seria = rusPassport[0];
                passportNumber = rusPassport[1];
                pasportMap.put(passport, 1);
            } else if (join.length() == 10 && join.matches("[0-9]+")) {
                seria = join.substring(0, 4);
                passportNumber = join.substring(4, join.length());
                pasportMap.put(passport, 2);
            } else if (rusPassports.contains(passport)) {
                if (!passport.contains("passportRus") || !(join.length() == 10 && join.matches("[0-9]+")))
                    if (passport.trim().length() == 11) {
                        String[] split2 = passport.split("-");
                        if (split2.length > 1) {
                            System.err.println(passport);
                            System.err.println(split2[0] + split2[1]);
                            pasportMap.put(passport, 1);

                        }
                    }
                pasportMap.put(passport, 1);
            } else
//                if (notRusPassports.contains(passport))
            {
                pasportMap.put(passport, 2);
                Starter.countOfNotRusPassports.getAndIncrement();
            }
//            userToSave.setPassportSeries(seria);
//            userToSave.setPassportNumber(passportNumber);
        }
    }
}
