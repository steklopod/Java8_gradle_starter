package ru.steklopod.wait_notify;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
@RunWith(JUnitPlatform.class)
class Concurrents {

    @Test
    void waitAndNotify() {
        Message msg = new Message("обработать");
        Waiter waiter = new Waiter(msg);
        new Thread(waiter,"waiter").start();

        Waiter waiter1 = new Waiter(msg);
        new Thread(waiter1, "waiter1").start();

        Notifier notifier = new Notifier(msg);
        new Thread(notifier, "notifier").start();
        System.out.println("Стартовали все потоки");
    }



}
