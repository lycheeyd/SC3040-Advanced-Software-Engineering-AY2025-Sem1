package com.DataTransferObject;

public class AchievementResponseDTO {
    private int totalCarbonSavedExp;
    private int totalCalorieBurntExp;
    private String carbonSavedMedal;
    private String calorieBurntMedal;

    public AchievementResponseDTO(int totalCarbonSavedExp, int totalCalorieBurntExp, String carbonSavedMedal,
            String calorieBurntMedal) {
        this.totalCarbonSavedExp = totalCarbonSavedExp;
        this.totalCalorieBurntExp = totalCalorieBurntExp;
        this.carbonSavedMedal = carbonSavedMedal;
        this.calorieBurntMedal = calorieBurntMedal;
    }

    // Getters
    public int getTotalCarbonSavedExp() {
        return totalCarbonSavedExp;
    }

    public int getTotalCalorieBurntExp() {
        return totalCalorieBurntExp;
    }

    public String getCarbonSavedMedal() {
        return carbonSavedMedal;
    }

    public String getCalorieBurntMedal() {
        return calorieBurntMedal;
    }
}