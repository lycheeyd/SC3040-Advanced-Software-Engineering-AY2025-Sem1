package com.RelationshipNotification.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.persistence.Column;

@Entity
@Table(name = "FriendRelationship") // Map to your database table
public class FriendRelationshipEntity {

    @Id
    @Column(name = "Unique_ID", length = 8, nullable = false, unique = true)
    private String uniqueID;

    @Column(name = "Friend_Unique_ID", length = 8, nullable = false, unique = true)
    private String friendUniqueID;

    @Column(name = "Friended On", nullable = false)
    private String friendedOn;

    @Column(name = "status", length = 20)
    private String status;

    public FriendRelationshipEntity() {
    }
    // Add other fields and their respective getters and setters

    public String getUniqueID() {
        return this.uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getFriendUniqueID() {
        return this.friendUniqueID;
    }

    public void setFriendUniqueID(String friendUniqueID) {
        this.friendUniqueID = friendUniqueID;
    }

    public String getFriendedOn() {
        return this.friendedOn;
    }

    public void setFriendedOn(String friendedOn) {
        this.friendedOn = friendedOn;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Add other getters and setters
}
