package com.dto;

public class FriendRelationshipDTO {
    private String userId;
    private String userName;
    private String friendUserId;
    private String friendUserName;
    private String status;

    // Constructors, Getters, and Setters

    public FriendRelationshipDTO() {
    }

    public FriendRelationshipDTO(String userId, String userName, String friendUserId, String friendUserName, String status) {
        this.userId = userId;
        this.userName = userName;
        this.friendUserId = friendUserId;
        this.friendUserName = friendUserName;
        this.status = status;
    }

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

    public String getFriendUserId() {
        return friendUserId;
    }

    public void setFriendUserId(String friendUserId) {
        this.friendUserId = friendUserId;
    }

    public String getFriendUserName() {
        return friendUserName;
    }

    public void setFriendUserName(String friendUserName) {
        this.friendUserName = friendUserName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
