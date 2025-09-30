package com.Services;

import com.DataTransferObject.AchievementResponseDTO;
import com.Entity.AchievementEntity;
import com.repository.AchievementRepository;
import org.springframework.stereotype.Service;

@Service
public class AchievementService {

    private final AchievementRepository achievementRepository;

    public AchievementService(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    public void addTripMetricsToAchievement(String userId, int carbonSaved, int caloriesBurnt) {
        AchievementEntity userAchievementEntity = achievementRepository.fetchAchievementForUser(userId)
                .orElse(new AchievementEntity()); // Get existing or create new

        userAchievementEntity.addTripExperience(carbonSaved, caloriesBurnt);
        achievementRepository.updateAchievement(userAchievementEntity, userId);
    }

    public AchievementResponseDTO getAchievementProgress(String userId) {
        AchievementEntity userAchievementEntity = achievementRepository.fetchAchievementForUser(userId)
                .orElseThrow(() -> new RuntimeException("Achievement not found for user: " + userId));

        return new AchievementResponseDTO(
                userAchievementEntity.getTotalCarbonSavedExp(),
                userAchievementEntity.getTotalCalorieBurntExp(),
                userAchievementEntity.getCarbonSavedMedal(),
                userAchievementEntity.getCalorieBurntMedal()
        );
    }
}