package ru.steklopod.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import ru.steklopod.converter.config.AppConfig;

public class ConvServDemo {
    private static Logger logger = LoggerFactory.getLogger(ConvServDemo.class);

    public static void main(String... args) {
        GenericApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);

        Singer john = ctx.getBean("john", Singer.class);
        logger.info("Singer info: " + john);

        ctx.close();
    }
}
