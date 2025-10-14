package com.controller;

import com.dto.ParkResponseDTO;
import com.model.NPark;
import com.service.ParkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // <-- 2. Add this annotation

public class ParkController {

    @Autowired
    private ParkService parkService;

    @GetMapping("/parks")
    public ResponseEntity<?> getNearbyParks(@RequestParam("lat") double userLat, @RequestParam("lon") double userLon) {
        try {
            List<NPark> parks = parkService.findNearbyParks(userLat, userLon);

            // In ParkController.java

            if (parks.isEmpty()) {
                // This is a clearer response: status 200 OK with an empty list
                return new ResponseEntity<>(List.of(), HttpStatus.OK);
            }

            // Map the list of NPark domain objects to a list of ParkResponseDto objects
            List<ParkResponseDTO> formattedParks = parks.stream()
                    .map(park -> new ParkResponseDTO(park.getName(), park.getDistance(), park.getClosestPoint()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(formattedParks);

        } catch (Exception e) {
            // It's good practice to log the exception in a real application
            // e.g., log.error("Error fetching parks", e);
            return new ResponseEntity<>("Failed to retrieve parks data: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}