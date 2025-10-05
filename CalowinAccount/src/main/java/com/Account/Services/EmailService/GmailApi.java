package com.Account.Services.EmailService;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Properties;

import com.Account.Entities.EmailServiceProperties;

@Service("gmailApiService")
public class GmailApi implements IEmailService {

    @Autowired
    private EmailServiceProperties emailProperties;

    String clientId = emailProperties.getClientId();
    String clientSecret = emailProperties.getClientSecret();
    String refreshToken = emailProperties.getRefreshToken();
    String userEmail = emailProperties.getUsername();

    @Override
    public void sendEmail(String recipient, String subject, String messageBody) throws Exception {
        Credential credential = getCredential();

        Gmail service = new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("Calowin")
                .build();

        MimeMessage email = createEmail(recipient, userEmail, subject, messageBody);
        Message message = createMessageWithEmail(email);

        service.users().messages().send("me", message).execute();
        System.out.println("[Gmail API] Sent email [" + subject + "] to " + recipient);
    }

    private Credential getCredential() throws Exception {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalStateException(
                    "No refresh token found. Please set gmail.refresh.token in application.properties."
            );
        }

        return new GoogleCredential.Builder()
                .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                .setJsonFactory(JacksonFactory.getDefaultInstance())
                .setClientSecrets(clientId, clientSecret)
                .build()
                .setRefreshToken(refreshToken);
    }

    private MimeMessage createEmail(String to, String from, String subject, String bodyText)
            throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    private Message createMessageWithEmail(MimeMessage emailContent) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.getUrlEncoder().encodeToString(rawMessageBytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }
}

