package com.Account.Entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "OTPRegister")

public class OTPEntry {

    @Id
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "otp_code", length = 6, nullable = false)
    private String otpCode;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "otp_type", nullable = false)
    private ActionType otpType;

    // Default constructor is required by JPA
    public OTPEntry() {
    }

    public OTPEntry(String email, String otpCode, LocalDateTime expiresAt, ActionType otpType) {
        this.email = email;
        this.otpCode = otpCode;
        this.expiresAt = expiresAt;
        this.otpType = otpType;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtpCode() {
        return this.otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public LocalDateTime getExpiresAt() {
        return this.expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public ActionType getOtpType() {
        return this.otpType;
    }

    public void setOtpType(ActionType otpType) {
        this.otpType = otpType;
    }

}
