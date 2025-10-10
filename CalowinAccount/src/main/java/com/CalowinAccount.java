package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.Account.Entities.EmailServiceProperties;
import com.Database.CalowinDB.CalowinDBProperties;
import com.Database.CalowinSecureDB.CalowinSecureDBProperties;

@SpringBootApplication(scanBasePackages = { "com.Account", "com.DataTransferObject", "com.Database" })
@EnableConfigurationProperties({ EmailServiceProperties.class, CalowinSecureDBProperties.class,
        CalowinDBProperties.class })
public class CalowinAccount {

    public static void main(String[] args) {
        SpringApplication.run(CalowinAccount.class, args);

    }

}