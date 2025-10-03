package com.Services;

import com.dto.AchievementDTO;
import com.models.AchievementEntity;
import com.models.FriendRelationshipEntity;
import com.models.FriendRelationshipIdEntity;
import com.repository.FriendRelationshipRepository;
import com.service.LeaderboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {

    @Mock
    private FriendRelationshipRepository friendRelationshipRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private LeaderboardService leaderboardService;

    private FriendRelationshipEntity friendship1;
    private FriendRelationshipEntity friendship2;
    private AchievementEntity userAchievement;
    private AchievementEntity friend1Achievement;
    private AchievementEntity friend2Achievement;

    @BeforeEach
    void setUp() {
        // Helper method to create FriendRelationship objects
        friendship1 = createFriendship("user1", "friend1", "ACCEPTED");
        friendship2 = createFriendship("friend2", "user1", "ACCEPTED");

        // Helper method to create Achievement objects
        userAchievement = createAchievement("user1", "User One", 150, 2500);
        friend1Achievement = createAchievement("friend1", "Friend One", 200, 2200);
        friend2Achievement = createAchievement("friend2", "Friend Two", 100, 3000);
    }

    private FriendRelationshipEntity createFriendship(String userId, String friendId, String status) {
        FriendRelationshipEntity rel = new FriendRelationshipEntity();
        rel.setId(new FriendRelationshipIdEntity(userId, friendId));
        rel.setStatus(status);
        return rel;
    }

    private AchievementEntity createAchievement(String userId, String userName, int carbon, int calories) {
        AchievementEntity ach = new AchievementEntity();
        ach.setUserId(userId);
        ach.setTotalCarbonSaved(carbon);
        ach.setTotalCalorieBurnt(calories);
        // Mock names are set here for simplicity, but in the service, they are fetched separately
        return ach;
    }

    @Test
    @DisplayName("getFriendsIds should return a list of accepted friend IDs including the user")
    void getFriendsIds_ShouldReturnAcceptedFriends() {
        // ARRANGE
        String userId = "user1";
        when(friendRelationshipRepository.findByIdUniqueIdOrIdFriendUniqueIdAndStatus(userId, userId, "ACCEPTED"))
                .thenReturn(List.of(friendship1, friendship2));

        // ACT
        List<String> friendsIds = leaderboardService.getFriendsIds(userId);

        // ASSERT
        assertThat(friendsIds).hasSize(3).containsExactlyInAnyOrder("user1", "friend1", "friend2");
    }

    @Test
    @DisplayName("getCarbonLeaderboard should return a list of achievements sorted by carbon saved")
    void getCarbonLeaderboard_ShouldReturnSortedByCarbon() {
        // ARRANGE
        String userId = "user1";
        List<AchievementEntity> achievements = List.of(userAchievement, friend1Achievement, friend2Achievement);

        // Mock the repository call within getFriendsIds
        when(friendRelationshipRepository.findByIdUniqueIdOrIdFriendUniqueIdAndStatus(userId, userId, "ACCEPTED"))
                .thenReturn(List.of(friendship1, friendship2));

        // Mock the jdbcTemplate query for the list of achievements
        when(jdbcTemplate.query(anyString(), any(Object[].class), any(RowMapper.class)))
                .thenReturn(achievements);

        // --- START OF FIX ---
        // Correctly mock the 3-argument version of queryForObject for each user ID
        String sql = "SELECT name FROM UserInfo WHERE user_id = ?";
        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{"user1"}), eq(String.class))).thenReturn("User One");
        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{"friend1"}), eq(String.class))).thenReturn("Friend One");
        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{"friend2"}), eq(String.class))).thenReturn("Friend Two");
        // --- END OF FIX ---

        // ACT
        List<AchievementDTO> carbonLeaderboard = leaderboardService.getCarbonLeaderboard(userId);

        // ASSERT
        assertThat(carbonLeaderboard).hasSize(3);
        // Verify it is sorted by totalCarbonSaved in descending order (200, 150, 100)
        assertThat(carbonLeaderboard)
                .extracting(AchievementDTO::getTotalCarbonSaved)
                .containsExactly(200, 150, 100);

        // Verify that user names are correctly mapped
        assertThat(carbonLeaderboard)
                .extracting(AchievementDTO::getUserName)
                .containsExactly("Friend One", "User One", "Friend Two");
    }

    @Test
    @DisplayName("getCaloriesLeaderboard should return a list of achievements sorted by calories burnt")
    void getCaloriesLeaderboard_ShouldReturnSortedByCalories() {
        // ARRANGE
        String userId = "user1";
        List<AchievementEntity> achievements = List.of(userAchievement, friend1Achievement, friend2Achievement);

        // Mock the repository call within getFriendsIds
        when(friendRelationshipRepository.findByIdUniqueIdOrIdFriendUniqueIdAndStatus(userId, userId, "ACCEPTED"))
                .thenReturn(List.of(friendship1, friendship2));

        // Mock the jdbcTemplate query for the list of achievements
        when(jdbcTemplate.query(anyString(), any(Object[].class), any(RowMapper.class)))
                .thenReturn(achievements);

        // --- START OF FIX ---
        // Correctly mock the 3-argument version of queryForObject for each user ID
        String sql = "SELECT name FROM UserInfo WHERE user_id = ?";
        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{"user1"}), eq(String.class))).thenReturn("User One");
        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{"friend1"}), eq(String.class))).thenReturn("Friend One");
        when(jdbcTemplate.queryForObject(eq(sql), eq(new Object[]{"friend2"}), eq(String.class))).thenReturn("Friend Two");
        // --- END OF FIX ---

        // ACT
        List<AchievementDTO> caloriesLeaderboard = leaderboardService.getCaloriesLeaderboard(userId);

        // ASSERT
        assertThat(caloriesLeaderboard).hasSize(3);
        // Verify it is sorted by totalCalorieBurnt in descending order (3000, 2500, 2200)
        assertThat(caloriesLeaderboard)
                .extracting(AchievementDTO::getTotalCalorieBurnt)
                .containsExactly(3000, 2500, 2200);

        // Verify that user names are correctly mapped
        assertThat(caloriesLeaderboard)
                .extracting(AchievementDTO::getUserName)
                .containsExactly("Friend Two", "User One", "Friend One");
    }

    @Test
    @DisplayName("getUserNameById should return the correct name for a given user ID")
    void getUserNameById_ShouldReturnCorrectName() {
        // ARRANGE
        String userId = "testUser";
        String expectedName = "Test Name";
        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), eq(String.class)))
                .thenReturn(expectedName);

        // ACT
        String actualName = leaderboardService.getUserNameById(userId);

        // ASSERT
        assertThat(actualName).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("getFriendsIds when user has no friends should return a list with only the user's ID")
    void getFriendsIds_WhenUserHasNoFriends_ShouldReturnOnlySelf() {
        // ARRANGE
        String userId = "user1";
        // Simulate the repository finding no friend relationships
        when(friendRelationshipRepository.findByIdUniqueIdOrIdFriendUniqueIdAndStatus(userId, userId, "ACCEPTED"))
                .thenReturn(List.of());

        // ACT
        List<String> friendsIds = leaderboardService.getFriendsIds(userId);

        // ASSERT
        // The list should contain only the user themselves, as per the logic `friendsIds.add(userId)`
        assertThat(friendsIds).hasSize(1).containsExactly("user1");
    }

    @Test
    @DisplayName("getCarbonLeaderboard when no achievements exist should return an empty list")
    void getCarbonLeaderboard_WhenNoAchievementsExist_ShouldReturnEmptyList() {
        // ARRANGE
        String userId = "user1";
        // Simulate the user having friends
        when(friendRelationshipRepository.findByIdUniqueIdOrIdFriendUniqueIdAndStatus(userId, userId, "ACCEPTED"))
                .thenReturn(List.of(friendship1, friendship2));

        // Simulate the database returning no achievement records for those friends
        when(jdbcTemplate.query(anyString(), any(Object[].class), any(RowMapper.class)))
                .thenReturn(List.of());

        // ACT
        List<AchievementDTO> carbonLeaderboard = leaderboardService.getCarbonLeaderboard(userId);

        // ASSERT
        // The service should handle this gracefully and return an empty list
        assertThat(carbonLeaderboard).isNotNull();
        assertThat(carbonLeaderboard).isEmpty();
    }

    @Test
    @DisplayName("getCaloriesLeaderboard when no achievements exist should return an empty list")
    void getCaloriesLeaderboard_WhenNoAchievementsExist_ShouldReturnEmptyList() {
        // ARRANGE
        String userId = "user1";
        // Simulate the user having friends
        when(friendRelationshipRepository.findByIdUniqueIdOrIdFriendUniqueIdAndStatus(userId, userId, "ACCEPTED"))
                .thenReturn(List.of(friendship1, friendship2));

        // Simulate the database returning no achievement records for those friends
        when(jdbcTemplate.query(anyString(), any(Object[].class), any(RowMapper.class)))
                .thenReturn(List.of());

        // ACT
        List<AchievementDTO> caloriesLeaderboard = leaderboardService.getCaloriesLeaderboard(userId);

        // ASSERT
        // The service should handle this gracefully and return an empty list
        assertThat(caloriesLeaderboard).isNotNull();
        assertThat(caloriesLeaderboard).isEmpty();
    }
}