package com;

import com.Account.Entities.ActionType;
import com.Account.Entities.UserEntity;
import com.Account.Managers.PasswordManagementService;
import com.Account.Services.EmailService.EmailFactory;
import com.Account.Services.OTPService;
import com.Account.Services.PasswordSecurityService;
import com.Database.CalowinSecureDB.SecureInfoDBRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordManagementServiceTest {

    @Mock
    private SecureInfoDBRepository secureInfoRepository;
    @Mock
    private PasswordSecurityService passwordSecurityService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailFactory emailService;
    @Mock
    private OTPService otpService;

    @InjectMocks
    private PasswordManagementService passwordManagementService;

    private UserEntity user;
    private final String userId = "USERTEST1";
    private final String userEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        user = new UserEntity(userId, userEmail, "encodedOldPassword");
        // Manually set the value for the @Value-injected field
        ReflectionTestUtils.setField(passwordManagementService, "SECRET_KEY", "test-secret");
    }

    @Nested
    @DisplayName("changePassword Tests")
    class ChangePasswordTests {

        @Test
        @DisplayName("should change password successfully with valid inputs")
        void changePassword_withValidInputs_shouldSucceed() throws Exception {
            // Arrange
            // Mock decryption to simplify the test
            when(passwordSecurityService.decrypt(anyString(), anyString())).thenAnswer(i -> i.getArgument(0));
            doNothing().when(passwordSecurityService).isPasswordValid("newPassword", "newPassword");
            when(secureInfoRepository.findByUserID(userId)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
            when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

            // Act
            passwordManagementService.changePassword(userId, "oldPassword", "newPassword", "newPassword");

            // Assert
            ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
            verify(secureInfoRepository).save(userCaptor.capture());
            assertThat(userCaptor.getValue().getPassword()).isEqualTo("encodedNewPassword");
        }

        @Test
        @DisplayName("should throw exception if user is not found")
        void changePassword_whenUserNotFound_shouldThrowException() throws Exception {
            // REMOVED: when(passwordSecurityService.decrypt(anyString(),
            // anyString())).thenAnswer(i -> i.getArgument(0));
            when(secureInfoRepository.findByUserID(userId)).thenReturn(Optional.empty());

            Exception e = assertThrows(RuntimeException.class,
                    () -> passwordManagementService.changePassword(userId, "old", "new", "new"));

            assertThat(e.getMessage()).isEqualTo("Invalid user");
            verify(secureInfoRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw exception if old password does not match")
        void changePassword_whenOldPasswordIsIncorrect_shouldThrowException() throws Exception {
            when(passwordSecurityService.decrypt(anyString(), anyString())).thenAnswer(i -> i.getArgument(0));
            doNothing().when(passwordSecurityService).isPasswordValid("newPassword", "newPassword");
            when(secureInfoRepository.findByUserID(userId)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrongOldPassword", "encodedOldPassword")).thenReturn(false);

            Exception e = assertThrows(RuntimeException.class, () -> passwordManagementService.changePassword(userId,
                    "wrongOldPassword", "newPassword", "newPassword"));

            assertThat(e.getMessage()).isEqualTo("Wrong password");
            verify(secureInfoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("forgotPassword Tests")
    class ForgotPasswordTests {

        @Test
        @DisplayName("should set new password and send email with valid OTP")
        void forgotPassword_withValidOtp_shouldSucceed() throws Exception {
            // Arrange
            when(secureInfoRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
            when(otpService.verifyOTP(userEmail, "123456", ActionType.FORGOT_PASSWORD)).thenReturn(true);
            when(passwordSecurityService.generateRandomPassword()).thenReturn("randomNewPassword");
            when(passwordEncoder.encode("randomNewPassword")).thenReturn("encodedRandomNewPassword");
            // We don't need a doNothing() for void methods unless we need to throw an
            // exception.

            // Act
            passwordManagementService.forgotPassword(userEmail, "123456");

            // Assert
            // Verify the user's password was updated and saved
            ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
            verify(secureInfoRepository).save(userCaptor.capture());
            assertThat(userCaptor.getValue().getPassword()).isEqualTo("encodedRandomNewPassword");

            // Use ArgumentCaptor to verify the email content correctly
            ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
            verify(emailService, times(1)).sendEmail(eq(userEmail), anyString(), bodyCaptor.capture());
            assertThat(bodyCaptor.getValue()).contains("randomNewPassword");
        }

        @Test
        @DisplayName("should throw exception if user email is not found")
        void forgotPassword_whenUserNotFound_shouldThrowException() throws Exception { // <-- FIX IS HERE
            when(secureInfoRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

            Exception e = assertThrows(RuntimeException.class,
                    () -> passwordManagementService.forgotPassword(userEmail, "123456"));

            assertThat(e.getMessage()).isEqualTo("Invalid user");
            verify(otpService, never()).verifyOTP(anyString(), anyString(), any());
            verify(secureInfoRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw exception if OTP is invalid")
        void forgotPassword_whenOtpIsInvalid_shouldThrowException() throws Exception { // <-- FIX IS HERE
            when(secureInfoRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
            when(otpService.verifyOTP(userEmail, "wrong-otp", ActionType.FORGOT_PASSWORD)).thenReturn(false);

            Exception e = assertThrows(RuntimeException.class,
                    () -> passwordManagementService.forgotPassword(userEmail, "wrong-otp"));

            assertThat(e.getMessage()).isEqualTo("Invalid OTP");
            verify(secureInfoRepository, never()).save(any());
            verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
        }
    }
}