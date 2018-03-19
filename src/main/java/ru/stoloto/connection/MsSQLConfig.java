package ru.stoloto.connection;

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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "msSQLEntityFactory",
        transactionManagerRef = "MsSqlTtransactionManager",
        basePackages = "ru.stoloto.repositories.ms")
@EnableTransactionManagement
public class MsSQLConfig {

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    @Autowired
    JpaVendorAdapter jpaVendorAdapter;

    @Bean
    @ConfigurationProperties(prefix = "sql-server.datasource")
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

        Properties properties = new Properties();
        properties.setProperty("hibernate.format_sql", "true");
        properties.setProperty("hibernate.connection.shutdown", "true");
        properties.setProperty("hibernate.classloading.use_current_tccl_as_parent", "false");
        properties.setProperty("hibernate.proc.param_null_passing", "true");
        properties.setProperty("hibernate.temp.use_jdbc_metadata_defaults", "false");
        properties.setProperty("hibernate.hbm2ddl.auto", "none");
        lef.setJpaProperties(properties);
        lef.afterPropertiesSet();
        return lef.getObject();
    }

    @Bean
    public PlatformTransactionManager MsSqlTtransactionManager() {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(msSQLEntityFactory());
        return tm;
    }

    @Bean(name = "jdbcMsSql")
    @Autowired
    public JdbcTemplate createJdbcTemplate_ProfileService(@Qualifier("msSqlDataSource") DataSource profileServiceDS) {
        return new JdbcTemplate(msSqlDataSource());
    }
}
