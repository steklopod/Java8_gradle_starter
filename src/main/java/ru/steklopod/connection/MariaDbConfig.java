package ru.steklopod.connection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = "ru.steklopod.repositories")
@EnableTransactionManagement
public class MariaDbConfig {

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    @ConfigurationProperties(prefix = "postgres.datasource")
    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
            lef.setDataSource(dataSource());
            lef.setJpaVendorAdapter(jpaVendorAdapter());
            lef.setPackagesToScan("ru.steklopod.entities");
        Properties properties = new Properties();
            properties.setProperty("hibernate.connection.shutdown", "true");
    //        properties.setProperty("hibernate.classloading.use_current_tccl_as_parent", "false");
            properties.setProperty("hibernate.proc.param_null_passing", "true");
            properties.setProperty("hibernate.hbm2ddl.auto", ddlAuto);
        lef.setJpaProperties(properties);
        lef.afterPropertiesSet();
        return lef.getObject();
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(false);
        return hibernateJpaVendorAdapter;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(entityManagerFactory());
        return tm;
    }

    @Bean
    @Autowired
    public JdbcTemplate createJdbcTemplate_maria(@Qualifier("dataSource") DataSource profileServiceDS) {
        return new JdbcTemplate(profileServiceDS);
    }
}

