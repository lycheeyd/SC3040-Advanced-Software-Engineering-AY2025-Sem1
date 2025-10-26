package com.Services;


import com.model.Achievement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class AchievementTest {

    @Test
    @DisplayName("Default constructor should set default values")
    void constructor_shouldSetDefaults() {
        Achievement achievement = new Achievement();
        assertThat(achievement.getTotalCarbonSavedExp()).isZero();
        assertThat(achievement.getTotalCalorieBurntExp()).isZero();
        assertThat(achievement.getCarbonSavedMedal()).isEqualTo("No Medal");
        assertThat(achievement.getCalorieBurntMedal()).isEqualTo("No Medal");
    }

    @Test
    @DisplayName("addTripExperience should sum up experience points")
    void addTripExperience_shouldSumPoints() {
        Achievement achievement = new Achievement();
        achievement.addTripExperience(100, 200);
        achievement.addTripExperience(50, 75);

        assertThat(achievement.getTotalCarbonSavedExp()).isEqualTo(150);
        assertThat(achievement.getTotalCalorieBurntExp()).isEqualTo(275);
    }

    @ParameterizedTest
    @DisplayName("updateMedalStatus should set correct carbon medal based on exp")
    @CsvSource({
            "500, 'No Medal'",
            "1000, 'EcoBronze'",
            "4999, 'EcoBronze'",
            "5000, 'EcoSilver'",
            "9999, 'EcoSilver'",
            "10000, 'EcoGold'",
            "14999, 'EcoGold'",
            "15000, 'EcoPlatinum'",
            "20000, 'EcoPlatinum'"
    })
    void updateMedalStatus_shouldSetCorrectCarbonMedal(int exp, String medal) {
        Achievement achievement = new Achievement();
        achievement.addTripExperience(exp, 0);
        assertThat(achievement.getCarbonSavedMedal()).isEqualTo(medal);
    }

    @ParameterizedTest
    @DisplayName("updateMedalStatus should set correct calorie medal based on exp")
    @CsvSource({
            "999, 'No Medal'",
            "1000, 'CalorieBronze'",
            "5000, 'CalorieSilver'",
            "10000, 'CalorieGold'",
            "15000, 'CaloriePlatinum'"
    })
    void updateMedalStatus_shouldSetCorrectCalorieMedal(int exp, String medal) {
        Achievement achievement = new Achievement();
        achievement.addTripExperience(0, exp);
        assertThat(achievement.getCalorieBurntMedal()).isEqualTo(medal);
    }

    @Test
    @DisplayName("pointsToNextCarbonBronze should return correct remaining points")
    void pointsToNextCarbonBronze_shouldReturnRemaining() {
        Achievement achievement = new Achievement();
        achievement.addTripExperience(700, 0);

        // 1000 (BRONZE_THRESHOLD) - 700 = 300
        assertThat(achievement.pointsToNextCarbonBronze()).isEqualTo(300);
    }

    @Test
    @DisplayName("pointsToNextCarbonBronze should return 0 if threshold is met")
    void pointsToNextCarbonBronze_shouldReturnZeroWhenMet() {
        Achievement achievement = new Achievement();
        achievement.addTripExperience(1200, 0);

        assertThat(achievement.pointsToNextCarbonBronze()).isZero();
    }

    // ... similar tests for pointsToNext... Silver, Gold, Platinum for both types
}