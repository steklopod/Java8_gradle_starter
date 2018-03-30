package ru.stoloto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"ru.stoloto.repositories.maria"})
@EnableTransactionManagement
//@EnableAsync
public class RebaseUsers {

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"));
    }

    public static void main(String[] args) {
        SpringApplication.run(RebaseUsers.class, args);
    }

//    @Bean
//    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
//        PropertySourcesPlaceholderConfigurer properties = new PropertySourcesPlaceholderConfigurer();
//        properties.setLocation(new FileSystemResource("application.yml"));
//        properties.setIgnoreResourceNotFound(false);
//        return properties;
//    }

}
