package ru.steklopod.java8.comporator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonForTest {

    private String firstName;
    private String lastName;
    private int age;

}
