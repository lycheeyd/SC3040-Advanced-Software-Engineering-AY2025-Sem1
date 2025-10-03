package com.controller;


import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ENUM.TravelMethod;
import com.DataTransferObject.TripMetricsRequestDTO;
import com.DataTransferObject.TripStartRequestDTO;
import com.model.Trip;
import com.service.TripService;
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
    public Trip startTrip(@RequestBody TripStartRequestDTO tripStartRequest) {
        return tripService.startTrip(tripStartRequest);
    }

    @PostMapping("/retrieve-metrics")
    public Map<String, Object> retrieveMetrics(@RequestBody TripMetricsRequestDTO tripMetricsRequest) {
        return tripService.calculateTripMetrics(tripMetricsRequest);
    }
}