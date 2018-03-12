package ru.stoloto.java8.future;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.SneakyThrows;
import name.falgout.jeffrey.testing.junit5.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
@RunWith(JUnitPlatform.class)
class FutureTest {
    private static Logger logger = LoggerFactory.getLogger(FutureTest.class);

    @Test
    @SneakyThrows
    void executorService(){
//      Задаем имя потока(необязательно):
        final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("Поток № %d")
                .setDaemon(true)
                .build();

        Callable task = () -> {
//          Получаем/меняем имя потока(необязательно):
            final Thread currentThread = Thread.currentThread();
            final String oldName = currentThread.getName();
            currentThread.setName("Обработка-" + " новое имя");

            try {
                TimeUnit.SECONDS.sleep(2);
                return 123;
            } catch (InterruptedException e) {
                throw new IllegalStateException("task interrupted", e);
            }
        };
        ExecutorService executor = Executors.newFixedThreadPool(1, threadFactory);
        Future<Integer> future = executor.submit(task);

        System.out.println("future done? " + future.isDone());
        Integer result = future.get(3, TimeUnit.SECONDS);

        System.out.println("future done? " + future.isDone());
        System.out.print("result: " + result);
    }

    @Test
    @SneakyThrows
    void testInokeAll() {
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        Callable<String> task1 = () -> {
            Thread.sleep(2000);
            return "Result of Task1";
        };

        Callable<String> task2 = () -> {
            Thread.sleep(1000);
            return "Result of Task2";
        };

        Callable<String> task3 = () -> {
            Thread.sleep(5000);
            return "Result of Task3";
        };

        List<Callable<String>> taskList = Arrays.asList(task1, task2, task3);
//        new CopyOnWriteArrayList(taskList);
        List<Future<String>> futures = executorService.invokeAll(taskList);

        for (Future<String> future : futures) {
            // The result is printed only after all the futures are complete. (i.e. after 5 seconds)
            System.out.println(future.get());
        }
        executorService.shutdown();
    }

}
