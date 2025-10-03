package com.DataTransferObject;

import com.Entity.TravelMethod;
import com.Entity.CurrentLocationEntity;
import com.Entity.LocationEntity;

public class TripStartRequestDTO {
    private CurrentLocationEntity currentLocation;
    private LocationEntity destination;
    private TravelMethod travelMethod;
    private String userId;

    // Getters and setters
    public CurrentLocationEntity getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(CurrentLocationEntity currentLocation) { this.currentLocation = currentLocation; }
    public LocationEntity getDestination() { return destination; }
    public void setDestination(LocationEntity destination) { this.destination = destination; }
    public TravelMethod getTravelMethod() { return travelMethod; }
    public void setTravelMethod(TravelMethod travelMethod) { this.travelMethod = travelMethod; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}