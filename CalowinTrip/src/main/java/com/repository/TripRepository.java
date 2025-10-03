package com.repository;

import com.model.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;

@Repository
public class TripRepository {

    private final DataSource dataSource; // <-- Add a field for DataSource

    // Add this constructor to let Spring inject the DataSource
    @Autowired
    public TripRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public void insertTripIntoDatabase(Trip trip) {
        String insertSQL = "INSERT INTO trips (trip_id, start_location, start_longitude, "
                + "start_latitude, end_location, end_latitude, "
                + "end_longitude, distance, calories_burnt, carbon_saved, trip_time, travel_method, status, user_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            preparedStatement.setString(1, trip.getTripId());
            preparedStatement.setString(2, trip.getCurrentLocation().getName());
            preparedStatement.setDouble(3, trip.getCurrentLocation().getLongitude());
            preparedStatement.setDouble(4, trip.getCurrentLocation().getLatitude());
            preparedStatement.setString(5, trip.getDestination().getName());
            preparedStatement.setDouble(6, trip.getDestination().getLatitude());
            preparedStatement.setDouble(7, trip.getDestination().getLongitude());
            preparedStatement.setDouble(8, trip.getDistance());
            preparedStatement.setInt(9, trip.getCaloriesBurnt());
            preparedStatement.setInt(10, trip.getCarbonSaved());
            preparedStatement.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setString(12, trip.getTravelMethod().toString());
            preparedStatement.setString(13, "ONGOING");
            preparedStatement.setString(14, trip.getUserId());

            // Execute the insert statement
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Trip data inserted successfully.");
            } else {
                System.out.println("No trip data was inserted.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error inserting trip data into the database.", e);
        }
    }

    public boolean tripIdExists(String tripId) {
        String query = "SELECT COUNT(*) FROM trips WHERE trip_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, tripId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}