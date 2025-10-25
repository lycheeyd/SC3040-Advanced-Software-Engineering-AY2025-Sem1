package com;

import com.Account.Entities.ActionType;
import com.Account.Entities.OTPEntry;
import com.Account.Services.EmailService.EmailFactory;
import com.Account.Services.OTPService;
import com.Database.CalowinSecureDB.OTPRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OTPServiceTest {

    @Mock
    private OTPRepository otpRepository;

    @Mock
    private EmailFactory emailService;

    @InjectMocks
    private OTPService otpService;

    private final String email = "test@example.com";
    private final String otpCode = "123456";
    private final ActionType type = ActionType.SIGN_UP;

    @Test
    @DisplayName("sendOtpCode should generate, save, and send an OTP")
    void sendOtpCode_shouldGenerateSaveAndSendOtp() throws Exception {
        // Arrange
        when(otpRepository.save(any(OTPEntry.class))).thenReturn(null);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        // Act
        otpService.sendOtpCode(email, type);

        // Assert
        verify(otpRepository, times(1)).save(any(OTPEntry.class));
        verify(emailService, times(1)).sendEmail(eq(email), eq(type.getSubject()), anyString());
    }

    @Test
    @DisplayName("sendOtpCode should delete existing OTP of a user and type")
    void sendOtpCode_shouldDeleteExistingOtp() throws Exception {
        // Arrange
        OTPEntry existingOtp = new OTPEntry(email, "old-otp", LocalDateTime.now(), type);
        when(otpRepository.findByEmailAndOtpType(email, type)).thenReturn(Optional.of(existingOtp));

        // Act
        otpService.sendOtpCode(email, type);

        // Assert
        // Verify the existing OTP was deleted
        verify(otpRepository, times(1)).delete(existingOtp);
        // Verify a new one was saved
        verify(otpRepository, times(1)).save(any(OTPEntry.class));
        // Verify the email was sent
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
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

    // --- NEWLY GENERATED SCHEDULED TEST ---

    @Test
    @DisplayName("cleanUpExpiredOTPs should delete expired but not valid OTPs")
    void cleanUpExpiredOTPs_shouldDeleteExpiredButNotValid() {
        // Arrange
        OTPEntry expiredOtp = new OTPEntry("expired@test.com", "111", LocalDateTime.now().minusDays(1), type);
        OTPEntry validOtp = new OTPEntry("valid@test.com", "222", LocalDateTime.now().plusDays(1), type);
        List<OTPEntry> otpList = Arrays.asList(expiredOtp, validOtp);

        when(otpRepository.findAll()).thenReturn(otpList);

        // Act
        otpService.cleanUpExpiredOTPs();

        // Assert
        // Verify that delete was called exactly once, with the expired OTP
        verify(otpRepository, times(1)).delete(expiredOtp);
        // Verify that delete was NOT called with the valid OTP
        verify(otpRepository, never()).delete(validOtp);
    }
}