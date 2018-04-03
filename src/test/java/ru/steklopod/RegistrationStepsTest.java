package ru.steklopod;

import org.junit.jupiter.api.Disabled;
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
import ru.steklopod.entities.mssql.ClientVerificationStep;
import ru.steklopod.repositories.ms.VerificationStepDAO;
import ru.steklopod.service.RegistrtionStepsService;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@Transactional
@DisplayName("Тестируем стадии верификации")
@Disabled
class RegistrationStepsTest {
    private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private RegistrtionStepsService service;
    @Autowired
    private VerificationStepDAO verificationStepDAO;

    private static Stream<Integer> makeIDs() {
        return Stream.of(11595571, 11701132, 55308090, 22225320, 11446392, 11486046, 11523437,
                55238717, 11571919, 11591672, 11812258, 11563150);
    }

    @ParameterizedTest(name = "Тест #{index} для [{arguments}]")
    @MethodSource("makeIDs")
    @DisplayName("Сохранение VerificationStep [Integer]")
    void save(int id) {
        List<ClientVerificationStep> max = verificationStepDAO.findAllByClientId(id);
        max.forEach(x -> service.convertAndSave(x));
    }


}
