package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/central/wellness")
public class WellnessZoneController {

    private final RestTemplate restTemplate;

    @Autowired
    public WellnessZoneController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/parks")
    public List<Map<String, Object>> findNearbyParks(@RequestParam("lat") double userLat, @RequestParam("lon") double userLon) {
        // Construct the URL to call the HttpReqController endpoint
        String url = "http://localhost:8085/api/parks?lat=" + userLat + "&lon=" + userLon;

        // Use RestTemplate to make the request to the backend
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {
                });

        List<Map<String, Object>> responseBody = response.getBody();
        return responseBody;
    }
}
