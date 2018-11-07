package org.superbiz.moviefun.movies;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;
import org.superbiz.moviefun.DatabaseServiceCredentials;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class DatabaseMovieConfig {


    @Bean
    public DataSource moviesDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource moviesDataSource = new MysqlDataSource();
        moviesDataSource.setURL(serviceCredentials.jdbcUrl("movies-mysql"));
        return moviesDataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryMovieBean(DataSource moviesDataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter){
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryMovieBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryMovieBean.setDataSource(moviesDataSource);
        localContainerEntityManagerFactoryMovieBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        localContainerEntityManagerFactoryMovieBean.setPackagesToScan("org.superbiz.moviefun.movies");
        localContainerEntityManagerFactoryMovieBean.setPersistenceUnitName("movies");

        return localContainerEntityManagerFactoryMovieBean;
    }

    @Bean(name="moviesPlatformTransactionManager")
    public PlatformTransactionManager moviesPlatformTransactionManager(EntityManagerFactory localContainerEntityManagerFactoryMovieBean) {

        PlatformTransactionManager moviesPlatformTransactionManager = new JpaTransactionManager();
        ((JpaTransactionManager) moviesPlatformTransactionManager).setEntityManagerFactory(localContainerEntityManagerFactoryMovieBean);

        return moviesPlatformTransactionManager;

    }

    @Bean(name="moviesTransactionOperations")
    public TransactionOperations moviesTransactionOperations(@Qualifier("moviesPlatformTransactionManager") PlatformTransactionManager moviesPlatformTransactionManager) {

        TransactionOperations movieTransactionOperations = new TransactionTemplate(moviesPlatformTransactionManager);
        ((TransactionTemplate) movieTransactionOperations).setTransactionManager(moviesPlatformTransactionManager);

        return movieTransactionOperations;

    }


}
