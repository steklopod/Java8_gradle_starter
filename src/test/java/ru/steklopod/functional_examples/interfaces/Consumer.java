package ru.steklopod.functional_examples.interfaces;

import io.github.glytching.junit.extension.random.RandomBeansExtension;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

@Slf4j
@SpringBootTest
@RunWith(JUnitPlatform.class)
@ExtendWith({SpringExtension.class, RandomBeansExtension.class})
@DisplayName("Ничего не возвращает")
class Consumer {

    /**
     * AndThen:
     */
    @Data
    @AllArgsConstructor
    class Student {
        public int id;
        public double gpa;
        public String name;
    }

    private static void raiseStudents(List<Student> employees, java.util.function.Consumer<Student> fx) {
// 1 вариант
        for (Student e : employees) {
            fx.accept(e);
        }
// 2 вариант
//        employees.forEach(x -> fx.accept(x));
// 3 вариант
//        employees.forEach(fx::accept);
// 4 вариант
//        employees.forEach(fx);
    }

    @Test
    void consumerChain() {
        List<Student> students = Arrays.asList(
                new Student(1, 3, "John"),
                new Student(2, 4, "Jane"),
                new Student(3, 3, "Jack"));

        java.util.function.Consumer<Student> raiser = e -> e.gpa = e.gpa * 1.1;

        UnaryOperator<Student> update = student -> new Student(student.id += 13, 2, "");


        raiseStudents(students, raiser.andThen(System.out::println));

    }

}
