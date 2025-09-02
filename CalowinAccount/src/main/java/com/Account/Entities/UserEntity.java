package com.Account.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "SecureUserInfo")

public class UserEntity {

    @Id
    @Column(name = "user_id", length = 8, nullable = false, unique = true)
    private String userID;

    @Column(name = "email_address", nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    // Default constructor is required by JPA
    public UserEntity() {
    }

    public UserEntity(String userID, String email, String password) {
        this.userID = userID;
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
