package com.Services;


import com.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private DataSource dataSource;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    @DisplayName("getUserWeight should return weight when user is found")
    void getUserWeight_whenUserFound_shouldReturnWeight() throws SQLException {
        // Arrange
        String userId = "user123";
        double expectedWeight = 70.5;

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getDouble("weight")).thenReturn(expectedWeight);

        // Act
        Optional<Double> actualWeight = userRepository.getUserWeight(userId);

        // Assert
        assertThat(actualWeight).isPresent().contains(expectedWeight);
    }

    @Test
    @DisplayName("getUserWeight should return empty when user is not found")
    void getUserWeight_whenUserNotFound_shouldReturnEmpty() throws SQLException {
        // Arrange
        String userId = "user-nonexistent";
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // Act
        Optional<Double> actualWeight = userRepository.getUserWeight(userId);

        // Assert
        assertThat(actualWeight).isEmpty();
    }

    @Test
    @DisplayName("getUserWeight should return empty on SQLException")
    void getUserWeight_onSqlException_shouldReturnEmpty() throws SQLException {
        // Arrange
        String userId = "user123";
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("DB error"));

        // Act
        Optional<Double> actualWeight = userRepository.getUserWeight(userId);

        // Assert
        assertThat(actualWeight).isEmpty();
    }
}