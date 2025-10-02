package com.Services;

import com.Entity.TravelMethod;
import com.DataTransferObject.TripMetricsRequest;
import com.DataTransferObject.TripStartRequest;
import com.Entity.*;
import com.repository.TripRepository;
import com.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.*;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final AchievementService achievementService;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int TRIP_ID_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    public TripService(TripRepository tripRepository, UserRepository userRepository, AchievementService achievementService) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.achievementService = achievementService;
    }

    public Trip startTrip(TripStartRequest request) {
        double distance = calculateDistance(request.getCurrentLocation(), request.getDestination());
        double weight = userRepository.getUserWeight(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User weight not found for userId: " + request.getUserId()));

        int caloriesBurned = calculateCalories(request.getTravelMethod(), distance, weight);
        int carbonSaved = calculateCarbon(request.getTravelMethod(), distance);

        Trip trip = new Trip(
                generateUniqueTripId(),
                request.getCurrentLocation(),
                request.getDestination(),
                request.getTravelMethod(),
                request.getUserId()
        );
        trip.setCaloriesBurnt(caloriesBurned);
        trip.setCarbonSaved(carbonSaved);
        trip.setDistance(distance);

        tripRepository.insertTripIntoDatabase(trip);

        // Update achievements after saving the trip
        achievementService.addTripMetricsToAchievement(request.getUserId(), carbonSaved, caloriesBurned);

        return trip;
    }

    public Map<String, Object> calculateTripMetrics(TripMetricsRequest request) {
        double distance = calculateDistance(request.getCurrentLocation(), request.getDestination());
        double weight = userRepository.getUserWeight(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User weight not found for userId: " + request.getUserId()));

        int caloriesBurned = calculateCalories(request.getTravelMethod(), distance, weight);
        int carbonSaved = calculateCarbon(request.getTravelMethod(), distance);

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("caloriesBurnt", caloriesBurned);
        metrics.put("carbonSaved", carbonSaved);
        metrics.put("distance", distance);

        return metrics;
    }

    // --- Private Helper Methods ---

    private double calculateDistance(CurrentLocationEntity userLocation, LocationEntity destination) {
        double earthRadius = 6371; // Kilometers
        double dLat = toRadians(destination.getLatitude() - userLocation.getLatitude());
        double dLon = toRadians(destination.getLongitude() - userLocation.getLongitude());
        double a = sin(dLat / 2) * sin(dLat / 2) +
                cos(toRadians(userLocation.getLatitude())) * cos(toRadians(destination.getLatitude())) *
                        sin(dLon / 2) * sin(dLon / 2);
        double c = 2 * atan2(sqrt(a), sqrt(1 - a));
        return earthRadius * c;
    }

    private int calculateCalories(TravelMethod method, double distance, double weight) {
        switch (method) {
            case WALK: return (int) (distance * weight * 0.5);
            case CYCLE: return (int) (distance * weight * 0.3);
            case PUBLIC_TRANSPORT: return (int) (distance * weight * 0.1);
            default: return 0;
        }
    }

    private int calculateCarbon(TravelMethod method, double distance) {
        switch (method) {
            case WALK: return (int) (distance * 30);
            case CYCLE: return (int) (distance * 20);
            case PUBLIC_TRANSPORT: return (int) (distance * 10);
            default: return 0;
        }
    }

    private String generateUniqueTripId() {
        String tripId;
        do {
            tripId = generateRandomString();
        } while (tripRepository.tripIdExists(tripId));
        return tripId;
    }

    private String generateRandomString() {
        StringBuilder sb = new StringBuilder(TRIP_ID_LENGTH);
        for (int i = 0; i < TRIP_ID_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}