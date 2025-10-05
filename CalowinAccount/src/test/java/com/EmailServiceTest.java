package com;

import com.Account.Entities.EmailServiceProperties;
import com.Account.Services.EmailService.EmailFactory;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class EmailServiceTest {

    // Starts an in-memory SMTP server before tests and stops it after
    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test-user", "test-password"));

    // We inject the real EmailService bean
    @Autowired
    private EmailFactory emailService;

    // We mock the properties bean to redirect the service to our fake server
    @MockBean
    private EmailServiceProperties emailServiceProperties;

    @Test
    @DisplayName("sendEmail should correctly construct and send an email")
    void sendEmail_shouldSendMimeMessageSuccessfully() throws Exception {
        // Arrange
        // Configure the mock properties to point to the GreenMail server
        when(emailServiceProperties.getHost()).thenReturn(ServerSetupTest.SMTP.getBindAddress());
        when(emailServiceProperties.getPort()).thenReturn(String.valueOf(ServerSetupTest.SMTP.getPort()));
        when(emailServiceProperties.getUsername()).thenReturn("test-user");
        when(emailServiceProperties.getPassword()).thenReturn("test-password");

        String recipient = "recipient@test.com";
        String subject = "Test Subject";
        String body = "This is a test email body.";

        // Act
        emailService.sendEmail(recipient, subject, body);

        // Assert
        // Retrieve messages received by the fake server
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertThat(receivedMessages).hasSize(1); // Verify one email was received

        MimeMessage receivedMessage = receivedMessages[0];
        // Verify the email's details
        assertThat(receivedMessage.getSubject()).isEqualTo(subject);
        assertThat(receivedMessage.getRecipients(MimeMessage.RecipientType.TO)[0].toString()).isEqualTo(recipient);
        assertThat(receivedMessage.getContent().toString()).contains(body);
    }
}