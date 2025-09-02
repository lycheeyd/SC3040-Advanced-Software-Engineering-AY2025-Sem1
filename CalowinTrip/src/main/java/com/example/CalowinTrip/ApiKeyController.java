package com.example.CalowinTrip;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiKeyController {

    // Method to fetch the API key from the database
    private String getApiKeyFromDatabase(String keyName) {
        String selectSQL = "SELECT ApiKey FROM ApiKeys WHERE KeyName = ?";
    
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
    
            // Set the key name in the query
            preparedStatement.setString(1, keyName);
    
            // Execute the query and retrieve the result
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Retrieve the API key from the result set
                    String apiKey = resultSet.getString("ApiKey");
                    System.out.println("API Key retrieved successfully: " + apiKey);
                    return apiKey;
                } else {
                    System.out.println("API Key not found for key name: " + keyName);
                    return null;
                }
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            throw new RuntimeException("Error retrieving API key from the database.", e);
        }
    }

    // GetMapping to handle the GET request
    @GetMapping("/keys/{keyName}")
    public String getApiKey(@PathVariable String keyName) {
        String apiKey = getApiKeyFromDatabase(keyName);
        if (apiKey != null) {
            return apiKey;
        } else {
            return "API Key not found for key name: " + keyName;
        }
    }
}
