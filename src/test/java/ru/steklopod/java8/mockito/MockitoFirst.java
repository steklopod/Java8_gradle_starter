package ru.steklopod.java8.mockito;

import name.falgout.jeffrey.testing.junit5.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
@RunWith(JUnitPlatform.class)
class MockitoFirst {
    private static Logger logger = LoggerFactory.getLogger(MockitoFirst.class);

    @Test
    void fromOficialSite() {
        List mockedList = mock(List.class);
        mockedList.add("one");
        mockedList.clear();

        verify(mockedList).add("one");
        verify(mockedList).clear();
    }

    @Test
    void fromOficialSite2() {
        LinkedList mockedList = mock(LinkedList.class);
        when(mockedList.get(0)).thenReturn("first");
        System.out.println(mockedList.get(0));
// the following prints "null" because get(999) was not stubbed
        System.out.println(mockedList.get(999));
    }

    @Test
    void fromOficialSite3() {

    }

    @Test
    void testVerify() {
        MyMockClassForTest test = Mockito.mock(MyMockClassForTest.class);
        when(test.getUniqueId()).thenReturn(43);

        test.testing(12);
        test.getUniqueId();
        test.getUniqueId();
        test.getUniqueId();
        test.getUniqueId();

//      Переданные параметры:
        verify(test).testing(ArgumentMatchers.eq(12));
//      Кол-во вызовов метода:
        verify(test, never()).someMethod("never called");
        verify(test, atLeastOnce()).getUniqueId();
        verify(test, atLeast(2)).getUniqueId();
        verify(test, times(4)).getUniqueId();
        verify(test, atMost(666)).getUniqueId();
        verifyNoMoreInteractions(test);
    }

    @Test
    void with_arguments() {
        Comparable c = mock(Comparable.class);
        when(c.compareTo("Test")).thenReturn(1);
        assertEquals(1, c.compareTo("Test"));
    }

    @Test
    void with_unspecified_arguments() {
        Comparable c = mock(Comparable.class);
        when(c.compareTo(anyInt())).thenReturn(-1);
        assertEquals(-1, c.compareTo(5));
    }
}
