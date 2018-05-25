package ru.steklopod.functional_examples;

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

import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@Slf4j
@SpringBootTest
@RunWith(JUnitPlatform.class)
@ExtendWith({SpringExtension.class, RandomBeansExtension.class})
class AndThen {

    @Test
        //Function<T,R> - переход от объекта типа T к объекту типа R:
    void functionAndThen() {
        Function<Integer, Integer> f1 = i -> i * 4;
        Function<Integer, Integer> f2 = i -> i + 4;

        f2.andThen(f1).apply(3); // 28

        //f2.andThen(f1).apply(3) - то же самое:
        Integer j1 = f2.apply(3);
        Integer j2 = f1.apply(j1); //28
    }

    @Test
    void самый_простой() {
        Function<Integer, String> converter = (i) -> Integer.toString(i);
        System.out.println(
                converter.apply(3).length()
        );

        Function<String, Integer> reverse = (s) -> Integer.parseInt(s);
        System.out.println(
                converter.andThen(reverse).apply(30).byteValue()
        );
    }

    @Test
    void самый_простой_2_BiFunction() {
        BinaryOperator<Integer> operator = (a, b) -> a + b;
        Function<Integer, Integer> function = n -> n * 2;

        System.out.println(
                operator.andThen(function).apply(1, 6) //14
        );
    }

    @Test
    void andThenChain() {
        class MyClass {
            Integer addTen(Integer a) {
                return a + 10;
            }
        }
        Function<Integer, Integer> add1 = a -> a + 1;
        Function<Integer, Integer> add10 = new MyClass()::addTen;

        add1.apply(10);//11
        add10.apply(10);//20

        //function composition
        Function<Integer, Integer> add5 = add1
                .andThen(add1)
                .andThen(add1)
                .andThen(add1)
                .andThen(add1);
    }



    @Test
    void consumerChain() {
        List<Student> students = Arrays.asList(
                new Student(1, 3, "John"),
                new Student(2, 4, "Jane"),
                new Student(3, 3, "Jack"));

        java.util.function.Consumer<Student> raiser = e -> e.gpa = e.gpa * 1.1;

        raiseStudents(students, raiser.andThen(System.out::println));

        UnaryOperator<Student> update = student -> new Student(student.id += 13, student.gpa *= 2, "EMPTY");
        students.forEach(update::apply);
        System.err.println(students);
    }

    @Data
    @AllArgsConstructor
    class Student {
        public int id;
        public double gpa;
        public String name;
    }

    private static void raiseStudents(List<Student> employees, java.util.function.Consumer<Student> fx) {
// 1 вариант:
//        for (Student e : employees) { fx.accept(e); }
// 2 вариант:
//        employees.forEach(x -> fx.accept(x));
// 3 вариант:
        employees.forEach(fx::accept);
// 4 вариант:
//        employees.forEach(fx);
    }


}
