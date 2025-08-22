package com.example.CalowinTrip;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trips")
public class TripController {

    // private final AchievementController achievementController = new AchievementController();


    public static Timestamp getCurrentSqlTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        return Timestamp.valueOf(now); // Convert LocalDateTime to Timestamp
    }

    @GetMapping("/methods")
    public List<TravelMethod> getTravelMethods() {
        return Arrays.asList(TravelMethod.values());
    }

    @PostMapping("/start")
    public Trip startTrip(@RequestBody Trip trip, String user_Id) {
        double distance = calculateDistance(trip.getCurrentLocation(), trip.getDestination());


        user_Id = trip.getUserId();
            // Retrieve user weight from the database
        double weight = getUserWeight(user_Id);


        int caloriesBurned = calculateCalories(trip.getTravelMethod(), distance, weight);
        int carbonSaved = calculateCarbon(trip.getTravelMethod(), distance);

        trip.setCaloriesBurnt(caloriesBurned);
        trip.setCarbonSaved(carbonSaved);
        trip.setDistance(distance);

        insertTripIntoDatabase(trip);

        return trip;
    }

    @PostMapping("/retrieve-metrics")
    public Map<String, Object> retrieveMetrics(@RequestBody Trip trip) {
        // Extract the necessary information from the Trip object
        TravelMethod method = trip.getTravelMethod();  // Get travel method
        Location destination = trip.getDestination();  // Get destination location
        CurrentLocation currentLocation = trip.getCurrentLocation();  // Get current location
        String userId = trip.getUserId();  // Get user ID

        // Calculate the distance (You can modify this as needed)
        double distance = calculateDistance(currentLocation, destination);

        // Fetch user weight from the database (this can be dynamic or mock for now)
        double weight = getUserWeight(userId);

        // Calculate calories and carbon saved based on the travel method and distance
        int caloriesBurned = calculateCalories(method, distance, weight);
        int carbonSaved = calculateCarbon(method, distance);

        // Create a map to send the metrics back as a response
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("caloriesBurnt", caloriesBurned);
        metrics.put("carbonSaved", carbonSaved);
        metrics.put("distance", distance);

        return metrics;
    }


    private double calculateDistance(CurrentLocation userLocation, Location destination) {
        double earthRadius = 6371; // Radius of the Earth in kilometers
        double dLat = toRadians(destination.getLatitude() - userLocation.getLatitude());
        double dLon = toRadians(destination.getLongitude() - userLocation.getLongitude());

        double a = sin(dLat / 2) * sin(dLat / 2) +
                   cos(toRadians(userLocation.getLatitude())) *
                   cos(toRadians(destination.getLatitude())) *
                   sin(dLon / 2) * sin(dLon / 2);

        double c = 2 * atan2(sqrt(a), sqrt(1 - a));
        return earthRadius * c; // Distance in kilometers
    }

    private int calculateCalories(TravelMethod method, double distance, double weight) {
        switch (method) {
            case WALK:
                return (int) (distance * weight * 0.5); 
            case CYCLE:
                return (int) (distance * weight * 0.3); 
            case PUBLIC_TRANSPORT:
                return (int) (distance * weight * 0.1);
            case CAR:
                return 0; // No calories burned while driving
            default:
                return 0;
        }
    }

    private int calculateCarbon(TravelMethod method, double distance) {
        switch (method) {
            case WALK:
                return (int) (distance * 30);
            case CYCLE:
                return (int) (distance * 20); 
            case PUBLIC_TRANSPORT:
                return (int) (distance * 10);
            case CAR:
                return 0; // No carbon saved while using these methods
            default:
                return 0;
        }
    }

    private void insertTripIntoDatabase(Trip trip) {
        String insertSQL = "INSERT INTO trips (trip_id, start_location, start_longitude, "
                         + "start_latitude, end_location, end_latitude, "
                         + "end_longitude, distance, calories_burnt, carbon_saved, trip_time, travel_method, status, user_id) "
                         + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; // Ensure correct count

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            // Set values for the insert query
            preparedStatement.setString(1, generateUniqueTripId()); // Use generated user ID
            preparedStatement.setString(2, trip.getCurrentLocation().getName());
            preparedStatement.setDouble(3, trip.getCurrentLocation().getLatitude());
            preparedStatement.setDouble(4, trip.getCurrentLocation().getLongitude());
            preparedStatement.setString(5, trip.getDestination().getName());
            preparedStatement.setDouble(6, trip.getDestination().getLatitude());
            preparedStatement.setDouble(7, trip.getDestination().getLongitude());
            preparedStatement.setDouble(8, trip.getDistance());
            preparedStatement.setInt(9, trip.getCaloriesBurnt());
            preparedStatement.setInt(10, trip.getCarbonSaved());
            preparedStatement.setTimestamp(11, getCurrentSqlTimestamp());
            preparedStatement.setString(12, trip.getTravelMethod().toString()); // Store enum as string
            preparedStatement.setString(13, "ONGOING"); // Set status
            preparedStatement.setString(14, trip.getUserId()); // Set user ID

            // Execute the insert statement
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Trip data inserted successfully.");
            } else {
                System.out.println("No trip data was inserted.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            throw new RuntimeException("Error inserting trip data into the database.", e);
        }
    }

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int TRIP_ID_LENGTH = 6;
    private SecureRandom random = new SecureRandom();

    // Method to generate a random unique trip ID
    private String generateUniqueTripId() {
        String tripId;
        do {
            tripId = generateRandomString();
        } while (tripIdExists(tripId)); // Check if the trip ID already exists
        return tripId;
    }

    // Method to generate a random alphanumeric string
    private String generateRandomString() {
        StringBuilder sb = new StringBuilder(TRIP_ID_LENGTH);
        for (int i = 0; i < TRIP_ID_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    // Method to check if a trip ID already exists in the database
    private boolean tripIdExists(String tripId) {
        String query = "SELECT COUNT(*) FROM trips WHERE trip_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, tripId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // If count > 0, the ID exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private double getUserWeight(String userId) {
        String query = "SELECT weight FROM UserInfo WHERE user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getDouble("weight"); // Retrieve the weight
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("User weight not found for userId: " + userId);
    }
    
    
}
