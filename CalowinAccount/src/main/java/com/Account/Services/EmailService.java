package com.Account.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Account.Entities.EmailServiceProperties;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

@Service
public class EmailService {

    @Autowired
    private EmailServiceProperties emailServiceProperties;

    public void sendEmail(String recipient, String subject, String messageBody) throws Exception {
        // SMTP server configuration
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", emailServiceProperties.getHost());
        properties.put("mail.smtp.port", emailServiceProperties.getPort());
        properties.put("mail.smtp.ssl.trust", emailServiceProperties.getHost());

        // Create a session with SMTP server
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailServiceProperties.getUsername(), emailServiceProperties.getPassword());
            }
        });

        try {
            // Create email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailServiceProperties.getUsername()));  // sender email
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient)); // recipient email
            message.setSubject(subject);  // email subject
            message.setText(messageBody); // email body

            // Send email
            Transport.send(message);

            System.out.println("Email titled '" + subject + "' sent successfully to '" + recipient);

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
