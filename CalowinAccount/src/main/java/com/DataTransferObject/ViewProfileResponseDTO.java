package com.DataTransferObject;

import com.Account.Entities.FriendStatus;

public class ViewProfileResponseDTO {

    // UserEntity
    private String userID;
    private String name;
    private String bio;
    private FriendStatus friendStatus;

    // AchievementEntry
    private int totalCarbonSaved;
    private int totalCalorieBurnt;
    private String carbonMedal;
    private String calorieMedal;

    public ViewProfileResponseDTO(String userID, String name, String bio, FriendStatus friendStatus,
            int totalCarbonSaved, int totalCalorieBurnt, String carbonMedal, String calorieMedal) {
        this.userID = userID;
        this.name = name;
        this.bio = bio;
        this.friendStatus = friendStatus;

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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return this.bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public FriendStatus getFriendStatus() {
        return this.friendStatus;
    }

    public void setFriendStatus(FriendStatus friendStatus) {
        this.friendStatus = friendStatus;
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
