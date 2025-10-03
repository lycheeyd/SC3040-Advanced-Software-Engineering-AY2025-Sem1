package com;

import com.Relationship.Managers.FriendRelationshipService;
import com.Database.CalowinDB.FriendRelationshipRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

// Use the Mockito extension for JUnit 5
@ExtendWith(MockitoExtension.class)
class FriendRelationshipServiceTest {

    // Create a mock of the repository dependency
    @Mock
    private FriendRelationshipRepository friendRelationshipRepository;

    // Create an instance of the service and inject the mocks into it
    @InjectMocks
    private FriendRelationshipService friendRelationshipService;

    @Test
    @DisplayName("getFriendRequestsForUser should return a list of user IDs when requests are found")
    void getFriendRequestsForUser_whenRequestsExist_shouldReturnListOfIds() {
        // Arrange
        String userId = "user001";
        List<String> expectedRequestIds = List.of("user002", "user003");

        // Configure the mock repository to return our sample list when the method is called
        when(friendRelationshipRepository.findPendingFriendRequests(userId)).thenReturn(expectedRequestIds);

        // Act
        List<String> actualRequestIds = friendRelationshipService.getFriendRequestsForUser(userId);

        // Assert
        assertThat(actualRequestIds).isNotNull();
        assertThat(actualRequestIds).hasSize(2);
        assertThat(actualRequestIds).containsExactly("user002", "user003");
    }

    @Test
    @DisplayName("getFriendRequestsForUser should return an empty list when no requests are found")
    void getFriendRequestsForUser_whenNoRequestsExist_shouldReturnEmptyList() {
        // Arrange
        String userId = "user999";

        // Configure the mock repository to return an empty list
        when(friendRelationshipRepository.findPendingFriendRequests(userId)).thenReturn(Collections.emptyList());

        // Act
        List<String> actualRequestIds = friendRelationshipService.getFriendRequestsForUser(userId);

        // Assert
        assertThat(actualRequestIds).isNotNull();
        assertThat(actualRequestIds).isEmpty();
    }
}