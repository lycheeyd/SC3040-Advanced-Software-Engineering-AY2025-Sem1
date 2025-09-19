package com.dto;

public class AchievementDTO {
    private String userId;
    private String userName;
    private int totalCarbonSaved;
    private int totalCalorieBurnt;
    private String carbonMedal;
    private String calorieMedal;

    // Default Constructor
    public AchievementDTO() {}

    // Parameterized Constructor
    public AchievementDTO(String userId, String userName, int totalCarbonSaved, int totalCalorieBurnt, String carbonMedal, String calorieMedal) {
        this.userId = userId;
        this.userName = userName;
        this.totalCarbonSaved = totalCarbonSaved;
        this.totalCalorieBurnt = totalCalorieBurnt;
        this.carbonMedal = carbonMedal;
        this.calorieMedal = calorieMedal;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getTotalCarbonSaved() {
        return totalCarbonSaved;
    }

    public void setTotalCarbonSaved(int totalCarbonSaved) {
        this.totalCarbonSaved = totalCarbonSaved;
    }

    public int getTotalCalorieBurnt() {
        return totalCalorieBurnt;
    }

    public void setTotalCalorieBurnt(int totalCalorieBurnt) {
        this.totalCalorieBurnt = totalCalorieBurnt;
    }

    public String getCarbonMedal() {
        return carbonMedal;
    }

    public void setCarbonMedal(String carbonMedal) {
        this.carbonMedal = carbonMedal;
    }

    public String getCalorieMedal() {
        return calorieMedal;
    }

    public void setCalorieMedal(String calorieMedal) {
        this.calorieMedal = calorieMedal;
    }
}
