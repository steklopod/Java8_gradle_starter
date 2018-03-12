package ru.steklopod;

import name.falgout.jeffrey.testing.junit5.MockitoExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
@RunWith(JUnitPlatform.class)
@IncludeTags("production")
@ExcludeTags("ex")

//@ContextConfiguration(classes = {DataProvider.class})
class InitJunit5 {
    private static Logger logger = LoggerFactory.getLogger(InitJunit5.class);

    @Value("${spring.datasource.url}")
    private String dataSourceURL;

    @BeforeAll
    static void initializeExternalResources() {
        System.out.println("Перед всеми...");
    }

    @BeforeEach
    void initializeMockObjects() {
        System.out.println("Перед каждым...");
    }

    @Test
    @DisplayName("Проверка application.properties")
    @Tag("production")
    void checkAppPropertiesFile() {
        assertNotNull(dataSourceURL);
        System.out.println(" >>> OK <<< Файл application.properties доступен.");
        System.err.println("Адрес БД: " + dataSourceURL);
    }

    @DisplayName("😱 Повторяемый тест")
    @RepeatedTest(4)
    @Tag("ex")
    void repeatable() {
        assumeTrue(true);
        System.err.println("повторяемый");
        assertNotEquals(1, 2, "Why wouldn't these be the same?");
    }

    @Test
    @DisplayName("Ожидаемое исключение")
    @Tag("production")
    void orElseThrow() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String nullName = null;
            String name = Optional.ofNullable(nullName).orElseThrow(
                    IllegalArgumentException::new);
        });
    }

    @Test
    @DisplayName("Прерываемый тест")
    void testOnDev() {
        System.setProperty("ENV", "DEV");
        Assumptions.assumeFalse("DEV".equals(System.getProperty("ENV")), InitJunit5::message);
        //remainder of test will be aborted
    }


    @Test
    @DisplayName("Непрерываемый тест")
    void testOnProd() {
        System.setProperty("ENV", "PROD");
        Assumptions.assumeFalse("DEV".equals(System.getProperty("ENV")));
        //remainder of test will proceed
    }
    @Test
    @Disabled
    @DisplayName("Исключенный тест")
    void disabledTest() {
        System.exit(1);
    }
    private static String message() {
        return "TEST Execution Failed :: ";
    }


    @AfterEach
    void afterEach() {
        System.out.println("После каждого метода...");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("И, наконец, после всех..");
    }

}
