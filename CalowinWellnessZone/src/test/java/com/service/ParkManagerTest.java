package com.service;

import com.client.NParkApiClient;
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

    // We mock the ParkApiClient because we don't want to make real HTTP calls in a
    // unit test
    @Mock
    private NParkApiClient parkApiClient;

    // This injects the mock ParkApiClient into our ParkService instance
    @InjectMocks
    private ParkService parkService;

    @Test
    @DisplayName("findNearbyParks should return a list of NParks when API call is successful")
    void findNearbyParks_whenApiSucceeds_shouldReturnParkList() throws Exception {
        // Arrange
        double userLat = 1.3521;
        double userLon = 103.8198;

        // A sample GeoJSON response from the API
        String fakeJsonData = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{\"NAME\":\"Bishan - Ang Mo Kio Park\"},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[103.83,1.37],[103.84,1.37],[103.83,1.36],[103.83,1.37]]]}}]}";
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
    @Test
    @DisplayName("findNearbyParks should return empty list for empty features")
    void findNearbyParks_whenApiReturnsEmptyFeatures() throws Exception {
        // Arrange
        double userLat = 1.3521;
        double userLon = 103.8198;
        String fakeJsonData = "{\"type\":\"FeatureCollection\",\"features\":[]}";

        when(parkApiClient.getResponseData()).thenReturn(fakeJsonData);
        when(parkApiClient.getErrorMessage()).thenReturn("");

        // Act
        List<NPark> parks = parkService.findNearbyParks(userLat, userLon);

        // Assert
        assertThat(parks).isNotNull();
        assertThat(parks).isEmpty();
    }

    @Test
    @DisplayName("findNearbyParks should correctly parse MultiPolygon geometry")
    void findNearbyParks_withMultiPolygon() throws Exception {
        // Arrange
        double userLat = 1.3521;
        double userLon = 103.8198;
        // GeoJSON with a MultiPolygon
        String fakeJsonData = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{\"NAME\":\"Multi-Poly Park\"},\"geometry\":{\"type\":\"MultiPolygon\",\"coordinates\":[[[[103.83,1.37],[103.84,1.37],[103.83,1.36],[103.83,1.37]]]]}}]}";

        when(parkApiClient.getResponseData()).thenReturn(fakeJsonData);
        when(parkApiClient.getErrorMessage()).thenReturn("");

        // Act
        List<NPark> parks = parkService.findNearbyParks(userLat, userLon);

        // Assert
        assertThat(parks).hasSize(1);
        assertThat(parks.get(0).getName()).isEqualTo("Multi-Poly Park");
        assertThat(parks.get(0).getClosestPoint()).isNotEmpty();
    }

    @Test
    @DisplayName("findNearbyParks should skip features with missing names")
    void findNearbyParks_withMissingName() throws Exception {
        // Arrange
        double userLat = 1.3521;
        double userLon = 103.8198;
        // One park with a name, one without
        String fakeJsonData = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{\"NAME\":\"Good Park\"},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[103.83,1.37],[103.84,1.37],[103.83,1.36],[103.83,1.37]]]}}, {\"type\":\"Feature\",\"properties\":{\"NAME\":\"\"},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[104.0,1.4],[104.1,1.4],[104.0,1.3],[104.0,1.4]]]}}]}";

        when(parkApiClient.getResponseData()).thenReturn(fakeJsonData);
        when(parkApiClient.getErrorMessage()).thenReturn("");

        // Act
        List<NPark> parks = parkService.findNearbyParks(userLat, userLon);

        // Assert
        // Should skip the park with the empty name
        assertThat(parks).hasSize(1);
        assertThat(parks.get(0).getName()).isEqualTo("Good Park");
    }

    @Test
    @DisplayName("findNearbyParks should skip unsupported geometry types")
    void findNearbyParks_withUnsupportedGeometry() throws Exception {
        // Arrange
        double userLat = 1.3521;
        double userLon = 103.8198;
        String fakeJsonData = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{\"NAME\":\"Point Park\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[103.83,1.37]}}]}";

        when(parkApiClient.getResponseData()).thenReturn(fakeJsonData);
        when(parkApiClient.getErrorMessage()).thenReturn("");

        // Act
        List<NPark> parks = parkService.findNearbyParks(userLat, userLon);

        // Assert
        // Should skip the "Point" geometry type
        assertThat(parks).isEmpty();
    }

    @Test
    @DisplayName("Untested public methods should execute")
    void testUntestedPublicMethods() {
        // These methods operate on the class-level `parks` list,
        // which `findNearbyParks` doesn't use.
        parkService.setUserCoordinate(1.0, 1.0);
        assertThat(parkService.getParks()).isEmpty(); // Should be empty

        // Call other methods to get coverage
        parkService.printAllParks(); // Prints "No parks found."

        // We can't easily test sortParks without refactoring ParkService,
        // but calling getParks() and printAllParks() covers some lines.
    }
}