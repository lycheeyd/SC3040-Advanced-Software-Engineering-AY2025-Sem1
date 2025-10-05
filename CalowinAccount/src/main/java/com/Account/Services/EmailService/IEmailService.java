package com.Account.Services.EmailService;

public interface IEmailService {
    void sendEmail(String recipient, String subject, String messageBody) throws Exception;
}
