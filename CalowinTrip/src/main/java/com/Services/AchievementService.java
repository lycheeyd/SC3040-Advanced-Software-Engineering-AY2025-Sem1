package com.Services;

import com.DataTransferObject.AchievementResponse;
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
        AchievementEntity userAchievement = achievementRepository.fetchAchievementForUser(userId)
                .orElse(new AchievementEntity()); // Get existing or create new

        userAchievement.addTripExperience(carbonSaved, caloriesBurnt);
        achievementRepository.updateAchievement(userAchievement, userId);
    }

    public AchievementResponse getAchievementProgress(String userId) {
        AchievementEntity userAchievement = achievementRepository.fetchAchievementForUser(userId)
                .orElseThrow(() -> new RuntimeException("Achievement not found for user: " + userId));

        return new AchievementResponse(
                userAchievement.getTotalCarbonSavedExp(),
                userAchievement.getTotalCalorieBurntExp(),
                userAchievement.getCarbonSavedMedal(),
                userAchievement.getCalorieBurntMedal()
        );
    }
}