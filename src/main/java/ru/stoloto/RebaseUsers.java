package ru.stoloto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
//		(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
//		DataSourceTransactionManagerAutoConfiguration.class })
@EnableJpaRepositories(basePackages = "ru.stoloto.repositories.mybatis")
@EnableTransactionManagement
public class RebaseUsers {
	public static void main(String[] args) {
		SpringApplication.run(RebaseUsers.class, args);
	}
}
