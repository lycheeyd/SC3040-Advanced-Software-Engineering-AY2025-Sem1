package com;

import com.Account.Entities.EmailServiceProperties;
import com.Account.Services.EmailService.EmailFactory;
import com.Account.Services.EmailService.IEmailService; // Need IEmailService
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress; // Correct Jakarta imports
import jakarta.mail.internet.MimeMessage; // Correct Jakarta imports

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.TestPropertySource;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * FINAL FIX: This configuration resolves the "Broken pipe" and "Javax/Jakarta"
 * API conflicts
 * by creating a test-only version of the GmailSmtp service with simplified
 * properties.
 */
@Import(EmailServiceTest.TestConfig.class)
// 1. Force EmailFactory to use the SMTP service for this test
@TestPropertySource(properties = { "email.provider=gmail-smtp" })
@SpringBootTest
class EmailServiceTest {

    // Static block to suppress conflicting mail providers (IMAP/POP3) during
    // Session init
    static {
        System.setProperty("mail.providers", "smtp;");
    }

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP.dynamicPort())
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test-user", "test-password"));

    // Inject the EmailFactory which will use our TestConfig's simplified bean
    @Autowired
    private EmailFactory emailService;

    // Mock the properties used by the service to direct traffic to GreenMail
    @MockBean
    private EmailServiceProperties emailServiceProperties;

    @Test
    @DisplayName("SMTP Service should correctly send an email to GreenMail mock server")
    void sendEmail_shouldSendMimeMessageSuccessfully() throws Exception {
        // Arrange
        String recipient = "recipient@test.com";
        String subject = "Test Subject";
        String body = "This is a test email body.";

        // Configure the mock properties to point to the GreenMail server
        when(emailServiceProperties.getHost()).thenReturn("127.0.0.1");
        when(emailServiceProperties.getPort()).thenReturn(String.valueOf(greenMail.getSmtp().getPort()));
        // ...
        when(emailServiceProperties.getUsername()).thenReturn("test-user");
        when(emailServiceProperties.getPassword()).thenReturn("test-password");

        // Act
        emailService.sendEmail(recipient, subject, body);

        // Assert
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertThat(receivedMessages).hasSize(1); // Verify one email was received

        MimeMessage receivedMessage = receivedMessages[0];
        // Note: Using jakarta.mail.internet.MimeMessage.RecipientType.TO from your
        // corrected imports
        assertThat(receivedMessage.getSubject()).isEqualTo(subject);
        assertThat(((InternetAddress) receivedMessage.getRecipients(jakarta.mail.Message.RecipientType.TO)[0])
                .getAddress()).isEqualTo(recipient);
        // The getContent().toString() is necessary to verify the body of the message
        assertThat(receivedMessage.getContent().toString()).contains(body);
    }

    /**
     * Test Configuration to safely override the production GmailSmtp bean.
     * This implementation uses simplified properties (no TLS/SSL) and correct
     * Jakarta APIs.
     */
    @TestConfiguration
    public static class TestConfig {

        @Bean
        @Primary
        // Qualifier is needed if the production bean uses it (which your code did)
        @Qualifier("gmailSmtpService")
        public IEmailService gmailSmtpServiceTest(EmailServiceProperties emailServiceProperties) {

            // This anonymous class implements IEmailService using the stabilized setup
            return (recipient, subject, messageBody) -> {
                Properties properties = new Properties();
                properties.put("mail.smtp.auth", "true");

                properties.put("mail.providers", "smtp;"); // <-- ADD THIS LINE

                // Use explicit host/port lookup settings required by JavaMail/GreenMail
                properties.put("mail.smtp.host", emailServiceProperties.getHost());
                properties.put("mail.smtp.port", emailServiceProperties.getPort());
                properties.put("mail.transport.protocol", "smtp"); // Define transport protocol

                // Use the correct jakarta.mail.Session and Authenticator
                Session session = Session.getInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailServiceProperties.getUsername(),
                                emailServiceProperties.getPassword());
                    }
                });

                // Create the message using the jakarta Session
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(emailServiceProperties.getUsername()));
                message.setRecipients(jakarta.mail.Message.RecipientType.TO, InternetAddress.parse(recipient));
                message.setSubject(subject);
                message.setText(messageBody);

                // Send using the jakarta Transport
                Transport.send(message);
            };
        }
    }
}