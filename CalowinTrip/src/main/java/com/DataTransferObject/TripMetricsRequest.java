package com.DataTransferObject;

import com.Entity.CurrentLocationEntity;
import com.Entity.LocationEntity;
import com.Entity.TravelMethod;

public class TripMetricsRequest {
    private TravelMethod travelMethod;
    private LocationEntity destination;
    private CurrentLocationEntity currentLocation;
    private String userId;

    // Getters and Setters
    public TravelMethod getTravelMethod() { return travelMethod; }
    public void setTravelMethod(TravelMethod travelMethod) { this.travelMethod = travelMethod; }
    public LocationEntity getDestination() { return destination; }
    public void setDestination(LocationEntity destination) { this.destination = destination; }
    public CurrentLocationEntity getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(CurrentLocationEntity currentLocation) { this.currentLocation = currentLocation; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}