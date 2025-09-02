package com.Database.CalowinDB;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.Database.CalowinDB",  // Repository package for this database
    entityManagerFactoryRef = "calowinDBEntityManagerFactory",
    transactionManagerRef = "calowinDBTransactionManager"
)

public class CalowinDBConfig {

    @Bean(name = "calowinDBEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean calowinDBEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("calowinDBDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.Relationship.Entity") // Entity package for CalowinDB
                .persistenceUnit("calowinDB")
                .build();
    }

    @Bean(name = "calowinDBTransactionManager")
    public PlatformTransactionManager calowinDBTransactionManager(
            @Qualifier("calowinDBEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}