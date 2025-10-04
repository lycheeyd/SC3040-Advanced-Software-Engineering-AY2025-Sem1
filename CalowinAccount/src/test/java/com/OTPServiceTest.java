package com;

import com.Account.Entities.ActionType;
import com.Account.Entities.OTPEntry;
import com.Account.Services.EmailService;
import com.Account.Services.OTPService;
import com.Database.CalowinSecureDB.OTPRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OTPServiceTest {

    @Mock
    private OTPRepository otpRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OTPService otpService;

    private final String email = "test@example.com";
    private final String otpCode = "123456";
    private final ActionType type = ActionType.SIGN_UP;

    @Test
    @DisplayName("sendOtpCode should generate, save, and send an OTP")
    void sendOtpCode_shouldGenerateSaveAndSendOtp() throws Exception {
        // Arrange
        // Mock the repository save operation to do nothing
        when(otpRepository.save(any(OTPEntry.class))).thenReturn(null);
        // Mock the email service to do nothing
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        // Act
        otpService.sendOtpCode(email, type);

        // Assert
        // Verify that a new OTP was saved to the repository
        verify(otpRepository, times(1)).save(any(OTPEntry.class));
        // Verify that the email service was called to send the email
        verify(emailService, times(1)).sendEmail(eq(email), eq(type.getSubject()), anyString());
    }

    @Test
    @DisplayName("verifyOTP should return true for a valid, non-expired OTP")
    void verifyOTP_withValidCodeAndNotExpired_shouldReturnTrue() {
        // Arrange
        OTPEntry validOtp = new OTPEntry(email, otpCode, LocalDateTime.now().plusHours(1), type);
        when(otpRepository.findByEmailAndOtpType(email, type)).thenReturn(Optional.of(validOtp));

        // Act
        boolean result = otpService.verifyOTP(email, otpCode, type);

        // Assert
        assertThat(result).isTrue();
        // Verify that the OTP is deleted after successful verification
        verify(otpRepository, times(1)).deleteByEmailAndOtpType(email, type);
    }

    @Test
    @DisplayName("verifyOTP should return false for an expired OTP")
    void verifyOTP_withExpiredOtp_shouldReturnFalse() {
        // Arrange
        OTPEntry expiredOtp = new OTPEntry(email, otpCode, LocalDateTime.now().minusHours(1), type);
        when(otpRepository.findByEmailAndOtpType(email, type)).thenReturn(Optional.of(expiredOtp));

        // Act
        boolean result = otpService.verifyOTP(email, otpCode, type);

        // Assert
        assertThat(result).isFalse();
        // Verify that the expired OTP is cleaned up
        verify(otpRepository, times(1)).deleteByEmailAndOtpType(email, type);
    }

    @Test
    @DisplayName("verifyOTP should return false for an incorrect OTP code")
    void verifyOTP_withIncorrectCode_shouldReturnFalse() {
        // Arrange
        String incorrectCode = "654321";
        OTPEntry validOtp = new OTPEntry(email, otpCode, LocalDateTime.now().plusHours(1), type);
        when(otpRepository.findByEmailAndOtpType(email, type)).thenReturn(Optional.of(validOtp));

        // Act
        boolean result = otpService.verifyOTP(email, incorrectCode, type);

        // Assert
        assertThat(result).isFalse();
        // Verify that the OTP is NOT deleted if the code is wrong
        verify(otpRepository, never()).deleteByEmailAndOtpType(anyString(), any());
    }

    @Test
    @DisplayName("verifyOTP should return false when no OTP is found")
    void verifyOTP_whenNoOtpFound_shouldReturnFalse() {
        // Arrange
        when(otpRepository.findByEmailAndOtpType(email, type)).thenReturn(Optional.empty());

        // Act
        boolean result = otpService.verifyOTP(email, otpCode, type);

        // Assert
        assertThat(result).isFalse();
        verify(otpRepository, never()).deleteByEmailAndOtpType(anyString(), any());
    }
}