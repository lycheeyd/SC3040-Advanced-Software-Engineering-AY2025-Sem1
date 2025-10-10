package com.DataTransferObject.TripDTO;

public class Achievement {

    private int totalCarbonSavedExp;
    private int totalCalorieBurntExp;
    private String carbonSavedMedal;
    private String calorieBurntMedal;
    private int pointsToNextCarbonBronze;
    private int pointsToNextCarbonSilver;
    private int pointsToNextCarbonGold;
    private int pointsToNextCarbonPlatinum;
    private int pointsToNextCalorieBronze;
    private int pointsToNextCalorieSilver;
    private int pointsToNextCalorieGold;
    private int pointsToNextCaloriePlatinum;

    // Constructor
    public Achievement(int totalCarbonSavedExp, int totalCalorieBurntExp,
            String carbonSavedMedal, String calorieBurntMedal,
            int pointsToNextCarbonBronze, int pointsToNextCarbonSilver,
            int pointsToNextCarbonGold, int pointsToNextCarbonPlatinum,
            int pointsToNextCalorieBronze, int pointsToNextCalorieSilver,
            int pointsToNextCalorieGold, int pointsToNextCaloriePlatinum) {
        this.totalCarbonSavedExp = totalCarbonSavedExp;
        this.totalCalorieBurntExp = totalCalorieBurntExp;
        this.carbonSavedMedal = carbonSavedMedal;
        this.calorieBurntMedal = calorieBurntMedal;
        this.pointsToNextCarbonBronze = pointsToNextCarbonBronze;
        this.pointsToNextCarbonSilver = pointsToNextCarbonSilver;
        this.pointsToNextCarbonGold = pointsToNextCarbonGold;
        this.pointsToNextCarbonPlatinum = pointsToNextCarbonPlatinum;
        this.pointsToNextCalorieBronze = pointsToNextCalorieBronze;
        this.pointsToNextCalorieSilver = pointsToNextCalorieSilver;
        this.pointsToNextCalorieGold = pointsToNextCalorieGold;
        this.pointsToNextCaloriePlatinum = pointsToNextCaloriePlatinum;
    }

    // Getters and setters
    public int getTotalCarbonSavedExp() {
        return totalCarbonSavedExp;
    }

    public void setTotalCarbonSavedExp(int totalCarbonSavedExp) {
        this.totalCarbonSavedExp = totalCarbonSavedExp;
    }

    public int getTotalCalorieBurntExp() {
        return totalCalorieBurntExp;
    }

    public void setTotalCalorieBurntExp(int totalCalorieBurntExp) {
        this.totalCalorieBurntExp = totalCalorieBurntExp;
    }

    public String getCarbonSavedMedal() {
        return carbonSavedMedal;
    }

    public void setCarbonSavedMedal(String carbonSavedMedal) {
        this.carbonSavedMedal = carbonSavedMedal;
    }

    public String getCalorieBurntMedal() {
        return calorieBurntMedal;
    }

    public void setCalorieBurntMedal(String calorieBurntMedal) {
        this.calorieBurntMedal = calorieBurntMedal;
    }

    public int getPointsToNextCarbonBronze() {
        return pointsToNextCarbonBronze;
    }

    public void setPointsToNextCarbonBronze(int pointsToNextCarbonBronze) {
        this.pointsToNextCarbonBronze = pointsToNextCarbonBronze;
    }

    public int getPointsToNextCarbonSilver() {
        return pointsToNextCarbonSilver;
    }

    public void setPointsToNextCarbonSilver(int pointsToNextCarbonSilver) {
        this.pointsToNextCarbonSilver = pointsToNextCarbonSilver;
    }

    public int getPointsToNextCarbonGold() {
        return pointsToNextCarbonGold;
    }

    public void setPointsToNextCarbonGold(int pointsToNextCarbonGold) {
        this.pointsToNextCarbonGold = pointsToNextCarbonGold;
    }

    public int getPointsToNextCarbonPlatinum() {
        return pointsToNextCarbonPlatinum;
    }

    public void setPointsToNextCarbonPlatinum(int pointsToNextCarbonPlatinum) {
        this.pointsToNextCarbonPlatinum = pointsToNextCarbonPlatinum;
    }

    public int getPointsToNextCalorieBronze() {
        return pointsToNextCalorieBronze;
    }

    public void setPointsToNextCalorieBronze(int pointsToNextCalorieBronze) {
        this.pointsToNextCalorieBronze = pointsToNextCalorieBronze;
    }

    public int getPointsToNextCalorieSilver() {
        return pointsToNextCalorieSilver;
    }

    public void setPointsToNextCalorieSilver(int pointsToNextCalorieSilver) {
        this.pointsToNextCalorieSilver = pointsToNextCalorieSilver;
    }

    public int getPointsToNextCalorieGold() {
        return pointsToNextCalorieGold;
    }

    public void setPointsToNextCalorieGold(int pointsToNextCalorieGold) {
        this.pointsToNextCalorieGold = pointsToNextCalorieGold;
    }

    public int getPointsToNextCaloriePlatinum() {
        return pointsToNextCaloriePlatinum;
    }

    public void setPointsToNextCaloriePlatinum(int pointsToNextCaloriePlatinum) {
        this.pointsToNextCaloriePlatinum = pointsToNextCaloriePlatinum;
    }
}
