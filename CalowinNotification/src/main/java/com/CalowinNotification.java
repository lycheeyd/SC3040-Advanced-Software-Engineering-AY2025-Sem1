package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan(basePackages = "com.RelationshipNotification.Entity")

@SpringBootApplication(scanBasePackages = { "com.Relationship", "com.Database", "com.RestController" })
public class CalowinNotification {
    public static void main(String[] args) {
        SpringApplication.run(CalowinNotification.class, args);
    }
}