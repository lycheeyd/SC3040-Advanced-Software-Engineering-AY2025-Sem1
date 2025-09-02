package com.Account.Entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "FriendRelationship")

public class FriendRelationshipEntry {

    @Id
    @Column(name = "Unique_ID", nullable = false)
    private String userID;

    @Column(name = "Friend_Unique_ID", nullable = false)
    private String friendID;

    @Column(name = "[Friended On]", nullable = false)
    private LocalDateTime friendedOn;

    @Column(name = "status")
    private String status;

    // Default constructor is required by JPA
    public FriendRelationshipEntry() {
    }

    public FriendRelationshipEntry(String userID, String friendID, LocalDateTime friendedOn, String status) {
        this.userID = userID;
        this.friendID = friendID;
        this.friendedOn = friendedOn;
        this.status = status;
    }

    public String getUserID() {
        return this.userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFriendID() {
        return this.friendID;
    }

    public void setFriendID(String friendID) {
        this.friendID = friendID;
    }

    public LocalDateTime getFriendedOn() {
        return this.friendedOn;
    }

    public void setFriendedOn(LocalDateTime friendedOn) {
        this.friendedOn = friendedOn;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
