package ru.stoloto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class GradleStarterApplication {

	public static void main(String[] args) {
		SpringApplication.run(GradleStarterApplication.class, args);
	}
}
