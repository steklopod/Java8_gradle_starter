package ru.steklopod.service;

import org.springframework.stereotype.Service;
import ru.steklopod.entities.mariadb.UserRebased;
import ru.steklopod.entities.mariadb.UserWithException;
import ru.steklopod.entities.mssql.Client;

import java.math.BigInteger;
import java.util.Optional;

import static ru.steklopod.Starter.*;

@Service
public class Checker {
    static boolean isTrash(Client client) {
        boolean isTrash = false;
        if (
                notNullCashDeskId(client)
                        || isEmptyEmail(client)
                        || isEmptyPhone(client)
                        || isContainsId(client)
                        || isTest(client)
                        || isPhoneExist(client)
                        || isEmailExist(client)
                        || isLoginExist(client)
                ) {
            isTrash = true;
        }
        return isTrash;
    }


    /**
     * Поиск в уже существующих исключениях:
     */
    static Optional<UserWithException> getUserWithException(Client client) {
        return Optional.ofNullable(userWithExceptions.get(client.getId()));
    }


    /**
     * ПРОВЕРКА НА ПОВТОР ID
     */
    private static boolean isContainsId(Client client) {
        boolean isContains = false;
        Integer id = client.getId();
        boolean contains =
                getIdsSet()
                        .contains
                                (BigInteger.valueOf(client.getId().intValue()));
        if (contains) {
            Optional<UserWithException> userWithException = getUserWithException(client);
            userWithException.ifPresent(u -> {
                u.setIsSameId(true);
            });
            boolean present = userWithException.isPresent();
            if (!present) {
                userWithExceptions.put(id,
                        UserWithException.builder()
                                .id(id)
                                .isSameId(true)
                                .build()
                );
            }
            isContains = true;
        }
        return isContains;
    }

    /**
     * ПРОВЕРКА НА ПОВТОР EMAIL:
     */
    private static boolean isEmailExist(Client client) {
        boolean isContains = false;
        Optional<String> email = Optional.ofNullable(client.getEmail());
        if (email.isPresent()) {
            Integer id = client.getId();
            if (getgeneralEmails().contains(email.get())) {
                Optional<UserWithException> userWithException = getUserWithException(client);
                userWithException.ifPresent(u -> {
                    u.setIsEmailExist(true);
                });
                boolean present = userWithException.isPresent();
                if (!present) {
                    userWithExceptions.put(id,
                            UserWithException.builder()
                                    .id(id)
                                    .isEmailExist(true)
                                    .build()
                    );
                }
                isContains = true;
            }
        }
        return isContains;
    }

    private static boolean isEmptyEmail(Client client) {
        boolean isContains = false;
        Optional<String> email = Optional.ofNullable(client.getEmail());
        if (!email.isPresent() || email.get().equals("")) {
            Integer id = client.getId();
            Optional<UserWithException> userWithException = getUserWithException(client);
            userWithException.ifPresent(u -> u.setIsEmptyEmail(true));
            if (!userWithException.isPresent()) {
                userWithExceptions.put(id,
                        UserWithException.builder()
                                .id(id)
                                .isEmptyEmail(true)
                                .build());
            }
            isContains = true;
        }
        return isContains;
    }

    private static boolean isEmptyPhone(Client client) {
        boolean isContains = false;
        Optional<String> phone = Optional.ofNullable(client.getPhone());
        if (!phone.isPresent() || phone.get().equals("")) {
            Integer id = client.getId();
            Optional<UserWithException> userWithException = getUserWithException(client);
            userWithException.ifPresent(u -> u.setIsEmptyPhone(true));
            if (!userWithException.isPresent()) {
                userWithExceptions.put(id,
                        UserWithException.builder()
                                .id(id)
                                .isEmptyPhone(true)
                                .build());
            }
            isContains = true;
        }
        return isContains;
    }

