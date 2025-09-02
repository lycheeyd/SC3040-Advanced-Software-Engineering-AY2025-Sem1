package com.example.CalowinTrip;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/achievements")
public class AchievementController {

    // Endpoint to add trip metrics (carbon saved and calories burnt)
    @PostMapping("/addTripMetrics")
    public void addTripMetrics(@RequestParam int carbonSaved, @RequestParam int caloriesBurnt, String userId) {
        // Fetch the user's current achievement from the database
        Achievement userAchievement = fetchAchievementForUser(userId);

        // Add the trip experience to the user's achievement
        userAchievement.addTripExperience(carbonSaved, caloriesBurnt);

        // Save or update the user's achievement in the database
        try {
            updateAchievement(userAchievement, userId);
        } catch (SQLException e) {
            // Handle the exception (log it, return an error response, etc.)
            e.printStackTrace();
        }
    }

    // Endpoint to get current achievement progress (EXP and medals)
    @GetMapping("/progress")
    public AchievementResponse getAchievementProgress(@RequestParam String userId) {
        // Fetch the user's achievement data
        Achievement userAchievement = fetchAchievementForUser(userId);

        return new AchievementResponse(
            userAchievement.getTotalCarbonSavedExp(),
            userAchievement.getTotalCalorieBurntExp(),
            userAchievement.getCarbonSavedMedal(),
            userAchievement.getCalorieBurntMedal()
        );
    }

    // Method to fetch the user's achievement data from the database
    private Achievement fetchAchievementForUser(String userId) {
        Achievement userAchievement = new Achievement();

        String query = "SELECT * FROM achievement WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                userAchievement.setTotalCarbonSavedExp(rs.getInt("total_carbon_saved"));
                userAchievement.setTotalCalorieBurntExp(rs.getInt("total_calorie_burnt"));
                userAchievement.setCarbonSavedMedal(rs.getString("carbon_medal"));
                userAchievement.setCalorieBurntMedal(rs.getString("calorie_medal"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userAchievement;
    }

    // Method to update the user's achievement data in the database
private void updateAchievement(Achievement userAchievement, String userId) throws SQLException {
    String updateQuery = "UPDATE achievement SET total_carbon_saved = ?, total_calorie_burnt = ?, carbon_medal = ?, calorie_medal = ? WHERE user_id = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

        // Set parameters for the update query
        updateStmt.setInt(1, userAchievement.getTotalCarbonSavedExp());
        updateStmt.setInt(2, userAchievement.getTotalCalorieBurntExp());
        updateStmt.setString(3, userAchievement.getCarbonSavedMedal());
        updateStmt.setString(4, userAchievement.getCalorieBurntMedal());
        updateStmt.setString(5, userId);

        // Execute the update
        int rowsAffected = updateStmt.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Record updated successfully for user " + userId);
        } else {
            System.out.println("No record found for user " + userId + "; update not applied.");
        }

    } catch (SQLException e) {
        System.out.println("Error while updating achievement for user " + userId);
        throw e;
    }
}




    // Response class to encapsulate achievement data
    public static class AchievementResponse {
        private int totalCarbonSavedExp;
        private int totalCalorieBurntExp;
        private String carbonSavedMedal;
        private String calorieBurntMedal;

        public AchievementResponse(int totalCarbonSavedExp, int totalCalorieBurntExp, String carbonSavedMedal,
                String calorieBurntMedal) {
            this.totalCarbonSavedExp = totalCarbonSavedExp;
            this.totalCalorieBurntExp = totalCalorieBurntExp;
            this.carbonSavedMedal = carbonSavedMedal;
            this.calorieBurntMedal = calorieBurntMedal;

        }

        // Getters for the response fields
        public int getTotalCarbonSavedExp() {
            return totalCarbonSavedExp;
        }

        public int getTotalCalorieBurntExp() {
            return totalCalorieBurntExp;
        }

        public String getCarbonSavedMedal() {
            return carbonSavedMedal;
        }

        public String getCalorieBurntMedal() {
            return calorieBurntMedal;
        }

    }

}
