package com.controller;

import com.dto.ParkResponse;
import com.model.NPark;
import com.service.ParkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ParkController {

    @Autowired
    private ParkService parkService;

    @GetMapping("/parks")
    public ResponseEntity<?> getNearbyParks(@RequestParam("lat") double userLat, @RequestParam("lon") double userLon) {
        try {
            List<NPark> parks = parkService.findNearbyParks(userLat, userLon);

            if (parks.isEmpty()) {
                return new ResponseEntity<>("No parks found near the given coordinates.", HttpStatus.NOT_FOUND);
            }

            // Map the list of NPark domain objects to a list of ParkResponseDto objects
            List<ParkResponse> formattedParks = parks.stream()
                    .map(park -> new ParkResponse(park.getName(), park.getDistance(), park.getClosestPoint()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(formattedParks);

        } catch (Exception e) {
            // It's good practice to log the exception in a real application
            // e.g., log.error("Error fetching parks", e);
            return new ResponseEntity<>("Failed to retrieve parks data: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}