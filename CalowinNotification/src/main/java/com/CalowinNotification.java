package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan(basePackages = "com.Relationship.Entity")

@SpringBootApplication(scanBasePackages = { "com.Relationship", "com.Database" })
public class CalowinNotification {
    public static void main(String[] args) {
        SpringApplication.run(CalowinNotification.class, args);
    }
}