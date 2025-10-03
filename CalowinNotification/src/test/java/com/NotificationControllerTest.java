package com;

import com.Relationship.Managers.FriendRelationshipService;
import com.Relationship.NotificationController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Use @WebMvcTest to load only the web layer and test the NotificationController
@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    // Spring Test provides MockMvc to perform fake HTTP requests
    @Autowired
    private MockMvc mockMvc;

    // @MockBean creates a mock of the service in the Spring application context
    @MockBean
    private FriendRelationshipService friendRelationshipService;

    @Test
    @DisplayName("GET /notifications/friend-requests/{userId} should return 200 OK with a list of IDs")
    void getIncomingFriendRequests_whenRequestsExist_shouldReturn200AndListOfIds() throws Exception {
        // Arrange
        String userId = "user001";
        List<String> friendRequestIds = List.of("user002", "user003");

        // Configure the mock service to return our sample list
        when(friendRelationshipService.getFriendRequestsForUser(userId)).thenReturn(friendRequestIds);

        // Act & Assert
        mockMvc.perform(get("/notifications/friend-requests/{userId}", userId))
                .andExpect(status().isOk()) // Check for HTTP 200 OK status
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Check that the response is JSON
                .andExpect(jsonPath("$").isArray()) // Check that the root of the response is an array
                .andExpect(jsonPath("$.length()").value(2)) // Check the size of the array
                .andExpect(jsonPath("$[0]").value("user002")) // Check the first element
                .andExpect(jsonPath("$[1]").value("user003")); // Check the second element
    }

    @Test
    @DisplayName("GET /notifications/friend-requests/{userId} should return 200 OK with an empty list")
    void getIncomingFriendRequests_whenNoRequestsExist_shouldReturn200AndEmptyList() throws Exception {
        // Arrange
        String userId = "user999";

        // Configure the mock service to return an empty list
        when(friendRelationshipService.getFriendRequestsForUser(userId)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/notifications/friend-requests/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0)); // Check that the array is empty
    }

}