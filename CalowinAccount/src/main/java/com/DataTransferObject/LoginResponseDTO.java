package com.DataTransferObject;

public class LoginResponseDTO {
    
    // UserEntity
    private String userID;
    private String email;
    private String name;
    private float weight;
    private String bio;

    //AchievementEntry
    private int totalCarbonSaved;
    private int totalCalorieBurnt;
    private String carbonMedal;
    private String calorieMedal;


    public LoginResponseDTO(String userID, String email, String name, float weight, String bio, int totalCarbonSaved, int totalCalorieBurnt, String carbonMedal, String calorieMedal) {
        this.userID = userID;
        this.email = email;
        this.name = name;
        this.weight = weight;
        this.bio = bio;
        
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

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getWeight() {
        return this.weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getBio() {
        return this.bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
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
