package com.Account.Entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Trips")

public class TripsEntry {

    @Id
    @Column(name = "trip_id", nullable = false, unique = true)
    private String tripID;

    @Column(name = "start_location", nullable = false)
    private String startLocation;

    @Column(name = "end_location", nullable = false)
    private String endLocation;

    @Column(name = "start_longitude", nullable = false)
    private BigDecimal startLongitude;

    @Column(name = "start_latitude", nullable = false)
    private BigDecimal startLatitude;

    @Column(name = "end_longitude", nullable = false)
    private BigDecimal endLongitude;

    @Column(name = "end_latitude", nullable = false)
    private BigDecimal endLatitude;

    @Column(name = "distance", nullable = false)
    private BigDecimal distance;

    @Column(name = "calories_burnt", nullable = false)
    private int caloriesBurnt;

    @Column(name = "carbon_saved", nullable = false)
    private int carbonSaved;

    @Column(name = "trip_time", nullable = false)
    private LocalDateTime tripTime;

    @Column(name = "travel_method", nullable = false)
    private String travelMethod;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "user_id", length = 8, nullable = false, unique = true)
    private String userID;

    // Default constructor is required by JPA
    public TripsEntry() {
    }

    public TripsEntry(String tripID, String startLocation, String endLocation, BigDecimal startLongitude,
            BigDecimal startLatitude, BigDecimal endLongitude, BigDecimal endLatitude, BigDecimal distance,
            int caloriesBurnt, int carbonSaved, LocalDateTime tripTime, String travelMethod, String status,
            String userID) {
        this.tripID = tripID;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.startLongitude = startLongitude;
        this.startLatitude = startLatitude;
        this.endLongitude = endLongitude;
        this.endLatitude = endLatitude;
        this.distance = distance;
        this.caloriesBurnt = caloriesBurnt;
        this.carbonSaved = carbonSaved;
        this.tripTime = tripTime;
        this.travelMethod = travelMethod;
        this.status = status;
        this.userID = userID;
    }

    public String getTripID() {
        return this.tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }

    public String getStartLocation() {
        return this.startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return this.endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public BigDecimal getStartLongitude() {
        return this.startLongitude;
    }

    public void setStartLongitude(BigDecimal startLongitude) {
        this.startLongitude = startLongitude;
    }

    public BigDecimal getStartLatitude() {
        return this.startLatitude;
    }

    public void setStartLatitude(BigDecimal startLatitude) {
        this.startLatitude = startLatitude;
    }

    public BigDecimal getEndLongitude() {
        return this.endLongitude;
    }

    public void setEndLongitude(BigDecimal endLongitude) {
        this.endLongitude = endLongitude;
    }

    public BigDecimal getEndLatitude() {
        return this.endLatitude;
    }

    public void setEndLatitude(BigDecimal endLatitude) {
        this.endLatitude = endLatitude;
    }

    public BigDecimal getDistance() {
        return this.distance;
    }

    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }

    public int getCaloriesBurnt() {
        return this.caloriesBurnt;
    }

    public void setCaloriesBurnt(int caloriesBurnt) {
        this.caloriesBurnt = caloriesBurnt;
    }

    public int getCarbonSaved() {
        return this.carbonSaved;
    }

    public void setCarbonSaved(int carbonSaved) {
        this.carbonSaved = carbonSaved;
    }

    public LocalDateTime getTripTime() {
        return this.tripTime;
    }

    public void setTripTime(LocalDateTime tripTime) {
        this.tripTime = tripTime;
    }

    public String getTravelMethod() {
        return this.travelMethod;
    }

    public void setTravelMethod(String travelMethod) {
        this.travelMethod = travelMethod;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserID() {
        return this.userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

}
