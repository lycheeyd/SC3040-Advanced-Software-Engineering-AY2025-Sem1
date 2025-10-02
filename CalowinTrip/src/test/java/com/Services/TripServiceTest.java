package com.Services;

import com.DataTransferObject.TripMetricsRequest;
import com.DataTransferObject.TripStartRequest;
import com.Entity.CurrentLocationEntity;
import com.Entity.LocationEntity;
import com.Entity.TravelMethod;
import com.Entity.Trip;
import com.repository.TripRepository;
import com.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    // These dependencies of TripService will be mocked
    @Mock
    private TripRepository tripRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AchievementService achievementService;

    // This creates an instance of TripService and injects the mocks into it
    @InjectMocks
    private TripService tripService;

    private TripStartRequest tripStartRequest;
    private TripMetricsRequest tripMetricsRequest;

    @BeforeEach
    void setUp() {
        // We use fixed locations to get a consistent, predictable distance for our tests.
        // The calculated distance for these coordinates is ~8.207 km.
        CurrentLocationEntity start = new CurrentLocationEntity("Start Point", 1.3521, 103.8198);
        LocationEntity end = new LocationEntity("End Point", 1.2869, 103.8544);

        tripStartRequest = new TripStartRequest();
        tripStartRequest.setUserId("user123");
        tripStartRequest.setTravelMethod(TravelMethod.WALK);
        tripStartRequest.setCurrentLocation(start);
        tripStartRequest.setDestination(end);

        tripMetricsRequest = new TripMetricsRequest();
        tripMetricsRequest.setUserId("user123");
        tripMetricsRequest.setTravelMethod(TravelMethod.CYCLE);
        tripMetricsRequest.setCurrentLocation(start);
        tripMetricsRequest.setDestination(end);
    }

    @Test
    @DisplayName("startTrip should calculate metrics, save trip, update achievements, and return trip object")
    void startTrip_whenUserExists_shouldCalculateAndSaveTrip() {
        // Arrange
        double userWeight = 70.0; // kg
        int expectedCalories = 287; // (int) (8.207 * 70.0 * 0.5)
        int expectedCarbon = 246;   // (int) (8.207 * 30)

        // Configure mocks to return expected values
        when(userRepository.getUserWeight("user123")).thenReturn(Optional.of(userWeight));
        when(tripRepository.tripIdExists(anyString())).thenReturn(false); // Ensure the ID generation loop runs once

        // Act
        Trip resultTrip = tripService.startTrip(tripStartRequest);

        // Assert
        // 1. Verify the returned object has the correct values
        assertThat(resultTrip).isNotNull();
        assertThat(resultTrip.getUserId()).isEqualTo("user123");
        assertThat(resultTrip.getDistance()).isBetween(8.2, 8.3);
        assertThat(resultTrip.getCaloriesBurnt()).isEqualTo(expectedCalories);
        assertThat(resultTrip.getCarbonSaved()).isEqualTo(expectedCarbon);
        assertThat(resultTrip.getTripId()).isNotNull();

        // 2. Verify that the repository and other services were called correctly
        verify(tripRepository, times(1)).insertTripIntoDatabase(any(Trip.class));
        verify(achievementService, times(1)).addTripMetricsToAchievement("user123", expectedCarbon, expectedCalories);

        // 3. (Optional) Capture the object passed to the repository to inspect it
        ArgumentCaptor<Trip> tripCaptor = ArgumentCaptor.forClass(Trip.class);
        verify(tripRepository).insertTripIntoDatabase(tripCaptor.capture());
        assertThat(tripCaptor.getValue().getCaloriesBurnt()).isEqualTo(expectedCalories);
    }

    @Test
    @DisplayName("startTrip should throw RuntimeException when user weight is not found")
    void startTrip_whenUserWeightNotFound_shouldThrowException() {
        // Arrange
        when(userRepository.getUserWeight("user123")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tripService.startTrip(tripStartRequest);
        });

        // Verify the exception message is correct
        assertThat(exception.getMessage()).isEqualTo("User weight not found for userId: user123");

        // Verify that no database insertions or achievement updates occurred
        verify(tripRepository, never()).insertTripIntoDatabase(any(Trip.class));
        verify(achievementService, never()).addTripMetricsToAchievement(anyString(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("calculateTripMetrics should return a Map with correct calculated values")
    void calculateTripMetrics_whenUserExists_shouldReturnMetricsMap() {
        // Arrange
        double userWeight = 75.0; // kg
        int expectedCalories = 184; // (int) (8.207 * 75.0 * 0.3) for CYCLE
        int expectedCarbon = 164;   // (int) (8.207 * 20) for CYCLE

        when(userRepository.getUserWeight("user123")).thenReturn(Optional.of(userWeight));

        // Act
        Map<String, Object> metrics = tripService.calculateTripMetrics(tripMetricsRequest);

        // Assert
        assertThat(metrics).isNotNull();
        assertThat(metrics.get("caloriesBurnt")).isEqualTo(expectedCalories);
        assertThat(metrics.get("carbonSaved")).isEqualTo(expectedCarbon);
        assertThat((Double) metrics.get("distance")).isBetween(8.2, 8.3);
    }

    @Test
    @DisplayName("calculateTripMetrics should throw RuntimeException when user weight is not found")
    void calculateTripMetrics_whenUserWeightNotFound_shouldThrowException() {
        // Arrange
        when(userRepository.getUserWeight("user123")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            tripService.calculateTripMetrics(tripMetricsRequest);
        });
    }

    @ParameterizedTest
    @DisplayName("calculateCalories and calculateCarbon should return correct values for all travel methods")
    @CsvSource({
            "WALK, 70.0, 287, 246",
            "CYCLE, 80.0, 196, 164",
            "PUBLIC_TRANSPORT, 65.0, 53, 82"
    })
    void calculations_forAllTravelMethods_shouldReturnCorrectValues(TravelMethod method, double weight, int expectedCalories, int expectedCarbon) {
        // Arrange
        tripMetricsRequest.setTravelMethod(method); // Use the method from the CSV source
        when(userRepository.getUserWeight("user123")).thenReturn(Optional.of(weight));

        // Act
        Map<String, Object> metrics = tripService.calculateTripMetrics(tripMetricsRequest);

        // Assert
        assertThat(metrics.get("caloriesBurnt")).isEqualTo(expectedCalories);
        assertThat(metrics.get("carbonSaved")).isEqualTo(expectedCarbon);
    }
}