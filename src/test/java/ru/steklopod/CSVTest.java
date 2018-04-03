package ru.steklopod;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.simpleflatmapper.csv.CsvParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.steklopod.repositories.maria.UserOutDAO;
import ru.steklopod.repositories.ms.ClientInDAO;
import ru.steklopod.service.Converter;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@Transactional
@DisplayName("Тестируем CSV  \uD83D\uDC7F")
//@Disabled
class CSVTest {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private Converter converter;
    @Autowired
    private UserOutDAO userOutDAO;
    @Autowired
    private ClientInDAO repositoryMsSql;

    private static Stream<Integer> createWordsWithLength() {
        return Stream.of(
                11444190
//                , 52155734, 52156137, 52160196
        );
    }

    @Value("${csv.filename}")
    private String filename;


    @DisplayName("Парсинг CSV Цуписа")
    @ParameterizedTest(name = "Тест #{index} для [{arguments}]")
    @MethodSource("createWordsWithLength")
    @SneakyThrows
    void initTest(int id) {
        HashMap<Long, String> regSteps = new HashMap<>();

        String fs = File.separator;
        ClassLoader classLoader = getClass().getClassLoader();
        String URI = "clients.csv";
        System.err.println(URI);
        File file = new File(classLoader.getResource(URI).getFile());


        CsvParser
                .skip(1)
                .forEach(file, row -> {
                    String[] split = row[0].split(";");
                    System.out.println("ID (Integer): " + Integer.parseInt(split[0]) + ", STEP (String): " + split[2]);
                    Long customerId = Long.parseLong(split[0]);
                    String step = split[2];
                    regSteps.put(customerId, step);

                    if (step.equals("")) {
                        System.err.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    }
                });
        String s = regSteps.get(id);
        System.out.println("Найденное значение: " + s);

        System.err.println("Кол-во записей в выгрузке ЦУПИС: " + regSteps.size());
        assertTrue(regSteps.containsKey(new Long(id)));
    }

}
