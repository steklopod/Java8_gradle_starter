package ru.steklopod;

import io.github.glytching.junit.extension.random.RandomBeansExtension;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import name.falgout.jeffrey.testing.junit.mockito.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.steklopod.PredicateTest.EmployeePredicates.filterEmployees;
import static ru.steklopod.PredicateTest.EmployeePredicates.*;

@Slf4j
@SpringBootTest
@RunWith(JUnitPlatform.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class, RandomBeansExtension.class})
@Transactional
class PredicateTest {

    @Data
    @AllArgsConstructor
    private class Employee {
        private Integer id;
        private Integer age;
        private String gender;
        private String firstName;
        private String lastName;
    }

    public static class EmployeePredicates {

        //All Employees who are male and age more than 21
        static Predicate<Employee> isAdultMale() {
            return p -> p.getAge() > 21 && p.getGender().equalsIgnoreCase("M");
        }

        //All Employees who are female and age more than 18
        static Predicate<Employee> isAdultFemale() {
            return p -> p.getAge() > 18 && p.getGender().equalsIgnoreCase("F");
        }

        //All Employees whose age is more than a given age
        static Predicate<Employee> isAgeMoreThan(Integer age) {
            return p -> p.getAge() > age;
        }


        static List<Employee> filterEmployees (List<Employee> employees, Predicate<Employee> predicate) {
            return employees.stream().filter( predicate ).collect(Collectors.<Employee>toList());
        }
    }

    @BeforeEach
    void init() {
        Employee e1 = new Employee(1, 23, "M", "Rick", "Beethovan");
        Employee e2 = new Employee(2, 13, "F", "Martina", "Hengis");
        Employee e3 = new Employee(3, 43, "M", "Ricky", "Martin");
        Employee e4 = new Employee(4, 26, "M", "Jon", "Lowman");
        Employee e5 = new Employee(5, 19, "F", "Cristine", "Maria");
        Employee e6 = new Employee(6, 15, "M", "David", "Feezor");
        Employee e7 = new Employee(7, 68, "F", "Melissa", "Roy");
        Employee e8 = new Employee(8, 79, "M", "Alex", "Gussin");
        Employee e9 = new Employee(9, 15, "F", "Neetu", "Singh");
        Employee e10 = new Employee(10, 45, "M", "Naveen", "Jain");

       employees = new ArrayList<>(Arrays.asList(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10));
    }

    private List<Employee> employees;

    @Test
    void predicat() {

        System.out.println(filterEmployees(employees, isAdultMale()));

        System.out.println(filterEmployees(employees, isAdultFemale()));

        System.out.println(filterEmployees(employees, isAgeMoreThan(35)));

        //Employees other than above collection of "isAgeMoreThan(35)" can be get using negate()
        System.out.println(filterEmployees(employees, isAgeMoreThan(35).negate()));
    }

}
