package ru.steklopod.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Конфигурация соединения с БД.
 * Все необходимые настройки находятся в application.properties (папка resources).
 */

@Configuration
@ComponentScan(basePackages = "ru.steklopod")
@EnableJpaRepositories(basePackages = "ru.steklopod")
@ConfigurationProperties(value = "application.properties")
@PropertySource("classpath:application.properties")
@EnableTransactionManagement
public class DataProvider {

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${pool.size}")
    private int poolsize;

    @Value("${pool.connection.timeout}")
    private int connectionTimeOut;

    @Value("${pool.idle.timeout}")
    private int idleTimeOut;

    @Value("${pool.max.lifetime}")
    private int lifetime;


    @Bean(name = "Face")
    @Primary
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(poolsize);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setMaxLifetime(lifetime);
        hikariConfig.setConnectionTimeout(connectionTimeOut);
        hikariConfig.setIdleTimeout(idleTimeOut);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setLeakDetectionThreshold(15000);
//        hikariConfig.setPoolName("Hikari-test");
        /**
         * Это свойство определяет, будет ли HikariCP изолировать внутренние запросы пула, такие как тест живой связи,
         * в своей собственной транзакции. Поскольку они обычно являются запросами только для чтения,
         * редко бывает необходимо инкапсулировать их в свою собственную транзакцию.
         * Это свойство применяется только в том случае, если autoCommit отключен. По умолчанию: false
         */

        hikariConfig.setIsolateInternalQueries(true);
        /**
         * Это свойство определяет, можно ли приостановить и возобновить пул через JMX.
         * Это полезно для некоторых сценариев автоматизации отказоустойчивости.
         * Когда пул приостановлен, вызовы getConnection () не будут таймаутом и будут удерживаться до тех пор,
         * пока пул не будет возобновлен. По умолчанию: false
         */

//        hikariConfig.setAllowPoolSuspension(true);
        /**
         Этот параметр задает количество подготовленных операторов, которые драйвер Posrgres будет кэшировать для каждого соединения.
         Значение по умолчанию - консервативное = 25. Рекомендуется установить это значение между 250-500.
         */

        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSize", 250);
        /**
         Это максимальная длина подготовленного оператора SQL, который будет кэшировать драйвер.
         */

        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", 2048);
        /**
         Ни один из вышеперечисленных параметров не имеет никакого эффекта, если кэш фактически отключен.
         */

        hikariConfig.addDataSourceProperty("dataSource.cachePrepStmts", true);
        /**
         Более новые версии БД поддерживают подготовленные операторы на стороне сервера, это может обеспечить
         существенное повышение производительности. Установите для этого свойства значение true.
         */

        hikariConfig.addDataSourceProperty("dataSource.useServerPrepStmts", true);
        HikariDataSource ds = new HikariDataSource(hikariConfig);
        return ds;
    }

    @Bean(name = "myJpaVendorAdapter")
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(true);
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
        return hibernateJpaVendorAdapter;
    }

    @Bean (name = "myEntityManagerFactoryBean")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(/*@Qualifier("myDataSource") DataSource dataSource, JpaVendorAdapter jpaVendorAdapter*/)  {
        LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
        lef.setDataSource(dataSource());
        lef.setJpaVendorAdapter(jpaVendorAdapter());

//        TODO - перепроверить необходимость
        lef.setPackagesToScan("ru.steklopod.*");

        Properties properties = new Properties();
            properties.setProperty("hibernate.show_sql", "true");
            properties.setProperty("hibernate.format_sql", "true");
            properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
            properties.setProperty("hibernate.connection.shutdown", "true");
            properties.setProperty("hibernate.classloading.use_current_tccl_as_parent", "false");
            properties.setProperty("hibernate.proc.param_null_passing", "true");
            properties.setProperty("hibernate.temp.use_jdbc_metadata_defaults", "false");

//        TODO - перепроверить необходимость
//            properties.setProperty("hibernate.hbm2ddl.auto", "validate");
        lef.setJpaProperties(properties);
        lef.afterPropertiesSet();
        return lef;
    }

    @Bean(name = "myTransactionManager")
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager();
    }
}