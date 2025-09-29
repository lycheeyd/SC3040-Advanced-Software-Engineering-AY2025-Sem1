package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

import com.Account.Entities.EmailServiceProperties;
import com.Account.Entities.ActionType;
import com.Account.Services.EmailService;
import com.Account.Services.OTPService;
import com.Database.CalowinDB.CalowinDBProperties;
import com.Database.CalowinSecureDB.CalowinSecureDBProperties;

@SpringBootApplication(scanBasePackages = { "com.Account", "com.DataTransferObject", "com.Database" })
@EnableConfigurationProperties({ EmailServiceProperties.class, CalowinSecureDBProperties.class,
        CalowinDBProperties.class })
public class test {

    public static void main2(String[] args) {
        // Start the Spring Boot application and get the application context
        ApplicationContext context = SpringApplication.run(test.class, args);

        // Get the EmailService bean from the application context
        OTPService otpService = context.getBean(OTPService.class);
        EmailService emailService = context.getBean(EmailService.class);

        // Test sending an email
        String recipient = "calowinsc2006@gmail.com"; // Replace with the actual recipient's email
        String subject = "Test Email";
        String messageBody = "This is a test email from the EmailService.";

        // Call the sendEmail method to send the test email
        try {
            otpService.sendOtpCode(recipient, ActionType.DEFAULT);
            otpService.sendOtpCode(recipient, ActionType.SIGN_UP);
            otpService.sendOtpCode(recipient, ActionType.FORGOT_PASSWORD);
            otpService.sendOtpCode(recipient, ActionType.DELETE_ACCOUNT);
            otpService.sendOtpCode(recipient, ActionType.SEND_NEW_PASSWORD);
            System.out.println("SUCCESSSS");
        } catch (Exception e) {
            System.out.println("ERRORRRRRRRRRRRRRR");
            System.out.println(e);
        }
    }

}
