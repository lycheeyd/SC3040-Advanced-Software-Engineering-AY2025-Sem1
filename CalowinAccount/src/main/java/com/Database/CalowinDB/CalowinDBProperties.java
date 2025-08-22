package com.Database.CalowinDB;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.Database.DBProperties;

@Component
@ConfigurationProperties(prefix = "spring.datasource.calowin-db")
public class CalowinDBProperties extends DBProperties {
    

}
