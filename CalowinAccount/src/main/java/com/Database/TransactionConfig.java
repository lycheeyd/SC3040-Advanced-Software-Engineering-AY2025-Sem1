package com.Database;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.data.transaction.ChainedTransactionManager;

@Configuration
@EnableTransactionManagement
public class TransactionConfig {

    @Bean(name = "chainedTransactionManager")
    public PlatformTransactionManager chainedTransactionManager(
            @Qualifier("calowinSecureDBTransactionManager") PlatformTransactionManager calowinSecureDBTransactionManager,
            @Qualifier("calowinDBTransactionManager") PlatformTransactionManager calowinDBTransactionManager) {
        return new ChainedTransactionManager(calowinSecureDBTransactionManager, calowinDBTransactionManager);
    }
}