package com.DataTransferObject;

import com.ENUM.TravelMethod;
import com.model.CurrentLocation;
import com.model.Location;

public class TripStartRequestDTO {
    private CurrentLocation currentLocation;
    private Location destination;
    private TravelMethod travelMethod;
    private String userId;

    // Getters and setters
    public CurrentLocation getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(CurrentLocation currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public TravelMethod getTravelMethod() {
        return travelMethod;
    }

    public void setTravelMethod(TravelMethod travelMethod) {
        this.travelMethod = travelMethod;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}