package com.repository;

import com.Database.DatabaseConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class UserRepository {
    private final DataSource dataSource; // <-- Add a field for DataSource

    // Add this constructor to let Spring inject the DataSource
    @Autowired
    public UserRepository(@Qualifier("calowin-dbDataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<Double> getUserWeight(String userId) {
        String query = "SELECT weight FROM UserInfo WHERE user_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return Optional.of(rs.getDouble("weight"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}