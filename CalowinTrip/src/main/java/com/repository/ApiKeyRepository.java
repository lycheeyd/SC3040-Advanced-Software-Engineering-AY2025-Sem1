package com.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class ApiKeyRepository {

    private final DataSource dataSource; // <-- Add a field for DataSource

    // Add this constructor to let Spring inject the DataSource
    @Autowired
    public ApiKeyRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<String> getApiKeyFromDatabase(String keyName) {
        String selectSQL = "SELECT ApiKey FROM ApiKeys WHERE KeyName = ?";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, keyName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(resultSet.getString("ApiKey"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving API key from the database.", e);
        }
        return Optional.empty();
    }
}