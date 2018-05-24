package ru.steklopod.other_examples;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.WordUtils;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
@RunWith(JUnitPlatform.class)
class HumanReadableDuration {

    @Test
    @SneakyThrows
    void человекочитаемоеВремя() {
        long now = System.currentTimeMillis();
        Thread.sleep(2000);
        long duration = System.currentTimeMillis() - now;
        String s = toHumanReadableDuration(duration);
        System.err.println(s);
    }

    private static final List<TimeUnit> timeUnits = Arrays.asList(TimeUnit.DAYS, TimeUnit.HOURS, TimeUnit.MINUTES,
            TimeUnit.SECONDS);

    static String toHumanReadableDuration(final long millis) {
        final StringBuilder builder = new StringBuilder();
        long acc = millis;
        for (final TimeUnit timeUnit : timeUnits) {
            final long convert = timeUnit.convert(acc, TimeUnit.MILLISECONDS);
            if (convert > 0) {
                builder.append(convert).append(' ').append(WordUtils.capitalizeFully(timeUnit.name())).append(", ");
                acc -= TimeUnit.MILLISECONDS.convert(convert, timeUnit);
            }
        }
        return builder.substring(0, builder.length() - 2);
    }
}
