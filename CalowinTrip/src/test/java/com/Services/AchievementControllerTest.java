package com.Services;

import com.DataTransferObject.AchievementResponseDTO;
import com.controller.AchievementController;
import com.service.AchievementService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AchievementController.class)
class AchievementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AchievementService achievementService;

    @Test
    @DisplayName("GET /achievements/progress should return user's progress")
    void getAchievementProgress_shouldReturnDTO() throws Exception {
        // Arrange
        String userId = "user123";
        AchievementResponseDTO responseDTO = new AchievementResponseDTO(
                1500, 5500, "EcoBronze", "CalorieSilver");

        when(achievementService.getAchievementProgress(userId)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/achievements/progress")
                        .param("userId", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCarbonSavedExp", is(1500)))
                .andExpect(jsonPath("$.carbonSavedMedal", is("EcoBronze")))
                .andExpect(jsonPath("$.calorieBurntMedal", is("CalorieSilver")));
    }

    @Test
    @DisplayName("POST /achievements/addTripMetrics should call service")
    void addTripMetrics_shouldCallService() throws Exception {
        // Arrange
        String userId = "user123";
        int carbon = 100;
        int calories = 200;

        // Act & Assert
        mockMvc.perform(post("/achievements/addTripMetrics")
                        .param("userId", userId)
                        .param("carbonSaved", String.valueOf(carbon))
                        .param("caloriesBurnt", String.valueOf(calories)))
                .andExpect(status().isOk());

        // Verify the service method was called with the correct parameters
        verify(achievementService).addTripMetricsToAchievement(userId, carbon, calories);
    }
}