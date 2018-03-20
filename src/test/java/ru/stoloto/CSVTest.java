package ru.stoloto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.stoloto.repositories.maria.UserOutDAO;
import ru.stoloto.repositories.ms.ClientInDAO;
import ru.stoloto.repositories.ms.VerificationStepDAO;
import ru.stoloto.service.Converter;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@Transactional
@DisplayName("Тестируем Service-слой  \uD83D\uDC7F")
//@Disabled
class CSVTest {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    Converter converter;
    @Autowired
    UserOutDAO userOutDAO;
    @Autowired
    ClientInDAO repositoryMsSql;
    @Autowired
    VerificationStepDAO verificationStepDAO;

    private static Stream<Integer> createWordsWithLength() {
        return Stream.of(52155734, 52156137, 52160196);
    }


    @DisplayName("\uD83D\uDE80 CSV первый")
    @ParameterizedTest(name = "Тест #{index} для ID № [{arguments}]")
    @MethodSource("createWordsWithLength")
    void initTest(int id) {
//        File file = new File(GettingStartedCsv_csvParser.class.getClassLoader().getResource("samples.csv").getFile());

    }

}
