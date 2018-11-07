package org.superbiz.moviefun.albums;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;
import org.superbiz.moviefun.DatabaseServiceCredentials;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class DatabaseAlbumConfig {


    @Bean
    public DataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource albumsDataSource = new MysqlDataSource();
        albumsDataSource.setURL(serviceCredentials.jdbcUrl("albums-mysql"));
        return albumsDataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryAlbumBean(DataSource albumsDataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter){
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryAlbumBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryAlbumBean.setDataSource(albumsDataSource);
        localContainerEntityManagerFactoryAlbumBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        localContainerEntityManagerFactoryAlbumBean.setPackagesToScan("org.superbiz.moviefun.albums");
        localContainerEntityManagerFactoryAlbumBean.setPersistenceUnitName("albums");

        return localContainerEntityManagerFactoryAlbumBean;
    }

    @Bean(name="albumsPlatformTransactionManager")
    public PlatformTransactionManager albumsPlatformTransactionManager(EntityManagerFactory localContainerEntityManagerFactoryAlbumBean) {

        PlatformTransactionManager albumsPlatformTransactionManager = new JpaTransactionManager();
        ((JpaTransactionManager) albumsPlatformTransactionManager).setEntityManagerFactory(localContainerEntityManagerFactoryAlbumBean);

        return albumsPlatformTransactionManager;

    }

    @Bean(name="albumsTransactionOperations")
    public TransactionOperations albumsTransactionOperations( @Qualifier("albumsPlatformTransactionManager") PlatformTransactionManager albumsPlatformTransactionManager) {

        TransactionOperations albumsTransactionOperations = new TransactionTemplate(albumsPlatformTransactionManager);
        ((TransactionTemplate) albumsTransactionOperations).setTransactionManager(albumsPlatformTransactionManager);

        return albumsTransactionOperations;

    }


}
