package com.Account.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Achievement")

public class AchievementEntry {

    @Id
    @Column(name = "user_id", length = 8, nullable = false, unique = true)
    private String userID;

    @Column(name = "total_carbon_saved", nullable = false)
    private int totalCarbonSaved;

    @Column(name = "total_calorie_burnt", nullable = false)
    private int totalCalorieBurnt;

    @Column(name = "carbon_medal", nullable = false)
    private String carbonMedal;

    @Column(name = "calorie_medal", nullable = false)
    private String calorieMedal;

    // Default constructor is required by JPA
    public AchievementEntry() {
    }

    public AchievementEntry(String userID, int totalCarbonSaved, int totalCalorieBurnt, String carbonMedal, String calorieMedal) {
        this.userID = userID;
        this.totalCarbonSaved = totalCarbonSaved;
        this.totalCalorieBurnt = totalCalorieBurnt;
        this.carbonMedal = carbonMedal;
        this.calorieMedal = calorieMedal;
    }

    public String getUserID() {
        return this.userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getTotalCarbonSaved() {
        return this.totalCarbonSaved;
    }

    public void setTotalCarbonSaved(int totalCarbonSaved) {
        this.totalCarbonSaved = totalCarbonSaved;
    }

    public int getTotalCalorieBurnt() {
        return this.totalCalorieBurnt;
    }

    public void setTotalCalorieBurnt(int totalCalorieBurnt) {
        this.totalCalorieBurnt = totalCalorieBurnt;
    }

    public String getCarbonMedal() {
        return this.carbonMedal;
    }

    public void setCarbonMedal(String carbonMedal) {
        this.carbonMedal = carbonMedal;
    }

    public String getCalorieMedal() {
        return this.calorieMedal;
    }

    public void setCalorieMedal(String calorieMedal) {
        this.calorieMedal = calorieMedal;
    }

}