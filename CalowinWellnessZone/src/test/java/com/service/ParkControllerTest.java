package com.service;

import com.controller.ParkController;
import com.model.NPark;
import com.service.ParkService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 1. Tell Spring to only load the web layer for the ParkController
@WebMvcTest(ParkController.class)
class ParkControllerTest {

    // 2. MockMvc is our main tool for "performing" HTTP requests
    @Autowired
    private MockMvc mockMvc;

    // 3. We mock the service layer, as we're not testing it here
    @MockBean
    private ParkService parkService;

    @Test
    @DisplayName("GET /api/parks should return 200 OK with parks")
    void getNearbyParks_whenParksFound_shouldReturn200() throws Exception {
        // Arrange
        double lat = 1.35;
        double lon = 103.81;

        // Create a mock NPark object
        Map<String, Double> coords = Map.of("Lat", 1.36, "Lon", 103.82);
        NPark mockPark = new NPark(List.of(coords), Map.of("Lat", lat, "Lon", lon), "Test Park");

        // Mock the service call
        when(parkService.findNearbyParks(lat, lon)).thenReturn(List.of(mockPark));

        // Act & Assert
        mockMvc.perform(get("/api/parks")
                        .param("lat", String.valueOf(lat))
                        .param("lon", String.valueOf(lon)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray()) // Expect a JSON array
                .andExpect(jsonPath("$[0].name").value("Test Park")) // Check the park name
                .andExpect(jsonPath("$[0].distance").value(mockPark.getDistance())); // Check distance
    }

    @Test
    @DisplayName("GET /api/parks should return 200 OK with empty list")
    void getNearbyParks_whenNoParksFound_shouldReturn200EmptyList() throws Exception {
        // Arrange
        double lat = 1.35;
        double lon = 103.81;
        when(parkService.findNearbyParks(lat, lon)).thenReturn(List.of()); // Return empty list

        // Act & Assert
        mockMvc.perform(get("/api/parks")
                        .param("lat", String.valueOf(lat))
                        .param("lon", String.valueOf(lon)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty()); // Expect an empty array
    }

    @Test
    @DisplayName("GET /api/parks should return 500 on service error")
    void getNearbyParks_whenServiceThrowsException_shouldReturn500() throws Exception {
        // Arrange
        double lat = 1.35;
        double lon = 103.81;
        String errorMsg = "Database is down";
        when(parkService.findNearbyParks(lat, lon)).thenThrow(new Exception(errorMsg));

        // Act & Assert
        mockMvc.perform(get("/api/parks")
                        .param("lat", String.valueOf(lat))
                        .param("lon", String.valueOf(lon)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to retrieve parks data: " + errorMsg));
    }

    @Test
    @DisplayName("GET /api/parks should return 400 for missing parameters")
    void getNearbyParks_whenMissingParams_shouldReturn400() throws Exception {
        // Act & Assert
        // Try calling without the "lat" parameter
        mockMvc.perform(get("/api/parks")
                        .param("lon", "103.81"))
                .andExpect(status().isBadRequest()); // Expect 400 Bad Request

        // Try calling without the "lon" parameter
        mockMvc.perform(get("/api/parks")
                        .param("lat", "1.35"))
                .andExpect(status().isBadRequest());
    }
}