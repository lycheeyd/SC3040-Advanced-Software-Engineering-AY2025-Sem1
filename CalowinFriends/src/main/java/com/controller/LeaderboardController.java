package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dto.AchievementDTO;
import com.service.LeaderboardService;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    @Autowired
    private LeaderboardService service;

    @GetMapping("/carbon")
    public ResponseEntity<List<AchievementDTO>> getCarbonLeaderboard(@RequestParam String userId) {
        try {
            List<AchievementDTO> result = service.getCarbonLeaderboard(userId); // Adjusted to use AchievementDTO
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/calories")
    public ResponseEntity<List<AchievementDTO>> getCaloriesLeaderboard(@RequestParam String userId) {
        try {
            List<AchievementDTO> result = service.getCaloriesLeaderboard(userId); // Adjusted to use AchievementDTO
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
