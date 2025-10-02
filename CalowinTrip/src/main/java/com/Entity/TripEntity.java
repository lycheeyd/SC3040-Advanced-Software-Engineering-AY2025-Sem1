package com.Entity;

public class TripEntity {
    private String tripId;
    private CurrentLocationEntity currentLocationEntity;
    private LocationEntity destination;
    private double distance;
    private int caloriesBurnt;
    private int carbonSaved;
    private TravelMethod travelMethod;
    private String userId;

    public TripEntity(String tripId, CurrentLocationEntity currentLocationEntity, LocationEntity destination, TravelMethod travelMethod, String userId) {
        this.tripId = tripId;
        this.currentLocationEntity = currentLocationEntity;
        this.destination = destination;
        this.travelMethod = travelMethod;
        this.userId = userId;
    }

    // Getters and setters
    public String getTripId() { return tripId; }
    public void setTripId(String tripId) { this.tripId = tripId; }

    public CurrentLocationEntity getCurrentLocation() { return currentLocationEntity; }
    public void setCurrentLocation(CurrentLocationEntity currentLocationEntity) { this.currentLocationEntity = currentLocationEntity; }

    public LocationEntity getDestination() { return destination; }
    public void setDestination(LocationEntity destination) { this.destination = destination; }

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


