package com.service;

import com.model.NPark;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NParkTest {

    @Test
    @DisplayName("NPark constructor should handle empty coordinates list")
    void nPark_withEmptyCoordinates() {
        // Arrange
        Map<String, Double> userCoord = Map.of("Lat", 1.0, "Lon", 1.0);
        List<Map<String, Double>> emptyCoords = Collections.emptyList();

        // Act
        // This will call setClosestPoint (which does nothing)
        // and then setDistance (with an empty closestPoint map)
        NPark park = new NPark(emptyCoords, userCoord, "Empty Park");

        // Assert
        // The check in setDistance should prevent errors
        assertThat(park.getClosestPoint()).isEmpty();
        assertThat(park.getDistance()).isZero(); // Distance defaults to 0.0
    }

    @Test
    @DisplayName("NPark constructor should throw error for invalid user coordinates")
    void nPark_withInvalidUserCoordinates() {
        // Arrange
        Map<String, Double> invalidUserCoord = new HashMap<>(); // Empty map
        List<Map<String, Double>> parkCoords = List.of(Map.of("Lat", 1.0, "Lon", 1.0));

        // Act & Assert
        // This fails in calculateDistance, which is called by setClosestPoint
        assertThrows(IllegalArgumentException.class, () -> {
            new NPark(parkCoords, invalidUserCoord, "Bad Park");
        });
    }

    @Test
    @DisplayName("setDistance should handle missing user coordinates")
    void setDistance_withMissingUserCoords() {
        // Arrange
        Map<String, Double> userCoord = Map.of("Lat", 1.0, "Lon", 1.0);
        List<Map<String, Double>> parkCoords = List.of(Map.of("Lat", 1.1, "Lon", 1.1));
        NPark park = new NPark(parkCoords, userCoord, "Test Park");

        // Act
        // These calls should be gracefully handled by the null/empty checks
        park.setDistance(park.getClosestPoint(), null);
        park.setDistance(park.getClosestPoint(), new HashMap<>());

        // Assert
        // No exception should be thrown, and distance remains unchanged
        // (This just checks that the System.out.println paths are covered)
        assertThat(park.getDistance()).isPositive();
    }
}