package com.Account.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "UserInfo")

public class ProfileEntity {

    @Id
    @Column(name = "user_id", length = 8, nullable = false, unique = true)
    private String userID;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "weight", nullable = false)
    private float weight;

    @Column(name = "bio", nullable = true)
    private String bio;

    // Default constructor is required by JPA
    public ProfileEntity() {
    }

    public ProfileEntity(String userID, String name, float weight, String bio) {
        this.userID = userID;
        this.name = name;
        this.weight = weight;
        this.bio = bio;
    }

    public void updateProfile(String name, float weight, String bio) {
        this.name = name;
        this.weight = weight;
        this.bio = bio;
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

}
