package com.Services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import com.DataTransferObject.AchievementDTO;
import com.Entity.AchievementEntity;
import com.Entity.FriendRelationshipEntity;
import com.repository.*;

@Service
public class LeaderboardService {

    @Autowired
    private FriendRelationshipRepository friendRelationshipRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Achievement RowMapper remains the same
    private RowMapper<AchievementEntity> achievementRowMapper = new RowMapper<AchievementEntity>() {
        @Override
        public AchievementEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            AchievementEntity achievement = new AchievementEntity();
            achievement.setUserId(rs.getString("user_id"));
            achievement.setTotalCarbonSaved(rs.getInt("total_carbon_saved"));
            achievement.setTotalCalorieBurnt(rs.getInt("total_calorie_burnt"));
            achievement.setCarbonMedal(rs.getString("carbon_medal"));
            achievement.setCalorieMedal(rs.getString("calorie_medal"));
            return achievement;
        }
    };

    public List<String> getFriendsIds(String userId) {
        List<FriendRelationshipEntity> relationships = friendRelationshipRepository
                .findByIdUniqueIdOrIdFriendUniqueIdAndStatus(userId, userId, "ACCEPTED");

        // Filter by accepted status to ensure no pending relationships are included
        List<String> friendsIds = relationships.stream()
                .filter(r -> "ACCEPTED".equals(r.getStatus())) // Double-check the status within the stream
                .map(r -> r.getId().getUniqueId().equals(userId) ? r.getId().getFriendUniqueId()
                        : r.getId().getUniqueId())
                .distinct()
                .collect(Collectors.toList());

        friendsIds.add(userId); // Optionally include the user itself
        return friendsIds;
    }

    public List<AchievementDTO> getCarbonLeaderboard(String userId) {
        List<String> friendsIds = getFriendsIds(userId);

        String sql = "SELECT * FROM Achievement WHERE user_id IN (" +
                friendsIds.stream().map(id -> "?").collect(Collectors.joining(", ")) + ")";

        List<AchievementEntity> achievements = jdbcTemplate.query(
                sql,
                friendsIds.toArray(),
                achievementRowMapper);

        return achievements.stream()
                .sorted((a, b) -> Integer.compare(b.getTotalCarbonSaved(), a.getTotalCarbonSaved()))
                .map(a -> {
                    AchievementDTO dto = new AchievementDTO();
                    dto.setUserId(a.getUserId());
                    dto.setUserName(getUserNameById(a.getUserId()));
                    dto.setTotalCarbonSaved(a.getTotalCarbonSaved());
                    dto.setTotalCalorieBurnt(a.getTotalCalorieBurnt());
                    dto.setCarbonMedal(a.getCarbonMedal());
                    dto.setCalorieMedal(a.getCalorieMedal());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<AchievementDTO> getCaloriesLeaderboard(String userId) {
        List<String> friendsIds = getFriendsIds(userId);

        String sql = "SELECT * FROM Achievement WHERE user_id IN (" +
                friendsIds.stream().map(id -> "?").collect(Collectors.joining(", ")) + ")";

        List<AchievementEntity> achievements = jdbcTemplate.query(
                sql,
                friendsIds.toArray(),
                achievementRowMapper);

        return achievements.stream()
                .sorted((a, b) -> Integer.compare(b.getTotalCalorieBurnt(), a.getTotalCalorieBurnt()))
                .map(a -> {
                    AchievementDTO dto = new AchievementDTO();
                    dto.setUserId(a.getUserId());
                    dto.setUserName(getUserNameById(a.getUserId()));
                    dto.setTotalCarbonSaved(a.getTotalCarbonSaved());
                    dto.setTotalCalorieBurnt(a.getTotalCalorieBurnt());
                    dto.setCarbonMedal(a.getCarbonMedal());
                    dto.setCalorieMedal(a.getCalorieMedal());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public String getUserNameById(String userId) {
        String sql = "SELECT name FROM UserInfo WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[] { userId }, String.class);
    }
}