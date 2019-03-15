package ru.steklopod;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ComparingExample {

    private List<Employee> employees = new ArrayList<>();

    @Data
    @AllArgsConstructor
    private class Employee {
        private Integer id;
        private String firstName;
        private String lastName;
        private Integer age;
    }

    @BeforeAll
    void init() {
        employees.add(new Employee(6, "Yash", "Chopra", 25));
        employees.add(new Employee(2, "Aman", "Sharma", 28));
        employees.add(new Employee(3, "Aakash", "Yaadav", 52));
        employees.add(new Employee(5, "David", "Kameron", 19));
        employees.add(new Employee(4, "James", "Hedge", 72));
        employees.add(new Employee(8, "Balaji", "Subbu", 88));
        employees.add(new Employee(7, "Karan", "Johar", 59));
        employees.add(new Employee(1, "Lokesh", "Gupta", 32));
        employees.add(new Employee(9, "Vishu", "Bissi", 33));
        employees.add(new Employee(10, "Lokesh", "Ramachandran", 60));

    }

    @Test
    void первый() {
        Comparator<Employee> compareById = Comparator.comparing(Employee::getId);

        Comparator<Employee> compareById_2 = Comparator.comparing(e -> e.getId());
        Comparator<Employee> compareById_3 = (Employee o1, Employee o2) -> o1.getId().compareTo(o2.getId());

        employees.sort(compareById.reversed());

        employees.forEach(System.out::println);
    }

}
