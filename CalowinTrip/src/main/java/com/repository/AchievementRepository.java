package com.repository;

import com.Database.DatabaseConnection;
import com.Entity.AchievementEntity;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Optional;

@Repository
public class AchievementRepository {

    public Optional<AchievementEntity> fetchAchievementForUser(String userId) {
        String query = "SELECT * FROM achievement WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                AchievementEntity achievementEntity = new AchievementEntity();
                achievementEntity.setTotalCarbonSavedExp(rs.getInt("total_carbon_saved"));
                achievementEntity.setTotalCalorieBurntExp(rs.getInt("total_calorie_burnt"));
                achievementEntity.setCarbonSavedMedal(rs.getString("carbon_medal"));
                achievementEntity.setCalorieBurntMedal(rs.getString("calorie_medal"));
                return Optional.of(achievementEntity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void updateAchievement(AchievementEntity achievementEntity, String userId) {
        // First, try to update. If no rows are affected, then insert.
        String updateQuery = "UPDATE achievement SET total_carbon_saved = ?, total_calorie_burnt = ?, carbon_medal = ?, calorie_medal = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
            updateStmt.setInt(1, achievementEntity.getTotalCarbonSavedExp());
            updateStmt.setInt(2, achievementEntity.getTotalCalorieBurntExp());
            updateStmt.setString(3, achievementEntity.getCarbonSavedMedal());
            updateStmt.setString(4, achievementEntity.getCalorieBurntMedal());
            updateStmt.setString(5, userId);

            int rowsAffected = updateStmt.executeUpdate();

            if (rowsAffected == 0) {
                // No existing record, so insert a new one
                String insertQuery = "INSERT INTO achievement (user_id, total_carbon_saved, total_calorie_burnt, carbon_medal, calorie_medal) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, userId);
                    insertStmt.setInt(2, achievementEntity.getTotalCarbonSavedExp());
                    insertStmt.setInt(3, achievementEntity.getTotalCalorieBurntExp());
                    insertStmt.setString(4, achievementEntity.getCarbonSavedMedal());
                    insertStmt.setString(5, achievementEntity.getCalorieBurntMedal());
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving achievement for user " + userId, e);
        }
    }
}