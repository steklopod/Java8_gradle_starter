package ru.steklopod.functional_examples.interfaces;

import io.github.glytching.junit.extension.random.RandomBeansExtension;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static ru.steklopod.functional_examples.interfaces.Validation.UserValidation.eMailContainsAtSign;
import static ru.steklopod.functional_examples.interfaces.Validation.UserValidation.nameIsNotEmpty;
import static ru.steklopod.functional_examples.interfaces.Validation.ValidationResult.invalid;
import static ru.steklopod.functional_examples.interfaces.Validation.ValidationResult.valid;

@Slf4j
@SpringBootTest
@RunWith(JUnitPlatform.class)
@ExtendWith({SpringExtension.class, RandomBeansExtension.class})
class Validation {
// Сложный для поеимания пример

    @Data
    @AllArgsConstructor
    class User {
        public final String name;
        public final int age;
        public final String email;

        boolean isValid() {
            return nameIsNotEmpty() && eMailContainsAtSign();
        }
        private boolean nameIsNotEmpty() {
            return !name.trim().isEmpty();
        }
        private boolean eMailContainsAtSign() {
            return email.contains("@");
        }
    }

    private static final class ValidationSupport {
        private static final ValidationResult valid = new ValidationResult() {
            public boolean isValid() {
                return true;
            }
            public Optional<String> getReason() {
                return Optional.empty();
            }
        };
        static ValidationResult valid() {
            return valid;
        }
    }

    @AllArgsConstructor
    private static final class Invalid implements ValidationResult {
        private final String reason;

        public boolean isValid() {
            return false;
        }

        public Optional<String> getReason() {
            return Optional.of(reason);
        }
    }
    /*************
     * ИНТЕРФЕЙСЫ
     ************/
    interface ValidationResult {
        static ValidationResult valid() {
            return ValidationSupport.valid();
        }
        static ValidationResult invalid(String reason) {
            return new Invalid(reason);
        }

        boolean isValid();

        Optional<String> getReason();
    }

    interface UserValidation extends Function<User, ValidationResult> {
        static UserValidation nameIsNotEmpty() {
            return holds(user -> !user.name.trim().isEmpty(), "Name is empty.");
        }

        static UserValidation eMailContainsAtSign() {
            return holds(user -> user.email.contains("@"), "Missing @-sign.");
        }

        static UserValidation holds(Predicate<User> p, String message) {
            return user -> p.test(user) ? valid() : invalid(message);
        }

        default UserValidation and(UserValidation other) {
            return user -> {
                final ValidationResult result = this.apply(user);
                return result.isValid() ? other.apply(user) : result;
            };
        }
    }


    @Test
    void проверка_Функц_Интерфейса() {
        UserValidation validation = nameIsNotEmpty().and(eMailContainsAtSign());

        User gregor = new User("Gregor", 30, "nicemail@gmail.com");

        ValidationResult result = validation.apply(gregor);
        result.getReason().ifPresent(System.out::println); // Name is empty.
    }



    /** (2)
     *  Валидация имени и email

     @Test
    void проверка_Функц_1() {
        User gregor = new User("Gregor", 30, "nicemail@gmail.com");

        UserValidation nameIsNotEmpty = user -> !user.name.trim().isEmpty();
        UserValidation eMailContainsAtSign = user -> user.email.contains("@");
        boolean b = nameIsNotEmpty.apply(gregor) && eMailContainsAtSign.apply(gregor);// true
        assertTrue(b);
    }

     public interface UserValidation extends Function<User, Boolean> {
     static UserValidation nameIsNotEmpty() {
     return user -> !user.name.trim().isEmpty();
     }
     static UserValidation eMailContainsAtSign() {
     return user -> user.email.contains("@");
     }
     default UserValidation and(UserValidation other) {
     return user -> this.apply(user) && other.apply(user);
     }
     }

     */

}
