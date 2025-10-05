package com;

import com.Account.Entities.AchievementEntry;
import com.Account.Entities.FriendStatus;
import com.Account.Entities.ProfileEntity;
import com.Account.Entities.UserEntity;
import com.Account.Managers.ProfileManagementService;
import com.DataTransferObject.LoginResponseDTO;
import com.DataTransferObject.ViewProfileResponseDTO;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileManagementServiceTest {

    // Mock all the dependencies required by ProfileManagementService
    @Mock
    private UserInfoRepository userInfoRepository;
    @Mock
    private SecureInfoDBRepository secureInfoRepository;
    @Mock
    private AchievementRepository achievementRepository;
    @Mock
    private ExternalServiceClient externalServiceClient;

    // Inject the mocks into the service under test
    @InjectMocks
    private ProfileManagementService profileManagementService;

    private ProfileEntity profile;
    private AchievementEntry achievement;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        // Create common test data objects to use in the tests
        profile = new ProfileEntity("USER001", "Test User", 75.5f, "A simple bio");
        achievement = new AchievementEntry("USER001", 150, 250, "Silver", "Gold");
        user = new UserEntity("USER001", "test@example.com", "password");
    }

    //--- Tests for editProfile ---
    @Test
    @DisplayName("editProfile should update and save the profile when user is found")
    void editProfile_whenUserExists_shouldUpdateAndReturnProfile() throws Exception {
        // Arrange
        when(userInfoRepository.findByUserID("USER001")).thenReturn(Optional.of(profile));
        when(userInfoRepository.save(any(ProfileEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ProfileEntity updatedProfile = profileManagementService.editProfile("USER001", "Updated Name", 80.0f, "New Bio");

        // Assert
        assertThat(updatedProfile).isNotNull();
        assertThat(updatedProfile.getName()).isEqualTo("Updated Name");
        assertThat(updatedProfile.getWeight()).isEqualTo(80.0f);
        assertThat(updatedProfile.getBio()).isEqualTo("New Bio");
        verify(userInfoRepository, times(1)).findByUserID("USER001");
        verify(userInfoRepository, times(1)).save(profile);
    }

    @Test
    @DisplayName("editProfile should throw RuntimeException when user is not found")
    void editProfile_whenUserNotFound_shouldThrowRuntimeException() {
        // Arrange
        when(userInfoRepository.findByUserID(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            profileManagementService.editProfile("UNKNOWN_USER", "Name", 70f, "Bio");
        });
        assertThat(exception.getMessage()).isEqualTo("User not found");
        verify(userInfoRepository, times(1)).findByUserID("UNKNOWN_USER");
        verify(userInfoRepository, never()).save(any());
    }

    //--- Tests for viewProfile (other's profile) ---
    @Test
    @DisplayName("viewProfile (other) should return full profile DTO when user is found")
    void viewProfile_forOtherUser_whenFound_shouldReturnViewProfileResponseDTO() throws Exception {
        // Arrange
        String selfId = "SELF001";
        String otherId = "OTHER002";

        ProfileEntity otherProfile = new ProfileEntity(otherId, "Other User", 80f, "Other bio");
        AchievementEntry otherAchievement = new AchievementEntry(otherId, 10, 20, "Bronze", "Bronze");

        when(userInfoRepository.findByUserID(otherId)).thenReturn(Optional.of(otherProfile));
        when(achievementRepository.findByUserID(otherId)).thenReturn(Optional.of(otherAchievement));
        when(externalServiceClient.getFriendStatus(selfId, otherId)).thenReturn(FriendStatus.STRANGER);

        // Act
        ViewProfileResponseDTO result = profileManagementService.viewProfile(selfId, otherId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserID()).isEqualTo(otherId);
        assertThat(result.getName()).isEqualTo("Other User");
        assertThat(result.getFriendStatus()).isEqualTo(FriendStatus.STRANGER);
        assertThat(result.getCalorieMedal()).isEqualTo("Bronze");
        verify(userInfoRepository, times(1)).findByUserID(otherId);
        verify(achievementRepository, times(1)).findByUserID(otherId);
        verify(externalServiceClient, times(1)).getFriendStatus(selfId, otherId);
    }

    @Test
    @DisplayName("viewProfile (other) should throw exception when user is not found")
    void viewProfile_forOtherUser_whenNotFound_shouldThrowException() {
        // Arrange
        when(userInfoRepository.findByUserID("UNKNOWN")).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            profileManagementService.viewProfile("SELF001", "UNKNOWN");
        });
        assertThat(exception.getMessage()).isEqualTo("User not found");
    }

    //--- Tests for viewProfile (self profile) ---
    @Test
    @DisplayName("viewProfile (self) should return full profile DTO when user is found")
    void viewProfile_forSelf_whenFound_shouldReturnLoginResponseDTO() throws Exception {
        // Arrange
        when(secureInfoRepository.findByUserID("USER001")).thenReturn(Optional.of(user));
        when(userInfoRepository.findByUserID("USER001")).thenReturn(Optional.of(profile));
        when(achievementRepository.findByUserID("USER001")).thenReturn(Optional.of(achievement));

        // Act
        LoginResponseDTO result = profileManagementService.viewProfile("USER001");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserID()).isEqualTo("USER001");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getCarbonMedal()).isEqualTo("Silver");
        verify(secureInfoRepository, times(1)).findByUserID("USER001");
        verify(userInfoRepository, times(1)).findByUserID("USER001");
        verify(achievementRepository, times(1)).findByUserID("USER001");
    }

    @Test
    @DisplayName("viewProfile (self) should throw exception when user profile is not found")
    void viewProfile_forSelf_whenProfileNotFound_shouldThrowException() {
        // Arrange
        when(secureInfoRepository.findByUserID("USER001")).thenReturn(Optional.of(user));
        when(userInfoRepository.findByUserID("USER001")).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            profileManagementService.viewProfile("USER001");
        });
        assertThat(exception.getMessage()).isEqualTo("User not found");
    }
}