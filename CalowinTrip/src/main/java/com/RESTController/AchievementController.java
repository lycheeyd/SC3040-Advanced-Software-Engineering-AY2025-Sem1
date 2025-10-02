package com.RESTController;


import com.DataTransferObject.AchievementResponse;
import com.Services.AchievementService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/achievements")
public class AchievementController {

    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @PostMapping("/addTripMetrics")
    public void addTripMetrics(@RequestParam String userId, @RequestParam int carbonSaved, @RequestParam int caloriesBurnt) {
        achievementService.addTripMetricsToAchievement(userId, carbonSaved, caloriesBurnt);
    }

    @GetMapping("/progress")
    public AchievementResponse getAchievementProgress(@RequestParam String userId) {
        return achievementService.getAchievementProgress(userId);
    }
}
