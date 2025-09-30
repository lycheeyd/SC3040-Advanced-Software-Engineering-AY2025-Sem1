package com.Services;



import com.DataTransferObject.AchievementResponse;
import com.Entity.AchievementEntity;
import com.repository.AchievementRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AchievementServiceTest {

    @Mock
    private AchievementRepository achievementRepository;

    @InjectMocks
    private AchievementService achievementService;

    @Test
    @DisplayName("addTripMetricsToAchievement should update existing achievement for an existing user")
    void addTripMetrics_whenUserExists_shouldUpdateExistingAchievement() {
        // Arrange
        String userId = "user-with-history";
        int initialCarbonExp = 100;
        int initialCalorieExp = 200;
        int carbonToAdd = 50;
        int caloriesToAdd = 75;

        // Create a pre-existing achievement record
        AchievementEntity existingAchievement = new AchievementEntity();
        existingAchievement.setTotalCarbonSavedExp(initialCarbonExp);
        existingAchievement.setTotalCalorieBurntExp(initialCalorieExp);

        // Stub the repository to return this existing record
        when(achievementRepository.fetchAchievementForUser(userId)).thenReturn(Optional.of(existingAchievement));

        // Act
        achievementService.addTripMetricsToAchievement(userId, carbonToAdd, caloriesToAdd);

        // Assert
        // Capture the achievement object that is passed to the save method
        ArgumentCaptor<AchievementEntity> achievementCaptor = ArgumentCaptor.forClass(AchievementEntity.class);
        verify(achievementRepository).updateAchievement(achievementCaptor.capture(), eq(userId));
        AchievementEntity savedAchievement = achievementCaptor.getValue();

        // Check that the saved object has the correctly updated totals
        assertThat(savedAchievement.getTotalCarbonSavedExp()).isEqualTo(initialCarbonExp + carbonToAdd); // 100 + 50 = 150
        assertThat(savedAchievement.getTotalCalorieBurntExp()).isEqualTo(initialCalorieExp + caloriesToAdd); // 200 + 75 = 275

        // Verify that findByUserId and save were each called once
        verify(achievementRepository, times(1)).fetchAchievementForUser(userId);
        verify(achievementRepository, times(1)).updateAchievement(any(AchievementEntity.class), eq(userId));
    }

    @Test
    @DisplayName("addTripMetricsToAchievement should create a new achievement for a new user")
    void addTripMetrics_whenUserIsNew_shouldCreateNewAchievement() {
        // Arrange
        String userId = "new-user";
        int carbonToAdd = 50;
        int caloriesToAdd = 75;

        // Stub the repository to return empty, simulating a new user
        when(achievementRepository.fetchAchievementForUser(userId)).thenReturn(Optional.empty());

        // Act
        achievementService.addTripMetricsToAchievement(userId, carbonToAdd, caloriesToAdd);

        // Assert
        // Capture the new achievement object passed to the save method
        ArgumentCaptor<AchievementEntity> achievementCaptor = ArgumentCaptor.forClass(AchievementEntity.class);
        verify(achievementRepository).updateAchievement(achievementCaptor.capture(), eq(userId));
        AchievementEntity savedAchievement = achievementCaptor.getValue();

        // Check that the new object has the correct initial totals
        assertThat(savedAchievement.getTotalCarbonSavedExp()).isEqualTo(carbonToAdd);
        assertThat(savedAchievement.getTotalCalorieBurntExp()).isEqualTo(caloriesToAdd);
    }

    @Test
    @DisplayName("getAchievementProgress should return AchievementResponse for an existing user")
    void getAchievementProgress_whenUserExists_shouldReturnAchievementResponse() {
        // Arrange
        String userId = "user-with-progress";
        AchievementEntity existingAchievement = new AchievementEntity();
        existingAchievement.setTotalCarbonSavedExp(1500); // Should be EcoBronze
        existingAchievement.setTotalCalorieBurntExp(5500); // Should be CalorieSilver

        when(achievementRepository.fetchAchievementForUser(userId)).thenReturn(Optional.of(existingAchievement));

        // Act
        AchievementResponse response = achievementService.getAchievementProgress(userId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getTotalCarbonSavedExp()).isEqualTo(1500);
        assertThat(response.getCarbonSavedMedal()).isEqualTo("EcoBronze");
        assertThat(response.getTotalCalorieBurntExp()).isEqualTo(5500);
        assertThat(response.getCalorieBurntMedal()).isEqualTo("CalorieSilver");
    }

    @Test
    @DisplayName("getAchievementProgress should throw RuntimeException when user is not found")
    void getAchievementProgress_whenUserNotFound_shouldThrowException() {
        // Arrange
        String userId = "non-existent-user";
        when(achievementRepository.fetchAchievementForUser(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            achievementService.getAchievementProgress(userId);
        });

        // Verify the exception message is correct
        assertThat(exception.getMessage()).isEqualTo("Achievement not found for user: " + userId);
    }
}