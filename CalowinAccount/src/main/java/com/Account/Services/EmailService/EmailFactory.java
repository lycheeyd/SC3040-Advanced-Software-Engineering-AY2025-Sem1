package com.Account.Services.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailFactory implements IEmailService {

    private final IEmailService delegate;

    @Autowired
    public EmailFactory(
            @Value("${email.provider}") String provider,
            @Qualifier("gmailSmtpService") IEmailService smtpService,
            @Qualifier("gmailApiService") IEmailService apiService
    ) {
        if ("gmail-api".equalsIgnoreCase(provider)) {
            delegate = apiService;
            System.out.println("Using Gmail API (HTTPS 443)");
        } else {
            throw new IllegalStateException("FATAL: Email provider set to unexpected value: " + provider + ". Expected 'gmail-api'.");
//            delegate = smtpService;
//            System.out.println("Using Gmail SMTP (port 587)");
        }
    }

    @Override
    public void sendEmail(String recipient, String subject, String messageBody) throws Exception {
        delegate.sendEmail(recipient, subject, messageBody);
    }
}
