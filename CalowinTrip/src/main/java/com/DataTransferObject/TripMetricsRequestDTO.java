package com.DataTransferObject;

import com.model.CurrentLocation;
import com.model.Location;
import com.ENUM.TravelMethod;

public class TripMetricsRequestDTO {
    private TravelMethod travelMethod;
    private Location destination;
    private CurrentLocation currentLocation;
    private String userId;

    // Getters and Setters
    public TravelMethod getTravelMethod() {
        return travelMethod;
    }

    public void setTravelMethod(TravelMethod travelMethod) {
        this.travelMethod = travelMethod;
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public CurrentLocation getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(CurrentLocation currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}