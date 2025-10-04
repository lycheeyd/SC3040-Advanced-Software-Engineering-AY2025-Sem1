package com;

import com.Account.Entities.UserEntity;
import com.Account.Managers.AccountManagementService;
import com.Account.Services.OTPService;
import com.Account.Services.PasswordSecurityService;
import com.DataTransferObject.LoginResponseDTO;
import com.Database.CalowinDB.AchievementRepository;
import com.Database.CalowinDB.UserInfoRepository;
import com.Database.CalowinSecureDB.SecureInfoDBRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    private PasswordEncoder passwordEncoder;
    @Mock
    private PasswordSecurityService passwordSecurityService;
    @Mock
    private OTPService otpService; // Though not used in signup/login, good practice

    @InjectMocks
    private AccountManagementService accountManagementService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity("USER1234", "test@example.com", "encodedPassword");
    }

    @Test
    @DisplayName("signup should create a new user successfully")
    void signup_whenUserDoesNotExist_shouldCreateUser() throws Exception {
        // Arrange
        when(secureInfoRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordSecurityService.decrypt(anyString(), any())).thenReturn("ValidPass1!");
        doNothing().when(passwordSecurityService).isPasswordValid(anyString(), anyString());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(secureInfoRepository.existsByUserID(anyString())).thenReturn(false);
        when(secureInfoRepository.save(any(UserEntity.class))).thenReturn(user);

        // Act
        LoginResponseDTO result = accountManagementService.signup("test@example.com", "encPass", "encPass", "Test User", 70.5f);

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