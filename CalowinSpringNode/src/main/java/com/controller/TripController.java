package com.controller;


import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.DataTransferObject.TripDTO.CurrentLocation;
import com.DataTransferObject.TripDTO.Location;
import com.DataTransferObject.TripDTO.Achievement;
import com.DataTransferObject.TripDTO.TripInfoDTO;



@RestController
@RequestMapping("/central/trips")
public class TripController extends HttpReqController{

    public TripController(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @GetMapping("/methods")
    public ResponseEntity<?> getTravelMethods() {
        // Construct the URL for the backend trip controller
        String url = "http://localhost:8082/trips/methods";
        return restTemplate.getForEntity(url, Object.class);
        /* 
        // Make the request and return the result
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        return response.getBody();*/
    }


    @PostMapping("/start")
    public ResponseEntity<String> startTrip(@RequestBody TripInfoDTO tripInfo) {
        // Define the backend URL for starting the trip
        String url = "http://localhost:8082/trips/start"; // URL for backend /start endpoint
        return restTemplate.postForEntity(url, tripInfo, String.class);
        /* 
        // Send the request to the backend with the trip data
        ResponseEntity<String> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                new HttpEntity<>(tripInfo), 
                String.class
        );
        // Return the response from the backend
        return response;*/
    }   

    @PostMapping("/addTripMetrics")
    public ResponseEntity<String> addTripMetrics(@RequestParam int carbonSaved, @RequestParam int caloriesBurnt, TripInfoDTO trip) {
        // Define the backend URL for starting the trip
        String url = "http://localhost:8082/achievement/start"; // URL for backend /start endpoint

        // Send the request to the backend with the trip data
        ResponseEntity<String> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                new HttpEntity<>(trip), 
                String.class
        );
        // Return the response from the backend
        return response;
    }   

    @GetMapping("/progress")
    public ResponseEntity<?> getAchievementProgress() {
        // Construct the URL to call the external achievement service
        String url = "http://localhost:8082/achievements/progress";
        return restTemplate.getForEntity(url, Object.class);
        /* 
        // Use RestTemplate to make the GET request to the external service
        ResponseEntity<Achievement> response = restTemplate.exchange(
            url, 
            HttpMethod.GET, 
            null, 
            Object.class);

        // Check if the response is successful
        if (response.getStatusCode().is2xxSuccessful()) {
            // Return the response body from the external service
            return ResponseEntity.ok(response.getBody());
        } else {
            // Handle failure (e.g., service unavailable, invalid response)
            return ResponseEntity.status(500).body(null);
        }*/
    
    }

    @GetMapping("/api/keys/{keyName}")
    public ResponseEntity<?> getApiKey(@PathVariable String keyName) {
        // Forward view profile request to AccountModule
        try {
            String url = "http://localhost:8082" + "/api/keys/" + keyName;
            return restTemplate.getForEntity(url, String.class);
        } catch (HttpClientErrorException ex) {
            HttpStatusCode statusCode = ex.getStatusCode();
            if (statusCode == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(statusCode).body(ex.getMessage());
            } else {
                return ResponseEntity.status(statusCode).body(statusCode + ex.getMessage());
            }
        } catch (Exception ex)  {
            System.out.println(ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }

    @PostMapping("/retrieve-metrics")
    public ResponseEntity<?> retrieveMetrics(@RequestBody TripInfoDTO trip) {
        String url = "http://localhost:8082/trips/retrieve-metrics";
        return restTemplate.postForEntity(url, trip,Map.class);
    }
}


    


