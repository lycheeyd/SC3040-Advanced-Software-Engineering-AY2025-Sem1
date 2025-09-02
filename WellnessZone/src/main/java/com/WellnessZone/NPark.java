package com.WellnessZone;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class NPark {
    private double distance;
    private List<Map<String, Double>> coordinates = new ArrayList<>();
    private Map<String, Double> closestPoint = new HashMap<>();
    private Map<String, Double> userCoordinate = new HashMap<>();
    private String name;

    public NPark(List<Map<String, Double>> coordinates, Map<String, Double> userCoordinate, String name) {
        this.coordinates = coordinates;
        this.userCoordinate = userCoordinate;
        this.name = name;
        setClosestPoint(this.coordinates, this.userCoordinate);
        setDistance(this.closestPoint, this.userCoordinate);
    }

    // Find the closest coordinate to the user
    public void setClosestPoint(List<Map<String, Double>> coordinates, Map<String, Double> userCoordinate) {
        double minDistance = Double.MAX_VALUE;
        Map<String, Double> nearestCoordinate = null;

        // Iterate over all coordinates and find the closest one
        for (Map<String, Double> coordinate : coordinates) {
            double dist = calculateDistance(coordinate, userCoordinate);

            if (dist < minDistance) {
                minDistance = dist;
                nearestCoordinate = coordinate;
            }
        }

        // Set the closest point and distance
        if (nearestCoordinate != null) {
            this.closestPoint = nearestCoordinate;
            setDistance(nearestCoordinate, userCoordinate);
        }
    }

    // Calculate the distance between two coordinates
    public void setDistance(Map<String, Double> coordinate, Map<String, Double> userCoordinate) {
        if (userCoordinate == null || !userCoordinate.containsKey("Lat") || !userCoordinate.containsKey("Lon")) {
            System.out.println("User coordinates are missing.");
            return;
        }

        if (coordinates == null || coordinates.isEmpty()) {
            System.out.println("Park coordinates are missing.");
            return;
        }

        this.distance = calculateDistance(coordinate, userCoordinate);
    }

    // Helper method to calculate distance using the Haversine formula
    private double calculateDistance(Map<String, Double> coord1, Map<String, Double> coord2) {
        if (coord1.isEmpty() || coord2.isEmpty()) {
            throw new IllegalArgumentException("Invalid user coordinates.");
        }

        final double R = 6371; // Radius of the Earth in kilometers

        double lat1 = coord1.get("Lat");
        double lon1 = coord1.get("Lon");
        double lat2 = coord2.get("Lat");
        double lon2 = coord2.get("Lon");

        // Convert degrees to radians
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // Distance in kilometers
    }

    public double getDistance() {
        return distance;
    }

    public Map<String, Double> getClosestPoint() {
        return closestPoint;
    }

    public String getName() {
        return name;
    }
}
