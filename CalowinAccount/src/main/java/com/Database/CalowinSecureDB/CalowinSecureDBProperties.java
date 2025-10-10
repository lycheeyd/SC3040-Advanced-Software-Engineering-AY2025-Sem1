package com.Database.CalowinSecureDB;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.Database.DBProperties;

@Component
@ConfigurationProperties(prefix = "spring.datasource.calowin-secure-db")
public class CalowinSecureDBProperties extends DBProperties {

}
