package ru.steklopod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableJpaRepositories
@EnableWebMvc
public class GradleStarterApplication {

	public static void main(String[] args) {
		SpringApplication.run(GradleStarterApplication.class, args);
	}
}
