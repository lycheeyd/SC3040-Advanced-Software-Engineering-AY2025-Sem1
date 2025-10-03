package com.model;

import com.ENUM.TravelMethod;

public class Trip {
    private String tripId;
    private CurrentLocation currentLocation;
    private Location destination;
    private double distance;
    private int caloriesBurnt;
    private int carbonSaved;
    private TravelMethod travelMethod;
    private String userId;

    public Trip(String tripId, CurrentLocation currentLocation, Location destination, TravelMethod travelMethod, String userId) {
        this.tripId = tripId;
        this.currentLocation = currentLocation;
        this.destination = destination;
        this.travelMethod = travelMethod;
        this.userId = userId;
    }

    // Getters and setters
    public String getTripId() { return tripId; }
    public void setTripId(String tripId) { this.tripId = tripId; }

    public CurrentLocation getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(CurrentLocation currentLocation) { this.currentLocation = currentLocation; }

    public Location getDestination() { return destination; }
    public void setDestination(Location destination) { this.destination = destination; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public int getCaloriesBurnt() { return caloriesBurnt; }
    public void setCaloriesBurnt(int caloriesBurnt) { this.caloriesBurnt = caloriesBurnt; }

    public int getCarbonSaved() { return carbonSaved; }
    public void setCarbonSaved(int carbonSaved) { this.carbonSaved = carbonSaved; }

    public TravelMethod getTravelMethod() { return travelMethod; }
    public void setTravelMethod(TravelMethod travelMethod) { this.travelMethod = travelMethod; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}


