package ar.edu.itba.paw.persistence.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@ComponentScan("ar.edu.itba.paw.persistence")
@Configuration
public class TestConfig {

    @Value("classpath:hsqldb.sql")
    private Resource hsqldbSql;

    @Value("classpath:/populators/games.sql")
    private Resource gamesSql;

    @Value("classpath:/populators/users.sql")
    private Resource usersSql;

    @Value("classpath:/populators/favoritegames.sql")
    private Resource favoriteGamesSql;

    @Value("classpath:/populators/tokens.sql")
    private Resource tokensSql;

    @Value("classpath:/populators/reviews.sql")
    private Resource reviewsSql;

    @Value("classpath:/populators/following.sql")
    private Resource followingSql;

    @Value("classpath:/populators/missions.sql")
    private Resource missionSql;

    @Value("classpath:/populators/reviewfeedback.sql")
    private Resource reviewFeedbackSql;

    @Value("classpath:/populators/reports.sql")
    private Resource reportsSql;

    @Bean
    public DataSource dataSource() {
        final SimpleDriverDataSource ds = new SimpleDriverDataSource();

        ds.setDriverClass(org.hsqldb.jdbc.JDBCDriver.class);
        ds.setUrl("jdbc:hsqldb:mem:paw;sql.syntax_pgs=true");
        ds.setUsername("ha");
        ds.setPassword("");

        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean factoryBean = new
                LocalContainerEntityManagerFactoryBean();
        factoryBean.setPackagesToScan("ar.edu.itba.paw.models");
        factoryBean.setDataSource(dataSource());
        final JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        final Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.dialect",
                "org.hibernate.dialect.HSQLDialect");
//        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("format_sql", "true");
        factoryBean.setJpaProperties(properties);
        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(final DataSource dataSource) {
        final DataSourceInitializer dsi = new DataSourceInitializer();
        dsi.setDataSource(dataSource);
        dsi.setDatabasePopulator(databasePopulator());
        return dsi;
    }

    private DatabasePopulator databasePopulator() {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(hsqldbSql);
        // tokens.sql
        populator.addScripts(gamesSql, usersSql, favoriteGamesSql, missionSql);
        populator.addScript(reviewsSql);
        populator.addScript(reviewFeedbackSql);
        populator.addScript(reportsSql);
        populator.addScript(followingSql);
        populator.addScript(tokensSql);
        return populator;
    }
}
