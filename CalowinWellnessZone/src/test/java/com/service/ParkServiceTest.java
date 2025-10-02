package com.service;

import com.Services.NParkApiService;
import com.Managers.ParkManager;
import com.model.NPark;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkServiceTest {

    // We mock the ParkApiClient because we don't want to make real HTTP calls in a unit test
    @Mock
    private NParkApiService parkApiClient;

    // This injects the mock ParkApiClient into our ParkService instance
    @InjectMocks
    private ParkManager parkService;

    @Test
    @DisplayName("findNearbyParks should return a list of NParks when API call is successful")
    void findNearbyParks_whenApiSucceeds_shouldReturnParkList() throws Exception {
        // Arrange
        double userLat = 1.3521;
        double userLon = 103.8198;

        // A sample GeoJSON response from the API
        String fakeJsonData = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{\"Description\":\"<table><tr><th>NAME</th> <td>Bishan - Ang Mo Kio Park</td></tr></table>\"},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[103.83,1.37],[103.84,1.37],[103.83,1.36],[103.83,1.37]]]}}]}";

        // Configure the mock client to return the fake data
        when(parkApiClient.getResponseData()).thenReturn(fakeJsonData);
        when(parkApiClient.getErrorMessage()).thenReturn(""); // No error

        // Act
        List<NPark> parks = parkService.findNearbyParks(userLat, userLon);

        // Assert
        assertThat(parks).isNotNull();
        assertThat(parks).hasSize(1);
        assertThat(parks.get(0).getName()).isEqualTo("Bishan - Ang Mo Kio Park");
        // Check that the distance was calculated
        assertThat(parks.get(0).getDistance()).isPositive();
    }

    @Test
    @DisplayName("findNearbyParks should throw an exception when the API client has an error")
    void findNearbyParks_whenApiHasError_shouldThrowException() {
        // Arrange
        double userLat = 1.3521;
        double userLon = 103.8198;

        // Configure the mock client to return an error message
        when(parkApiClient.getErrorMessage()).thenReturn("Failed to connect");

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            parkService.findNearbyParks(userLat, userLon);
        });

        assertThat(exception.getMessage()).isEqualTo("Data download error: Failed to connect");
    }

    @Test
    @DisplayName("findNearbyParks should throw an exception when API returns no data")
    void findNearbyParks_whenApiReturnsNoData_shouldThrowException() {
        // Arrange
        double userLat = 1.3521;
        double userLon = 103.8198;

        // Configure the mock client to return empty data and no error
        when(parkApiClient.getResponseData()).thenReturn("");
        when(parkApiClient.getErrorMessage()).thenReturn("");

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            parkService.findNearbyParks(userLat, userLon);
        });

        assertThat(exception.getMessage()).isEqualTo("No response data received from downloader.");
    }
}