package com.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "UserInfo", schema = "dbo")
public class UserInfo {

    @Id
    @Column(name = "user_id", columnDefinition = "nchar(8)")
    private String userId;

    @Column(name = "name", length = 16)
    private String name;

    @Column(name = "weight")
    private float weight;

    @Column(name = "bio", length = 250)
    private String bio;

    // Getters and Setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
