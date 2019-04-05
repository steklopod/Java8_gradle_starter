package ru.steklopod.converter.config;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.convert.converter.Converter;
import ru.steklopod.converter.Singer;
import ru.steklopod.converter.StringToDateTimeConverter;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;


@PropertySource("classpath:application.properties")
@Configuration
@ComponentScan(basePackages = "ru.steklopod.converter.config")
public class AppConfig {

	@Value("${date.format.pattern}")
	private String dateFormatPattern;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public Singer john(@Value("${countrySinger.firstName}") String firstName,
					   @Value("${countrySinger.lastName}") String lastName,
					   @Value("${countrySinger.personalSite}") URL personalSite,
					   @Value("${countrySinger.birthDate}") DateTime birthDate) {
		Singer singer = new Singer();
		singer.setFirstName(firstName);
		singer.setLastName(lastName);
		singer.setPersonalSite(personalSite);
		singer.setBirthDate(birthDate);
		return singer;
	}

	@Bean
	public ConversionServiceFactoryBean conversionService() {
		ConversionServiceFactoryBean conversionServiceFactoryBean = new ConversionServiceFactoryBean();
		Set<Converter> convs = new HashSet<>();
		convs.add(converter());
		conversionServiceFactoryBean.setConverters(convs);
		conversionServiceFactoryBean.afterPropertiesSet();
		return conversionServiceFactoryBean;
	}

	@Bean
	StringToDateTimeConverter converter(){
		StringToDateTimeConverter conv = new StringToDateTimeConverter();
		conv.setDatePattern(dateFormatPattern);
		conv.init();
		return conv;
	}
}
