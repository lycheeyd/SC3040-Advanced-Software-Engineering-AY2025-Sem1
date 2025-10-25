package com.Services;


import com.ENUM.TravelMethod;
import com.model.CurrentLocation;
import com.model.Location;
import com.model.Trip;
import com.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripRepositoryTest {

    @Mock
    private DataSource dataSource;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private TripRepository tripRepository;

    private Trip testTrip;

    @BeforeEach
    void setUp() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);

        CurrentLocation start = new CurrentLocation("Start", 1.0, 103.0);
        Location end = new Location("End", 1.1, 103.1);
        testTrip = new Trip("trip123", start, end, TravelMethod.WALK, "user123");
        testTrip.setDistance(2.5);
        testTrip.setCaloriesBurnt(100);
        testTrip.setCarbonSaved(50);
    }

    @Test
    @DisplayName("insertTripIntoDatabase should execute insert statement")
    void insertTrip_shouldExecuteInsert() throws SQLException {
        // Arrange
        when(connection.prepareStatement(startsWith("INSERT INTO trips"))).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // Act
        tripRepository.insertTripIntoDatabase(testTrip);

        // Assert
        // Assert
        verify(preparedStatement).setString(1, "trip123");
        verify(preparedStatement).setString(2, "Start");
        verify(preparedStatement).setDouble(3, 103.0);
        verify(preparedStatement).setDouble(4, 1.0);
        verify(preparedStatement).setString(5, "End");
        verify(preparedStatement).setDouble(6, 1.1);
        verify(preparedStatement).setDouble(7, 103.1);
        verify(preparedStatement).setDouble(8, 2.5);
        verify(preparedStatement).setInt(9, 100);
        verify(preparedStatement).setInt(10, 50);
        verify(preparedStatement).setTimestamp(eq(11), any(Timestamp.class)); // <-- CORRECTED LINE
        verify(preparedStatement).setString(12, "WALK");
        verify(preparedStatement).setString(13, "ONGOING");
        verify(preparedStatement).setString(14, "user123");

        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    @DisplayName("insertTripIntoDatabase should throw RuntimeException on SQLException")
    void insertTrip_onSqlException_shouldThrowRuntimeException() throws SQLException {
        // Arrange
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("DB error"));

        // Act & Assert
        assertThatThrownBy(() -> {
            tripRepository.insertTripIntoDatabase(testTrip);
        }).isInstanceOf(RuntimeException.class)
                .hasMessage("Error inserting trip data into the database.");
    }

    @Test
    @DisplayName("tripIdExists should return true when ID exists")
    void tripIdExists_whenExists_shouldReturnTrue() throws SQLException {
        // Arrange
        when(connection.prepareStatement(startsWith("SELECT COUNT(*)"))).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        // Act
        boolean exists = tripRepository.tripIdExists("trip123");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("tripIdExists should return false when ID does not exist")
    void tripIdExists_whenNotExists_shouldReturnFalse() throws SQLException {
        // Arrange
        when(connection.prepareStatement(startsWith("SELECT COUNT(*)"))).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(0);

        // Act
        boolean exists = tripRepository.tripIdExists("trip999");

        // Assert
        assertThat(exists).isFalse();
    }
}