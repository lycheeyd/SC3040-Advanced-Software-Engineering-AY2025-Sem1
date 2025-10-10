package com.controller;

import com.DataTransferObject.AchievementResponseDTO;
import com.service.AchievementService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/achievements")
public class AchievementController {

    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @PostMapping("/addTripMetrics")
    public void addTripMetrics(@RequestParam String userId, @RequestParam int carbonSaved,
            @RequestParam int caloriesBurnt) {
        achievementService.addTripMetricsToAchievement(userId, carbonSaved, caloriesBurnt);
    }

    @GetMapping("/progress")
    public AchievementResponseDTO getAchievementProgress(@RequestParam String userId) {
        return achievementService.getAchievementProgress(userId);
    }
}
