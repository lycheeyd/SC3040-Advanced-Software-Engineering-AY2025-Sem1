package com.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "FriendRelationship")
public class FriendRelationship {
    @EmbeddedId
    private FriendRelationshipId id;

    @Column(name = "status")
    private String status;

    @Column(name = "[Friended On]")
    private LocalDateTime friendedOn;

    // Getters and Setters
    public FriendRelationshipId getId() {
        return id;
    }

    public void setId(FriendRelationshipId id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getFriendedOn() {
        return friendedOn;
    }

    public void setFriendedOn(LocalDateTime friendedOn) {
        this.friendedOn = friendedOn;
    }
}
