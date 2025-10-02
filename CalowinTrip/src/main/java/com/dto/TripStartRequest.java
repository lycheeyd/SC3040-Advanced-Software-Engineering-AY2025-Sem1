package com.dto;

import com.ENUM.TravelMethod;
import com.models.CurrentLocation;
import com.models.Location;

public class TripStartRequest {
    private CurrentLocation currentLocation;
    private Location destination;
    private TravelMethod travelMethod;
    private String userId;

    // Getters and setters
    public CurrentLocation getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(CurrentLocation currentLocation) { this.currentLocation = currentLocation; }
    public Location getDestination() { return destination; }
    public void setDestination(Location destination) { this.destination = destination; }
    public TravelMethod getTravelMethod() { return travelMethod; }
    public void setTravelMethod(TravelMethod travelMethod) { this.travelMethod = travelMethod; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}