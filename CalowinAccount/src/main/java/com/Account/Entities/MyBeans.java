package com.Account.Entities;

import java.util.HashMap;

import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.web.client.RestTemplate;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;

import jakarta.transaction.SystemException;

@Configuration
public class MyBeans {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder(JpaVendorAdapter jpaVendorAdapter, JpaProperties jpaProperties) {
        return new EntityManagerFactoryBuilder(jpaVendorAdapter, new HashMap<>(jpaProperties.getProperties()), null);
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    public UserTransactionManager atomikosTransactionManager() {
        UserTransactionManager transactionManager = new UserTransactionManager();
        transactionManager.setForceShutdown(false);
        return transactionManager;
    }

    @Bean
    public UserTransactionImp userTransaction() {
        UserTransactionImp userTransaction = new UserTransactionImp();
        try {
            userTransaction.setTransactionTimeout(300);
        } catch (SystemException e) {
            e.printStackTrace();
        }
        return userTransaction;
    }

    @Bean
    public JtaTransactionManager transactionManager() {
        return new JtaTransactionManager(userTransaction(), atomikosTransactionManager());
    }
}
