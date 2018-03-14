package ru.stoloto.connection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "msSQLEntityFactory",
        transactionManagerRef = "MsSqlTtransactionManager",
        basePackages = "ru.stoloto.repositories.ms")
@EnableTransactionManagement
public class MsSQLDataBaseConfig {
//    @Value("${another.datasource.hibernate.dialect}")
//    private String dialect;

    @Autowired
    JpaVendorAdapter jpaVendorAdapter;

    @Bean
    @ConfigurationProperties(prefix="another.datasource")
    public DataSource msSqlDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public EntityManagerFactory msSQLEntityFactory() {
        LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
        lef.setDataSource(msSqlDataSource());
        lef.setJpaVendorAdapter(jpaVendorAdapter);

//        TODO - изменить при переименовании
        lef.setPackagesToScan("ru.stoloto.entities.mssql");
        lef.afterPropertiesSet();
        return lef.getObject();
    }

    @Bean
    public PlatformTransactionManager MsSqlTtransactionManager() {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(msSQLEntityFactory());
        return tm;
    }

}
