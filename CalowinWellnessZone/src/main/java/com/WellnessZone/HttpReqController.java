package com.WellnessZone;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HttpReqController {

    @GetMapping("/parks")
    public ResponseEntity<?> getNearbyParks(@RequestParam("lat") double userLat, @RequestParam("lon") double userLon) {
        try {
            // Initialize the NParkExtracter with user coordinates
            NParkExtracter parkExtracter = new NParkExtracter(userLat, userLon);

            // Get the list of NPark objects
            List<NPark> parks = parkExtracter.getParks();

            // Check if parks list is empty and return an appropriate message
            if (parks.isEmpty()) {
                return new ResponseEntity<>("No parks found near the given coordinates.", HttpStatus.NOT_FOUND);
            }

            // Reformat parks into a List of HashMaps to send as a response
            List<Map<String, Object>> formattedParks = new ArrayList<>();
            for (NPark park : parks) {
                Map<String, Object> parkMap = new HashMap<>();
                parkMap.put("name", park.getName());
                parkMap.put("distance", park.getDistance());
                parkMap.put("closestPoint", park.getClosestPoint());
                formattedParks.add(parkMap);
            }

            return new ResponseEntity<>(formattedParks, HttpStatus.OK);

        } catch (Exception e) {
            // Log the error and return a generic error response
            return new ResponseEntity<>("Failed to retrieve parks data: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
