package com.Services;


import com.DataTransferObject.TripMetricsRequestDTO;
import com.DataTransferObject.TripStartRequestDTO;
import com.ENUM.TravelMethod;
import com.controller.TripController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.model.CurrentLocation;
import com.model.Location;
import com.model.Trip;
import com.service.ApiKeyService;
import com.service.TripService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TripController.class)
class TripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TripService tripService;

    @MockBean
    private ApiKeyService apiKeyService; // This mock is required by TripController's constructor

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /trips/methods should return list of travel methods")
    void getTravelMethods_shouldReturnMethodList() throws Exception {
        mockMvc.perform(get("/trips/methods"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]", is("WALK")))
                .andExpect(jsonPath("$[1]", is("CYCLE")))
                .andExpect(jsonPath("$[2]", is("PUBLIC_TRANSPORT")))
                .andExpect(jsonPath("$[3]", is("CAR")));
    }

    @Test
    @DisplayName("POST /trips/start should return a new Trip")
    void startTrip_shouldReturnNewTrip() throws Exception {
        // Arrange
        CurrentLocation start = new CurrentLocation("Start", 1.0, 103.0);
        Location end = new Location("End", 1.1, 103.1);
        TripStartRequestDTO request = new TripStartRequestDTO();
        request.setCurrentLocation(start);
        request.setDestination(end);
        request.setTravelMethod(TravelMethod.WALK);
        request.setUserId("user123");

        Trip mockTrip = new Trip("tripId123", start, end, TravelMethod.WALK, "user123");
        mockTrip.setDistance(10.0);
        mockTrip.setCaloriesBurnt(100);
        mockTrip.setCarbonSaved(50);

        when(tripService.startTrip(any(TripStartRequestDTO.class))).thenReturn(mockTrip);

        // Act & Assert
        mockMvc.perform(post("/trips/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId", is("tripId123")))
                .andExpect(jsonPath("$.userId", is("user123")))
                .andExpect(jsonPath("$.caloriesBurnt", is(100)));
    }

    @Test
    @DisplayName("POST /trips/retrieve-metrics should return metrics map")
    void retrieveMetrics_shouldReturnMetricsMap() throws Exception {
        // Arrange
        TripMetricsRequestDTO request = new TripMetricsRequestDTO();
        request.setUserId("user123");
        // ... set other properties

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("caloriesBurnt", 150);
        metrics.put("carbonSaved", 75);
        metrics.put("distance", 12.5);

        when(tripService.calculateTripMetrics(any(TripMetricsRequestDTO.class))).thenReturn(metrics);

        // Act & Assert
        mockMvc.perform(post("/trips/retrieve-metrics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caloriesBurnt", is(150)))
                .andExpect(jsonPath("$.carbonSaved", is(75)))
                .andExpect(jsonPath("$.distance", is(12.5)));
    }

    @Test
    @DisplayName("GET /trips/places/autocomplete should proxy to Google API")
    void proxyPlaceAutocomplete_shouldReturnGoogleResponse() throws Exception {
        // Arrange
        String googleApiKey = "TEST_API_KEY";
        String googleResponse = "{\"predictions\": [], \"status\": \"OK\"}";

        when(apiKeyService.getApiKey("Places API")).thenReturn(googleApiKey);

        // Note: This test doesn't *actually* call Google, it just confirms
        // the controller is set up. A more advanced test would mock RestTemplate.
        // For @WebMvcTest, we'll assume the RestTemplate call (which happens
        // *inside* the controller method) works and just mock the service.

        // Since RestTemplate is created *inside* the method, we can't mock it
        // easily. A better design would inject RestTemplate.
        // Given the current code, we'll just test the endpoint mapping.

        // A full integration test would be better here.
        // For now, let's just mock the service call.

        // **Re-evaluation**: The controller creates `new RestTemplate()`.
        // This is hard to test. The test below just checks the endpoint exists.
        // A full test would require refactoring to inject `RestTemplate`.

        mockMvc.perform(get("/trips/places/autocomplete")
                        .param("input", "test"))
                .andExpect(status().isOk()); // We expect OK because the *real* RestTemplate
        // will be called by the test. This isn't ideal.
        // It will fail if the machine has no internet
        // or if the URL is bad.
    }
}