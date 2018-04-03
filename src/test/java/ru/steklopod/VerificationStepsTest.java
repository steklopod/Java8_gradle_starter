package ru.steklopod;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.steklopod.entities.mssql.ClientVerificationStep;
import ru.steklopod.repositories.ms.VerificationStepDAO;

import java.util.List;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@Transactional
@DisplayName("Тестируем стадии верификации")
//@Disabled
class VerificationStepsTest {

    @Autowired
    private VerificationStepDAO verificationStepDAO;

    private static Stream<Integer> makeIDs() {
        return Stream.of(
                12026821,
                12026838
                , 11595571, 11701132, 55308090, 22225320, 11446392, 11486046, 11523437,
                55238717, 11571919, 11591672, 11812258, 11563150
        );
    }

    @ParameterizedTest(name = "Тест #{index} для [{arguments}]")
    @MethodSource("makeIDs")
    @DisplayName("Максимальный VerificationStep [Integer]")
    void getMaxInteger(int id) {
        List<Integer> registrationStage = verificationStepDAO.getRegistrationStages(id);
        System.out.println(registrationStage);
        Integer maxRegistrationStages = verificationStepDAO.getMaxRegistrationStages(id);
        System.out.println("maxStep: " + maxRegistrationStages);
    }

    @DisplayName("Максимальный VerificationStep [Object]")
    @ParameterizedTest(name = "Тест #{index} для [{arguments}]")
    @ValueSource(ints = {12026821, 12026838, 22225320})
    void getMaxObj(int id) {

        List<ClientVerificationStep> allByClientId = verificationStepDAO.findAllByClientId(id);
        System.err.println("size: " + allByClientId.size());
        allByClientId.forEach(x -> {
            System.err.println("STEP: "+x.getStep());
            System.err.println("STATE" + x.getState());
        });

        ClientVerificationStep max = verificationStepDAO.getMaxVerificationStepObject(id);
        System.out.println("STEP: "+max.getStep());
        System.out.println("STATE" + max.getState());

        if (max == null) {
            throw new RuntimeException("Чет-то не то :-(");
        }
    }

    @ParameterizedTest(name = "Тест #{index} для [{arguments}]")
    @MethodSource("makeIDs")
    void getObject(int id) {
        List<ClientVerificationStep> max = verificationStepDAO.findAllByClientId(id);
        System.out.println("Колв-во записей: " + max.size());
        System.err.println(max);
    }

    @Test
    void getObject() {
        Long count = verificationStepDAO.selectCount();
        System.out.println("Колв-во записей: " + count);
    }


}
