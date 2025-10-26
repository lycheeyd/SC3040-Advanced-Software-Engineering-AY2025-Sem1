package com.Services;


import com.model.Achievement;
import com.repository.AchievementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AchievementRepositoryTest {

    @Mock
    private DataSource dataSource;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement fetchStmt;
    @Mock
    private PreparedStatement updateStmt;
    @Mock
    private PreparedStatement insertStmt;
    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private AchievementRepository achievementRepository;

    @BeforeEach
    void setUp() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
    }

    @Test
    @DisplayName("fetchAchievementForUser should return achievement when found")
    void fetchAchievement_whenUserFound_shouldReturnAchievement() throws SQLException {
        // Arrange
        when(connection.prepareStatement("SELECT * FROM achievement WHERE user_id = ?")).thenReturn(fetchStmt);
        when(fetchStmt.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("total_carbon_saved")).thenReturn(100);
        when(resultSet.getInt("total_calorie_burnt")).thenReturn(200);
        when(resultSet.getString("carbon_medal")).thenReturn("EcoBronze");
        when(resultSet.getString("calorie_medal")).thenReturn("CalorieBronze");

        // Act
        Optional<Achievement> result = achievementRepository.fetchAchievementForUser("user123");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getTotalCarbonSavedExp()).isEqualTo(100);
        assertThat(result.get().getCarbonSavedMedal()).isEqualTo("EcoBronze");
    }

    @Test
    @DisplayName("fetchAchievementForUser should return empty when not found")
    void fetchAchievement_whenUserNotFound_shouldReturnEmpty() throws SQLException {
        // Arrange
        when(connection.prepareStatement("SELECT * FROM achievement WHERE user_id = ?")).thenReturn(fetchStmt);
        when(fetchStmt.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // Act
        Optional<Achievement> result = achievementRepository.fetchAchievementForUser("user123");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("updateAchievement should UPDATE when record exists")
    void updateAchievement_whenRecordExists_shouldUpdate() throws SQLException {
        // Arrange
        Achievement achievement = new Achievement();
        achievement.setTotalCarbonSavedExp(150);
        achievement.setTotalCalorieBurntExp(250);
        achievement.setCarbonSavedMedal("EcoBronze");
        achievement.setCalorieBurntMedal("CalorieBronze");

        when(connection.prepareStatement(startsWith("UPDATE"))).thenReturn(updateStmt);
        when(updateStmt.executeUpdate()).thenReturn(1); // 1 row affected

        // Act
        achievementRepository.updateAchievement(achievement, "user123");

        // Assert
        verify(updateStmt).setInt(1, 150);
        verify(updateStmt).setInt(2, 250);
        verify(updateStmt).setString(3, "EcoBronze");
        verify(updateStmt).setString(4, "CalorieBronze");
        verify(updateStmt).setString(5, "user123");
        verify(updateStmt).executeUpdate();
        verify(connection, never()).prepareStatement(startsWith("INSERT"));
    }

    @Test
    @DisplayName("updateAchievement should INSERT when record does not exist")
    void updateAchievement_whenRecordNotExists_shouldInsert() throws SQLException {
        // Arrange
        Achievement achievement = new Achievement();
        achievement.setTotalCarbonSavedExp(50);
        achievement.setTotalCalorieBurntExp(75);
        achievement.setCarbonSavedMedal("No Medal");
        achievement.setCalorieBurntMedal("No Medal");

        when(connection.prepareStatement(startsWith("UPDATE"))).thenReturn(updateStmt);
        when(updateStmt.executeUpdate()).thenReturn(0); // 0 rows affected
        when(connection.prepareStatement(startsWith("INSERT"))).thenReturn(insertStmt);

        // Act
        achievementRepository.updateAchievement(achievement, "newUser");

        // Assert
        verify(updateStmt).executeUpdate(); // Verify UPDATE was tried first

        verify(insertStmt).setString(1, "newUser");
        verify(insertStmt).setInt(2, 50);
        verify(insertStmt).setInt(3, 75);
        verify(insertStmt).setString(4, "No Medal");
        verify(insertStmt).setString(5, "No Medal");
        verify(insertStmt).executeUpdate(); // Verify INSERT was called
    }
}