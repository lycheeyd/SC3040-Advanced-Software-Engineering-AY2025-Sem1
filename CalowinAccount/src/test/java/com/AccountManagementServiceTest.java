package com;

import com.Account.Entities.AchievementEntry;
import com.Account.Entities.ActionType;
import com.Account.Entities.ProfileEntity;
import com.Account.Entities.UserEntity;
import com.Account.Managers.AccountManagementService;
import com.Account.Services.OTPService;
import com.Account.Services.PasswordSecurityService;
import com.DataTransferObject.LoginResponseDTO;
import com.Database.CalowinDB.AchievementRepository;
import com.Database.CalowinDB.FriendRelationshipRepository;
import com.Database.CalowinDB.TripsRepository;
import com.Database.CalowinDB.UserInfoRepository;
import com.Database.CalowinSecureDB.SecureInfoDBRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountManagementServiceTest {

    @Mock
    private SecureInfoDBRepository secureInfoRepository;
    @Mock
    private UserInfoRepository userInfoRepository;
    @Mock
    private AchievementRepository achievementRepository;
    @Mock
    private TripsRepository tripsRepository; // Added for delete test
    @Mock
    private FriendRelationshipRepository friendRelationshipRepository; // Added for delete test
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private PasswordSecurityService passwordSecurityService;
    @Mock
    private OTPService otpService;

    @InjectMocks
    private AccountManagementService accountManagementService;

    private UserEntity user;
    private ProfileEntity profile;
    private AchievementEntry achievement;
    private final String email = "test@example.com";
    private final String userId = "USER1234";

    @BeforeEach
    void setUp() {
        user = new UserEntity(userId, email, "encodedPassword");
        profile = new ProfileEntity(userId, "Test User", 70.5f, "");
        achievement = new AchievementEntry(userId, 0, 0, "No Medal", "No Medal");
    }

    // --- EXISTING SIGNUP TESTS ---

    @Nested
    @DisplayName("signup Tests")
    class SignupTests {
        @Test
        @DisplayName("signup should create a new user successfully")
        void signup_whenUserDoesNotExist_shouldCreateUser() throws Exception {
            // Arrange
            when(secureInfoRepository.findByEmail(anyString())).thenReturn(Optional.empty());
            when(passwordSecurityService.decrypt(anyString(), any())).thenReturn("ValidPass1!");
            doNothing().when(passwordSecurityService).isPasswordValid(anyString(), anyString());
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(secureInfoRepository.existsByUserID(anyString())).thenReturn(false);
            // Must return the user object that will be saved
            when(secureInfoRepository.save(any(UserEntity.class))).thenReturn(user);

            // Act
            LoginResponseDTO result = accountManagementService.signup("test@example.com", "encPass", "encPass",
                    "Test User",
                    70.5f);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("test@example.com");
            verify(secureInfoRepository, times(1)).save(any(UserEntity.class));
            verify(userInfoRepository, times(1)).save(any());
            verify(achievementRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("signup should throw exception when user already exists")
        void signup_whenUserExists_shouldThrowException() {
            // Arrange
            when(secureInfoRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

            // Act & Assert
            Exception exception = assertThrows(RuntimeException.class, () -> {
                accountManagementService.signup("test@example.com", "encPass", "encPass", "Test User", 70.5f);
            });
            assertThat(exception.getMessage()).isEqualTo("User already exists");
        }
    }

    // --- NEWLY GENERATED LOGIN TESTS ---

    @Nested
    @DisplayName("login Tests")
    class LoginTests {

        @Test
        @DisplayName("login with valid credentials should return DTO")
        void login_withValidCredentials_shouldReturnDTO() throws Exception {
            // Arrange
            when(passwordSecurityService.decrypt(anyString(), any())).thenReturn("decryptedPassword");
            when(secureInfoRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("decryptedPassword", "encodedPassword")).thenReturn(true);
            when(userInfoRepository.findByUserID(userId)).thenReturn(Optional.of(profile));
            when(achievementRepository.findByUserID(userId)).thenReturn(Optional.of(achievement));

            // Act
            LoginResponseDTO result = accountManagementService.login(email, "encryptedPassword");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getUserID()).isEqualTo(userId);
            assertThat(result.getEmail()).isEqualTo(email);
            assertThat(result.getName()).isEqualTo("Test User");
        }

        @Test
        @DisplayName("login when user not found should throw exception")
        void login_whenUserNotFound_shouldThrowException() throws Exception {
            // Arrange
            when(passwordSecurityService.decrypt(anyString(), any())).thenReturn("decryptedPassword");
            when(secureInfoRepository.findByEmail(email)).thenReturn(Optional.empty());

            // Act & Assert
            Exception e = assertThrows(RuntimeException.class, () -> {
                accountManagementService.login(email, "encryptedPassword");
            });
            assertThat(e.getMessage()).isEqualTo("Invalid email or password");
        }

        @Test
        @DisplayName("login with incorrect password should throw exception")
        void login_withIncorrectPassword_shouldThrowException() throws Exception {
            // Arrange
            when(passwordSecurityService.decrypt(anyString(), any())).thenReturn("wrongDecryptedPassword");
            when(secureInfoRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrongDecryptedPassword", "encodedPassword")).thenReturn(false);

            // Act & Assert
            Exception e = assertThrows(RuntimeException.class, () -> {
                accountManagementService.login(email, "encryptedWrongPassword");
            });
            assertThat(e.getMessage()).isEqualTo("Invalid email or password");
        }

        @Test
        @DisplayName("login when profile not found should throw exception")
        void login_whenProfileNotFound_shouldThrowException() throws Exception {
            // Arrange
            when(passwordSecurityService.decrypt(anyString(), any())).thenReturn("decryptedPassword");
            when(secureInfoRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("decryptedPassword", "encodedPassword")).thenReturn(true);
            when(userInfoRepository.findByUserID(userId)).thenReturn(Optional.empty()); // Profile missing

            // Act & Assert
            Exception e = assertThrows(Exception.class, () -> {
                accountManagementService.login(email, "encryptedPassword");
            });
            assertThat(e.getMessage()).isEqualTo("Failed to retrieve user data");
        }

        @Test
        @DisplayName("login when achievement not found should throw exception")
        void login_whenAchievementNotFound_shouldThrowException() throws Exception {
            // Arrange
            when(passwordSecurityService.decrypt(anyString(), any())).thenReturn("decryptedPassword");
            when(secureInfoRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("decryptedPassword", "encodedPassword")).thenReturn(true);
            when(userInfoRepository.findByUserID(userId)).thenReturn(Optional.of(profile));
            when(achievementRepository.findByUserID(userId)).thenReturn(Optional.empty()); // Achievement missing

            // Act & Assert
            Exception e = assertThrows(Exception.class, () -> {
                accountManagementService.login(email, "encryptedPassword");
            });
            assertThat(e.getMessage()).isEqualTo("Failed to retrieve achievement data");
        }
    }

    // --- NEWLY GENERATED DELETE ACCOUNT TESTS ---

    @Nested
    @DisplayName("deleteAccount Tests")
    class DeleteAccountTests {

        @Test
        @DisplayName("deleteAccount with valid OTP should delete all user data")
        void deleteAccount_withValidOtp_shouldDeleteAllUserData() throws Exception {
            // Arrange
            String otpCode = "123456";
            when(otpService.verifyOTP(email, otpCode, ActionType.DELETE_ACCOUNT)).thenReturn(true);

            // Mock void delete methods to do nothing
            doNothing().when(secureInfoRepository).deleteByUserID(userId);
            doNothing().when(userInfoRepository).deleteByUserID(userId);
            doNothing().when(achievementRepository).deleteByUserID(userId);
            doNothing().when(tripsRepository).deleteByUserID(userId);
            doNothing().when(friendRelationshipRepository).deleteByUserID(userId);

            // Act
            accountManagementService.deleteAccount(userId, email, otpCode);

            // Assert
            // Verify that all delete methods were called exactly once
            verify(secureInfoRepository, times(1)).deleteByUserID(userId);
            verify(userInfoRepository, times(1)).deleteByUserID(userId);
            verify(achievementRepository, times(1)).deleteByUserID(userId);
            verify(tripsRepository, times(1)).deleteByUserID(userId);
            verify(friendRelationshipRepository, times(1)).deleteByUserID(userId);
        }

        @Test
        @DisplayName("deleteAccount with invalid OTP should throw exception")
        void deleteAccount_withInvalidOtp_shouldThrowException() {
            // Arrange
            String invalidOtp = "654321";
            when(otpService.verifyOTP(email, invalidOtp, ActionType.DELETE_ACCOUNT)).thenReturn(false);

            // Act & Assert
            Exception e = assertThrows(RuntimeException.class, () -> {
                accountManagementService.deleteAccount(userId, email, invalidOtp);
            });
            assertThat(e.getMessage()).isEqualTo("Invalid OTP");

            // Verify no delete methods were called
            verify(secureInfoRepository, never()).deleteByUserID(anyString());
            verify(userInfoRepository, never()).deleteByUserID(anyString());
        }

        @Test
        @DisplayName("deleteAccount should throw exception if a DB delete fails")
        void deleteAccount_whenDbErrorOccurs_shouldThrowException() {
            // Arrange
            String otpCode = "123456";
            when(otpService.verifyOTP(email, otpCode, ActionType.DELETE_ACCOUNT)).thenReturn(true);

            // Mock the first delete as successful
            doNothing().when(secureInfoRepository).deleteByUserID(userId);

            // Mock the second delete to fail
            doThrow(new RuntimeException("Database connection error"))
                    .when(userInfoRepository).deleteByUserID(userId);

            // Act & Assert
            Exception e = assertThrows(RuntimeException.class, () -> {
                accountManagementService.deleteAccount(userId, email, otpCode);
            });
            assertThat(e.getMessage()).isEqualTo("Database connection error");

            // Verify the first delete was attempted
            verify(secureInfoRepository, times(1)).deleteByUserID(userId);
            // Verify the second (failed) delete was attempted
            verify(userInfoRepository, times(1)).deleteByUserID(userId);
            // Verify subsequent deletes were NOT attempted (due to the exception)
            verify(achievementRepository, never()).deleteByUserID(anyString());
            verify(tripsRepository, never()).deleteByUserID(anyString());
        }
    }
}