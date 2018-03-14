package ru.stoloto.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "aentityManagerFactory",
        transactionManagerRef = "atransactionManager",
        basePackages = "ru.stoloto.repositories.ms")
@EnableTransactionManagement
public class MsSQLDataSource {

    @Value("${another.datasource.url}")
    private String url;

    @Value("${another.datasource.username}")
    private String username;

    @Value("${another.datasource.password}")
    private String password;

    @Value("${another.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${another.datasource.hibernate.dialect}")
    private String dialect;

    @Bean
//            ("MS_datasource")
    public DataSource adataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

//        hikariConfig.setMaximumPoolSize(poolsize);
//        hikariConfig.setMinimumIdle(2);
//        hikariConfig.setMaxLifetime(lifetime);
//        hikariConfig.setConnectionTimeout(connectionTimeOut);
//        hikariConfig.setIdleTimeout(idleTimeOut);
//        hikariConfig.setConnectionTestQuery("SELECT 1");
//        hikariConfig.setLeakDetectionThreshold(15000);
//        hikariConfig.setPoolName("Hikari-2");
//
//        hikariConfig.setIsolateInternalQueries(true);
//        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSize", 250);
//        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", 2048);
//        hikariConfig.addDataSourceProperty("dataSource.cachePrepStmts", true);
//        hikariConfig.addDataSourceProperty("dataSource.useServerPrepStmts", true);
        HikariDataSource ds = new HikariDataSource(hikariConfig);
        return ds;
    }

    @Bean
//            (name = "MS_jpaVendorAdapter")
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(false);
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
        return hibernateJpaVendorAdapter;
    }

    @Bean
//            (name = "MS_entityManagerFactory")
    public EntityManagerFactory aentityManagerFactory() {
        LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
        lef.setDataSource(adataSource());
        lef.setJpaVendorAdapter(jpaVendorAdapter());

//        TODO - изменить при переименовании
        lef.setPackagesToScan("ru.stoloto.entities.mssql");

        Properties properties = new Properties();
//        properties.setProperty("hibernate.show_sql", "true");
//        properties.setProperty("hibernate.format_sql", "true");
//        properties.setProperty("hibernate.dialect", dialect);
//        properties.setProperty("hibernate.connection.shutdown", "true");
//        properties.setProperty("hibernate.classloading.use_current_tccl_as_parent", "false");
//        properties.setProperty("hibernate.proc.param_null_passing", "true");
//        properties.setProperty("hibernate.temp.use_jdbc_metadata_defaults", "false");

//        TODO - изменить на validate в продакшн
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        lef.setJpaProperties(properties);
        lef.afterPropertiesSet();
        return lef.getObject();
    }

    @Bean
//            (name = "MS_transactionManager")
    public PlatformTransactionManager atransactionManager() {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(aentityManagerFactory());
        return tm;
    }



}
