package com.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Achievement", schema = "dbo")
public class AchievementEntity {

    @Id
    @Column(name = "user_id", columnDefinition = "nchar(255)")
    private String userId;

    @Column(name = "total_carbon_saved")
    private int totalCarbonSaved;

    @Column(name = "total_calorie_burnt")
    private int totalCalorieBurnt;

    @Column(name = "carbon_medal")
    private String carbonMedal;

    @Column(name = "calorie_medal")
    private String calorieMedal;
    
    // Getters and Setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
