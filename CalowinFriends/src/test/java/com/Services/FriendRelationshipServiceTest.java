package com.Services;

import com.ENUM.FriendStatus;
import com.dto.FriendRelationshipDTO;
import com.models.FriendRelationship;
import com.models.FriendRelationshipId;
import com.repository.FriendRelationshipRepository;
import com.service.FriendRelationshipService;
import com.service.UserInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendRelationshipServiceTest {

    @Mock
    private FriendRelationshipRepository repository;

    @Mock
    private UserInfoService userInfoService;

    @InjectMocks
    private FriendRelationshipService friendRelationshipService;

    private String userA_Id;
    private String userB_Id;
    private String userA_Name;
    private String userB_Name;

    @BeforeEach
    void setUp() {
        userA_Id = "userA";
        userB_Id = "userB";
        userA_Name = "User A Name";
        userB_Name = "User B Name";

        // Setup common mock behavior for converting to DTO
        lenient().when(userInfoService.getUserNameById(userA_Id)).thenReturn(userA_Name);
        lenient().when(userInfoService.getUserNameById(userB_Id)).thenReturn(userB_Name);
    }

    // Helper method to create relationship objects
    private FriendRelationship createRelationship(String senderId, String receiverId, String status) {
        FriendRelationship rel = new FriendRelationship();
        rel.setId(new FriendRelationshipId(senderId, receiverId));
        rel.setStatus(status);
        rel.setFriendedOn(LocalDateTime.now());
        return rel;
    }

    @Test
    @DisplayName("sendFriendRequest should succeed when no existing request")
    void sendFriendRequest_Success() {
        // ARRANGE
        FriendRelationshipId id = new FriendRelationshipId(userA_Id, userB_Id);
        FriendRelationshipId reverseId = new FriendRelationshipId(userB_Id, userA_Id);
        FriendRelationship pendingRequest = createRelationship(userA_Id, userB_Id, "PENDING");

        when(repository.existsById(id)).thenReturn(false);
        when(repository.existsById(reverseId)).thenReturn(false);
        when(repository.save(any(FriendRelationship.class))).thenReturn(pendingRequest);

        // ACT
        FriendRelationshipDTO result = friendRelationshipService.sendFriendRequest(userA_Id, userB_Id);

        // ASSERT
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("PENDING");
        // --- THIS LINE IS CORRECTED ---
        assertThat(result.getFriendUserId()).isEqualTo(userB_Id);
        verify(repository).save(any(FriendRelationship.class));
    }

    @Test
    @DisplayName("sendFriendRequest should throw exception if request already exists")
    void sendFriendRequest_ThrowsExceptionWhenExists() {
        // ARRANGE
        FriendRelationshipId id = new FriendRelationshipId(userA_Id, userB_Id);
        when(repository.existsById(id)).thenReturn(true);

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> {
            friendRelationshipService.sendFriendRequest(userA_Id, userB_Id);
        });
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("getPendingRequests should return a list of pending requests for a user")
    void getPendingRequests_ReturnsPendingList() {
        // ARRANGE
        FriendRelationship pendingFromB = createRelationship(userB_Id, userA_Id, "PENDING");
        when(repository.findByIdFriendUniqueIdAndStatus(userA_Id, "PENDING")).thenReturn(List.of(pendingFromB));

        // ACT
        List<FriendRelationshipDTO> requests = friendRelationshipService.getPendingRequests(userA_Id);

        // ASSERT
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getUserId()).isEqualTo(userB_Id);
    }

    @Test
    @DisplayName("acceptFriendRequest should update status to ACCEPTED")
    void acceptFriendRequest_UpdatesStatus() {
        // ARRANGE
        FriendRelationship pendingRequest = createRelationship(userA_Id, userB_Id, "PENDING");
        FriendRelationship acceptedRequest = createRelationship(userA_Id, userB_Id, "ACCEPTED");

        when(repository.findById(any(FriendRelationshipId.class))).thenReturn(Optional.of(pendingRequest));
        when(repository.save(any(FriendRelationship.class))).thenReturn(acceptedRequest);

        // ACT
        FriendRelationshipDTO result = friendRelationshipService.acceptFriendRequest(userA_Id, userB_Id);

        // ASSERT
        assertThat(result.getStatus()).isEqualTo("ACCEPTED");
        verify(repository).save(any(FriendRelationship.class));
    }

    @Test
    @DisplayName("getFriendList should return a list of accepted friends")
    void getFriendList_ReturnsAcceptedFriends() {
        // ARRANGE
        FriendRelationship acceptedFriendship = createRelationship(userA_Id, userB_Id, "ACCEPTED");
        when(repository.findByIdUniqueIdOrIdFriendUniqueIdAndStatus(userA_Id, userA_Id, "ACCEPTED")).thenReturn(List.of(acceptedFriendship));

        // ACT
        List<FriendRelationshipDTO> friendList = friendRelationshipService.getFriendList(userA_Id);

        // ASSERT
        assertThat(friendList).hasSize(1);
        assertThat(friendList.get(0).getFriendUserId()).isEqualTo(userB_Id);
        assertThat(friendList.get(0).getStatus()).isEqualTo("ACCEPTED");
    }

    @Test
    @DisplayName("getRelationshipStatus should return FRIEND for an accepted relationship")
    void getRelationshipStatus_ReturnsFriend() {
        // ARRANGE
        FriendRelationship accepted = createRelationship(userA_Id, userB_Id, "ACCEPTED");
        when(repository.findById(new FriendRelationshipId(userA_Id, userB_Id))).thenReturn(Optional.of(accepted));
        when(repository.findById(new FriendRelationshipId(userB_Id, userA_Id))).thenReturn(Optional.empty());

        // ACT
        FriendStatus status = friendRelationshipService.getRelationshipStatus(userA_Id, userB_Id);

        // ASSERT
        assertThat(status).isEqualTo(FriendStatus.FRIEND);
    }

    @Test
    @DisplayName("getRelationshipStatus should return REQUESTSENT for a pending relationship")
    void getRelationshipStatus_ReturnsRequestSent() {
        // ARRANGE
        FriendRelationship pending = createRelationship(userA_Id, userB_Id, "PENDING");
        when(repository.findById(new FriendRelationshipId(userA_Id, userB_Id))).thenReturn(Optional.of(pending));
        when(repository.findById(new FriendRelationshipId(userB_Id, userA_Id))).thenReturn(Optional.empty());

        // ACT
        FriendStatus status = friendRelationshipService.getRelationshipStatus(userA_Id, userB_Id);

        // ASSERT
        assertThat(status).isEqualTo(FriendStatus.REQUESTSENT);
    }

    @Test
    @DisplayName("removeFriend should delete the relationship")
    void removeFriend_DeletesRelationship() {
        // ARRANGE
        FriendRelationship accepted = createRelationship(userA_Id, userB_Id, "ACCEPTED");
        when(repository.findById(new FriendRelationshipId(userA_Id, userB_Id))).thenReturn(Optional.of(accepted));

        // ACT
        friendRelationshipService.removeFriend(userA_Id, userB_Id);

        // ASSERT
        verify(repository).delete(accepted);
    }

    @Test
    @DisplayName("removeFriend should throw exception if no friendship is found")
    void removeFriend_ThrowsExceptionWhenNotFound() {
        // ARRANGE
        when(repository.findById(any(FriendRelationshipId.class))).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> {
            friendRelationshipService.removeFriend(userA_Id, userB_Id);
        });
        verify(repository, never()).delete(any());
    }


    @Test
    @DisplayName("rejectFriendRequest should update status to REJECTED")
    void rejectFriendRequest_UpdatesStatus() {
        // ARRANGE
        FriendRelationship pendingRequest = createRelationship(userA_Id, userB_Id, "PENDING");
        FriendRelationship rejectedRequest = createRelationship(userA_Id, userB_Id, "REJECTED");

        when(repository.findById(any(FriendRelationshipId.class))).thenReturn(Optional.of(pendingRequest));
        when(repository.save(any(FriendRelationship.class))).thenReturn(rejectedRequest);

        // ACT
        FriendRelationshipDTO result = friendRelationshipService.rejectFriendRequest(userA_Id, userB_Id);

        // ASSERT
        assertThat(result.getStatus()).isEqualTo("REJECTED");
        verify(repository).save(any(FriendRelationship.class));
    }

    @Test
    @DisplayName("respondToRequest should throw exception if request is not pending")
    void respondToRequest_ThrowsExceptionIfNotPending() {
        // ARRANGE
        FriendRelationship acceptedRequest = createRelationship(userA_Id, userB_Id, "ACCEPTED");
        when(repository.findById(any(FriendRelationshipId.class))).thenReturn(Optional.of(acceptedRequest));

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> {
            friendRelationshipService.respondToRequest(userA_Id, userB_Id, "ACCEPTED");
        });
    }

    @Test
    @DisplayName("cancelFriendRequest should succeed for a pending request")
    void cancelFriendRequest_Success() {
        // ARRANGE
        FriendRelationship pendingRequest = createRelationship(userA_Id, userB_Id, "PENDING");
        when(repository.findById(new FriendRelationshipId(userA_Id, userB_Id))).thenReturn(Optional.of(pendingRequest));

        // ACT
        friendRelationshipService.cancelFriendRequest(userA_Id, userB_Id);

        // ASSERT
        verify(repository).delete(pendingRequest);
    }

    @Test
    @DisplayName("cancelFriendRequest should throw exception if request is not pending")
    void cancelFriendRequest_ThrowsExceptionIfNotPending() {
        // ARRANGE
        FriendRelationship acceptedRequest = createRelationship(userA_Id, userB_Id, "ACCEPTED");
        when(repository.findById(new FriendRelationshipId(userA_Id, userB_Id))).thenReturn(Optional.of(acceptedRequest));

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> {
            friendRelationshipService.cancelFriendRequest(userA_Id, userB_Id);
        });
        verify(repository, never()).delete(any());
    }

    @Test
    @DisplayName("getRelationshipStatus should return REQUESTRECIEVED for an incoming pending request")
    void getRelationshipStatus_ReturnsRequestReceived() {
        // ARRANGE
        FriendRelationship incomingRequest = createRelationship(userB_Id, userA_Id, "PENDING");
        when(repository.findById(new FriendRelationshipId(userA_Id, userB_Id))).thenReturn(Optional.empty());
        when(repository.findById(new FriendRelationshipId(userB_Id, userA_Id))).thenReturn(Optional.of(incomingRequest));

        // ACT
        FriendStatus status = friendRelationshipService.getRelationshipStatus(userA_Id, userB_Id);

        // ASSERT
        assertThat(status).isEqualTo(FriendStatus.REQUESTRECIEVED);
    }

    @Test
    @DisplayName("getRelationshipStatus should return STRANGER when no relationship exists")
    void getRelationshipStatus_ReturnsStranger() {
        // ARRANGE
        when(repository.findById(any(FriendRelationshipId.class))).thenReturn(Optional.empty());

        // ACT
        FriendStatus status = friendRelationshipService.getRelationshipStatus(userA_Id, userB_Id);

        // ASSERT
        assertThat(status).isEqualTo(FriendStatus.STRANGER);
    }

    @Test
    @DisplayName("respondToRequest should throw exception if request is not found")
    void respondToRequest_ThrowsExceptionIfNotFound() {
        // ARRANGE
        when(repository.findById(any(FriendRelationshipId.class))).thenReturn(Optional.empty());

        // ACT & ASSERT
        // This tests the .orElseThrow() in the respondToRequest method
        assertThrows(IllegalArgumentException.class, () -> {
            friendRelationshipService.respondToRequest(userA_Id, userB_Id, "ACCEPTED");
        });
    }

    @Test
    @DisplayName("respondToRequest should throw exception for invalid status")
    void respondToRequest_ThrowsExceptionForInvalidStatus() {
        // ARRANGE
        FriendRelationship pendingRequest = createRelationship(userA_Id, userB_Id, "PENDING");
        when(repository.findById(any(FriendRelationshipId.class))).thenReturn(Optional.of(pendingRequest));

        // ACT & ASSERT
        // This tests the validation for "ACCEPTED" or "REJECTED" status strings
        assertThrows(IllegalArgumentException.class, () -> {
            friendRelationshipService.respondToRequest(userA_Id, userB_Id, "INVALID_STATUS");
        });
    }

    @Test
    @DisplayName("getFriendList should return an empty list for a user with no friends")
    void getFriendList_ReturnsEmptyListForNoFriends() {
        // ARRANGE
        when(repository.findByIdUniqueIdOrIdFriendUniqueIdAndStatus(userA_Id, userA_Id, "ACCEPTED")).thenReturn(List.of());

        // ACT
        List<FriendRelationshipDTO> friendList = friendRelationshipService.getFriendList(userA_Id);

        // ASSERT
        assertThat(friendList).isNotNull();
        assertThat(friendList).isEmpty();
    }

    @Test
    @DisplayName("cancelFriendRequest should throw exception if request is not found")
    void cancelFriendRequest_ThrowsExceptionIfNotFound() {
        // ARRANGE
        when(repository.findById(any(FriendRelationshipId.class))).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> {
            friendRelationshipService.cancelFriendRequest(userA_Id, userB_Id);
        });
    }

    @Test
    @DisplayName("removeFriend should throw exception when trying to remove a non-accepted friend")
    void removeFriend_ThrowsExceptionForNonAcceptedRelationship() {
        // ARRANGE
        // A relationship exists but is not "ACCEPTED"
        FriendRelationship pendingRequest = createRelationship(userA_Id, userB_Id, "PENDING");
        when(repository.findById(new FriendRelationshipId(userA_Id, userB_Id))).thenReturn(Optional.of(pendingRequest));

        // REMOVED: The following line was unnecessary because the service logic finds the relationship
        // on its first attempt and never checks the reverse direction in this scenario.
        // when(repository.findById(new FriendRelationshipId(userB_Id, userA_Id))).thenReturn(Optional.empty());

        // ACT & ASSERT
        // This tests the logic that ensures only "ACCEPTED" relationships can be removed
        assertThrows(IllegalArgumentException.class, () -> {
            friendRelationshipService.removeFriend(userA_Id, userB_Id);
        });
    }

    @Test
    @DisplayName("sendFriendRequest should throw exception if reverse request already exists")
    void sendFriendRequest_ThrowsExceptionWhenReverseExists() {
        // ARRANGE
        FriendRelationshipId id = new FriendRelationshipId(userA_Id, userB_Id);
        FriendRelationshipId reverseId = new FriendRelationshipId(userB_Id, userA_Id);
        when(repository.existsById(id)).thenReturn(false);
        when(repository.existsById(reverseId)).thenReturn(true); // The reverse relationship exists

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> {
            friendRelationshipService.sendFriendRequest(userA_Id, userB_Id);
        });
    }

    @Test
    @DisplayName("getRelationshipStatus should return FRIEND for a reverse accepted relationship")
    void getRelationshipStatus_ReturnsFriendForReverse() {
        // ARRANGE
        FriendRelationship accepted = createRelationship(userB_Id, userA_Id, "ACCEPTED");
        when(repository.findById(new FriendRelationshipId(userA_Id, userB_Id))).thenReturn(Optional.empty());
        when(repository.findById(new FriendRelationshipId(userB_Id, userA_Id))).thenReturn(Optional.of(accepted));

        // ACT
        FriendStatus status = friendRelationshipService.getRelationshipStatus(userA_Id, userB_Id);

        // ASSERT
        assertThat(status).isEqualTo(FriendStatus.FRIEND);
    }

    @Test
    @DisplayName("getRelationshipStatus should return STRANGER if a request was REJECTED")
    void getRelationshipStatus_ReturnsStrangerForRejected() {
        // ARRANGE
        FriendRelationship rejected = createRelationship(userA_Id, userB_Id, "REJECTED");
        when(repository.findById(new FriendRelationshipId(userA_Id, userB_Id))).thenReturn(Optional.of(rejected));
        when(repository.findById(new FriendRelationshipId(userB_Id, userA_Id))).thenReturn(Optional.empty());

        // ACT
        FriendStatus status = friendRelationshipService.getRelationshipStatus(userA_Id, userB_Id);

        // ASSERT
        // This confirms the logic that a REJECTED status defaults to STRANGER
        assertThat(status).isEqualTo(FriendStatus.STRANGER);
    }

    @Test
    @DisplayName("removeFriend should delete a relationship stored in the reverse direction")
    void removeFriend_DeletesReverseRelationship() {
        // ARRANGE
        FriendRelationship reverseAccepted = createRelationship(userB_Id, userA_Id, "ACCEPTED");
        when(repository.findById(new FriendRelationshipId(userA_Id, userB_Id))).thenReturn(Optional.empty());
        when(repository.findById(new FriendRelationshipId(userB_Id, userA_Id))).thenReturn(Optional.of(reverseAccepted));

        // ACT
        friendRelationshipService.removeFriend(userA_Id, userB_Id);

        // ASSERT
        verify(repository).delete(reverseAccepted);
    }
}