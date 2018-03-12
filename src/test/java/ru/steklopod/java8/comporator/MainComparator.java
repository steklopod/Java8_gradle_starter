package ru.steklopod.java8.comporator;

import name.falgout.jeffrey.testing.junit5.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.function.Function;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
@RunWith(JUnitPlatform.class)
class MainComparator {

    @Test
    void compoatorTest() {
        Comparator<PersonForTest> cmpAge = (p1, p2) -> p2.getAge() - p1.getAge() ;
        Comparator<PersonForTest> cmpFirstName = (p1, p2) -> p1.getFirstName().compareTo(p2.getFirstName()) ;
        Comparator<PersonForTest> cmpLastName = (p1, p2) -> p1.getLastName().compareTo(p2.getLastName()) ;
        
        Function<PersonForTest, Integer> f1 = p -> p.getAge();
        Function<PersonForTest, String> f2 = p -> p.getLastName();
        Function<PersonForTest, String> f3 = p -> p.getFirstName();

        Comparator<PersonForTest> cmpPersonAge = Comparator.comparing(PersonForTest::getAge);
        Comparator<PersonForTest> cmpPersonLastName = Comparator.comparing(PersonForTest::getLastName);

        Comparator<PersonForTest> cmp = Comparator.comparing(PersonForTest::getLastName)
                                           .thenComparing(PersonForTest::getFirstName)
                                           .thenComparing(PersonForTest::getAge);
    }
}
