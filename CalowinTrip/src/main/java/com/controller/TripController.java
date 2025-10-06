package com.controller;


import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ENUM.TravelMethod;
import com.DataTransferObject.TripMetricsRequestDTO;
import com.DataTransferObject.TripStartRequestDTO;
import com.model.Trip;
import com.service.ApiKeyService;
import com.service.TripService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/trips")
public class TripController {

    private final TripService tripService;
    private final ApiKeyService apiKeyService; // <-- ADD THIS LINE

    public TripController(TripService tripService, ApiKeyService apiKeyService) {
        this.tripService = tripService;
        this.apiKeyService = apiKeyService;
    }

    @GetMapping("/methods")
    public List<TravelMethod> getTravelMethods() {
        return Arrays.asList(TravelMethod.values());
    }

    @PostMapping("/start")
    public Trip startTrip(@RequestBody TripStartRequestDTO tripStartRequest) {
        return tripService.startTrip(tripStartRequest);
    }

    @PostMapping("/retrieve-metrics")
    public Map<String, Object> retrieveMetrics(@RequestBody TripMetricsRequestDTO tripMetricsRequest) {
        return tripService.calculateTripMetrics(tripMetricsRequest);
    }

    @GetMapping("/places/autocomplete")
    public ResponseEntity<?> proxyPlaceAutocomplete(@RequestParam String input) {
        try {
            // Get the API key securely on the server
            String apiKey = apiKeyService.getApiKey("Places API");

            // Build the URL to call the actual Google API
            String googleUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="
                    + input + "&key=" + apiKey;

            // Use RestTemplate to call Google. You'll need to make RestTemplate available here.
            // A simple way is to define it as a @Bean in your main application class.
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForEntity(googleUrl, String.class);

        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }

    @GetMapping("/places/details")
    public ResponseEntity<?> proxyPlaceDetails(@RequestParam("place_id") String placeId) {
        try {
            // Get the API key securely on the server
            String apiKey = apiKeyService.getApiKey("Places API");

            // Build the URL to call the actual Google API
            String googleUrl = "https://maps.googleapis.com/maps/api/place/details/json?place_id="
                    + placeId + "&key=" + apiKey;

            // Use RestTemplate to call Google and forward the response to Flutter
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForEntity(googleUrl, String.class);

        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }
}