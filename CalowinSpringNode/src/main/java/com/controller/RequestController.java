package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class RequestController {

    private final RestTemplate restTemplate;

    @Autowired
    public RequestController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/{userId}/friend-requests")
    public ResponseEntity<?> getFriendRequests(@PathVariable("userId") String userId) {
        // Construct the URL to call the NotificationController endpoint
        String url = "https://sc3040G5-CalowinFriends.hf.space/notifications/friend-requests/" + userId;

        try {
            // Use RestTemplate to make the request to the backend
            ResponseEntity<List<String>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<String>>() {
                    });

            List<String> friendRequests = response.getBody();

            if (friendRequests == null || friendRequests.isEmpty()) {
                return new ResponseEntity<>("No friend requests found for the given userId.", HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(friendRequests, HttpStatus.OK);

        } catch (Exception e) {
            // Log and return error response
            return new ResponseEntity<>("Failed to retrieve friend requests: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
