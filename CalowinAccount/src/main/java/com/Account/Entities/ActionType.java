package com.Account.Entities;

public enum ActionType {
    DEFAULT(
            "[Calowin] One-time password (do not reply)",
            "Hi there!\n\nHere is your OTP: {placeholder}\n\nIf you don't use this code within 1 day, it will expire.\n\nPlease do not share this code with anyone.\n\nThanks,\nThe Calowin Team"),
    SIGN_UP(
            "[Calowin] Verify email (do not reply)",
            "Welcome!\n\nUse this OTP to verify your email: {placeholder}\n\nThanks for joining us!\n\nPlease do not share this code with anyone.\n\nThanks,\nThe Calowin Team"),
    FORGOT_PASSWORD(
            "[Calowin] Request new password (do not reply)",
            "Hi there!\n\nUse this OTP to retrieve your new password: {placeholder}\n\nIf you don't use this code within 1 day, it will expire.\n\nPlease do not share this code with anyone.\n\nThanks,\nThe Calowin Team"),
    DELETE_ACCOUNT(
            "[Calowin] Account deletion (do not reply)",
            "Hi there!\n\nUse this OTP to delete your Calowin account: {placeholder}\n\nIf you don't use this code within 1 day, it will expire.\n\nPlease do not share this code with anyone.\n\nThanks,\nThe Calowin Team"),
    SEND_NEW_PASSWORD(
            "[Calowin] New password (do not reply)",
            "Hi there!\n\nHere is the new password for your Calowin account: {placeholder}\n\nPlease do not share your password with anyone.\n\nThanks,\nThe Calowin Team");

    private final String subject;
    private final String messageBody;

    ActionType(String subject, String messageBody) {
        this.subject = subject;
        this.messageBody = messageBody;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessageBody(String value) {
        return messageBody.replace("{placeholder}", value);
    }
}