    /**
     * ПРОВЕРКА НА ПОВТОР ЛОГИНА:
     */
    private static boolean isLoginExist(Client client) {
        String login = client.getLogin();
        Integer id = client.getId();
        boolean isContains = false;

        if (getGeneralLogins().contains(login)) {
            Optional<UserWithException> userWithException = getUserWithException(client);
            userWithException.ifPresent(u -> u.setIsLoginExist(true));

            if (!userWithException.isPresent()) {
                userWithExceptions.put(id,
                        UserWithException.builder()
                                .id(id)
                                .isLoginExist(true)
                                .build()
                );
            }
            isContains = true;
        }
        return isContains;
    }

    /**
     * ПРОВЕРКА НА ПОВТОР ТЕЛЕФОНА
     */
    private static boolean isPhoneExist(Client client) {
        boolean isContains = false;
        Optional<String> phone = Optional.ofNullable(client.getPhone());
        if (phone.isPresent()) {
            Integer id = client.getId();
            if (getgeneralPhones().contains(phone.get())) {
                Optional<UserWithException> userWithException = getUserWithException(client);
                userWithException.ifPresent(u -> u.setIsPhoneExist(true));

                if (!userWithException.isPresent()) {
                    userWithExceptions.put(id,
                            UserWithException.builder()
                                    .id(id)
                                    .isPhoneExist(true)
                                    .build()
                    );
                    isContains = true;
                }
            }
        }
        return isContains;
    }

    /**
     * ПРОВЕРКА - ТЕСТОВЫЙ ЛИ ЮЗЕР:
     */
    private static boolean isTest(Client client) {
        boolean isContains = false;
        Integer id = client.getId();
        if (client.isTest()) {
            Optional<UserWithException> userWithException = getUserWithException(client);
            userWithException.ifPresent(u -> u.setIsTest(true));

            if (!userWithException.isPresent()) {
                userWithExceptions.put(id,
                        UserWithException.builder()
                                .id(id)
                                .isTest(true)
                                .build()
                );
            }
            isContains = true;
        }
        return isContains;
    }

    /**
     * ПРОВЕРКА НА НЕНУЛЕВОЙ CashDeskId:
     */
    private static boolean notNullCashDeskId(Client client) {
        boolean isContains = false;
        Integer id = client.getId();
        if (client.getCashDeskId() != null) {
            Optional<UserWithException> userWithException = getUserWithException(client);
            userWithException.ifPresent(u -> u.setNotNullCashDeskId(true));
            if (!userWithException.isPresent()) {
                userWithExceptions.put(id,
                        UserWithException.builder()
                                .id(id)
                                .notNullCashDeskId(true)
                                .build()
                );
            }
            isContains = true;
        }
        return isContains;
    }

    /**
     * НЕ ПОДТВЕРЖДЕН EMAIL:
     */
    static boolean isNotInBetTable(UserRebased userRebased) {
        boolean isContains = false;
        Integer id = (Integer) userRebased.getCustomerId().intValue();
        if (!userRebased.isEmailConfirmed() && !idsFromBET.contains(userRebased.getCustomerId())) {
            Optional<UserWithException> userWithException = Optional.ofNullable(userWithExceptions.get(id));
            userWithException.ifPresent(u -> {
                u.setIsCustomerIdInBetTable(true);
            });
            if (!userWithException.isPresent()) {
                userWithExceptions.put(id,
                        UserWithException.builder()
                                .id(id)
                                .isCustomerIdInBetTable(true)
                                .build()
                );
            }
            isContains = true;
        }
        return isContains;
    }

    public static boolean isInBetTable(Integer id) {
        boolean isContains = false;
        if (idsFromBET.contains(id.longValue())) {
            isContains = true;
        }
        return isContains;
    }


    public static boolean isNotConfirmedEmailWithBets(UserRebased user) {
        boolean b = false;
        boolean hasBet = false;
        Long customerId = user.getCustomerId();
        if (idsFromBET.contains(customerId)) hasBet = true;
        if (!user.isEmailConfirmed() && hasBet) b = true;
        return b;
    }
}
