package com.dto;

import java.util.Map;

public class ParkResponseDTO {
    private String name;
    private double distance;
    private Map<String, Double> closestPoint;

    public ParkResponseDTO(String name, double distance, Map<String, Double> closestPoint) {
        this.name = name;
        this.distance = distance;
        this.closestPoint = closestPoint;
    }

    // Getters
    public String getName() {
        return name;
    }

    public double getDistance() {
        return distance;
    }

    public Map<String, Double> getClosestPoint() {
        return closestPoint;
    }
}