package com.RESTController;


import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.Entity.TravelMethod;
import com.DataTransferObject.TripMetricsRequest;
import com.DataTransferObject.TripStartRequest;
import com.Entity.TripEntity;
import com.Services.TripService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping("/methods")
    public List<TravelMethod> getTravelMethods() {
        return Arrays.asList(TravelMethod.values());
    }

    @PostMapping("/start")
    public TripEntity startTrip(@RequestBody TripStartRequest tripStartRequest) {
        return tripService.startTrip(tripStartRequest);
    }

    @PostMapping("/retrieve-metrics")
    public Map<String, Object> retrieveMetrics(@RequestBody TripMetricsRequest tripMetricsRequest) {
        return tripService.calculateTripMetrics(tripMetricsRequest);
    }
}