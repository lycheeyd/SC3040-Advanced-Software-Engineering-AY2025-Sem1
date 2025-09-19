package com.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ENUM.FriendStatus;
import com.dto.FriendRelationshipDTO;
import com.models.FriendRelationship;
import com.models.FriendRelationshipId;
import com.repository.FriendRelationshipRepository;

import jakarta.transaction.Transactional;

@Service
public class FriendRelationshipService {

    @Autowired
    private FriendRelationshipRepository repository;

    @Autowired
    private UserInfoService userInfoService;

    private FriendRelationshipDTO convertToDTO(FriendRelationship relationship) {
        String userName = userInfoService.getUserNameById(relationship.getId().getUniqueId());
        String friendUserName = userInfoService.getUserNameById(relationship.getId().getFriendUniqueId());

        return new FriendRelationshipDTO(
            relationship.getId().getUniqueId(),
            userName,
            relationship.getId().getFriendUniqueId(),
            friendUserName,
            relationship.getStatus()
        );
    }

    @Transactional
    public FriendRelationshipDTO sendFriendRequest(String senderId, String receiverId) {
        FriendRelationshipId id = new FriendRelationshipId(senderId, receiverId);
        FriendRelationshipId reverseId = new FriendRelationshipId(receiverId, senderId);

        // Check if a relationship already exists in either direction
        if (repository.existsById(id) || repository.existsById(reverseId)) {
            throw new IllegalArgumentException("Friend request already exists.");
        }

        // Create and save new friend relationship
        FriendRelationship relationship = new FriendRelationship();
        relationship.setId(id);
        relationship.setFriendedOn(LocalDateTime.now());
        relationship.setStatus("PENDING");

        return convertToDTO(repository.save(relationship));
    }

    public List<FriendRelationshipDTO> getPendingRequests(String userId) {
        // Fetch only requests where the userId is in the Friend_Unique_ID column and status is "PENDING"
        return repository.findByIdFriendUniqueIdAndStatus(userId, "PENDING").stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    

    public FriendRelationshipDTO respondToRequest(String senderId, String receiverId, String status) {
        FriendRelationshipId id = new FriendRelationshipId(senderId, receiverId);

        FriendRelationship relationship = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (!"PENDING".equals(relationship.getStatus())) {
            throw new IllegalArgumentException("Request is not pending.");
        }

        if (!"ACCEPTED".equals(status) && !"REJECTED".equals(status)) {
            throw new IllegalArgumentException("Invalid status. Use 'ACCEPTED' or 'REJECTED'.");
        }

        relationship.setStatus(status);
        return convertToDTO(repository.save(relationship));
    }

    public List<FriendRelationshipDTO> getFriendList(String userId) {
        // Retrieve relationships where the status is explicitly "ACCEPTED"
        return repository.findByIdUniqueIdOrIdFriendUniqueIdAndStatus(userId, userId, "ACCEPTED").stream()
            .filter(relationship -> "ACCEPTED".equals(relationship.getStatus())) // Ensure only accepted relationships
            .map(relationship -> {
                // Determine the friend’s ID based on the direction of the relationship
                String friendId = relationship.getId().getUniqueId().equals(userId) 
                    ? relationship.getId().getFriendUniqueId() 
                    : relationship.getId().getUniqueId();
                
                // Convert to DTO using friend ID and name (prevent adding the userId itself to the friend list)
                return new FriendRelationshipDTO(
                    userId,
                    userInfoService.getUserNameById(userId),
                    friendId,
                    userInfoService.getUserNameById(friendId),
                    relationship.getStatus()
                );
            })
            .distinct() // Ensure no duplicate entries in the list
            .collect(Collectors.toList());
    }
    
    public FriendRelationshipDTO acceptFriendRequest(String senderId, String receiverId) {
        return respondToRequest(senderId, receiverId, "ACCEPTED");
    }

    public FriendRelationshipDTO rejectFriendRequest(String senderId, String receiverId) {
        return respondToRequest(senderId, receiverId, "REJECTED");
    }

    public void cancelFriendRequest(String senderId, String receiverId) {
        FriendRelationshipId id = new FriendRelationshipId(senderId, receiverId);
        FriendRelationship relationship = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));

        if (!"PENDING".equals(relationship.getStatus())) {
            throw new IllegalArgumentException("Cannot cancel a non-pending request.");
        }

        repository.delete(relationship);
    }

    public FriendStatus getRelationshipStatus(String userId1, String userId2) {
        Optional<FriendRelationship> directRelationship = repository.findById(new FriendRelationshipId(userId1, userId2));
        Optional<FriendRelationship> reverseRelationship = repository.findById(new FriendRelationshipId(userId2, userId1));
    
        // If there's an "ACCEPTED" relationship in either direction, they're friends
        if (directRelationship.isPresent() && "ACCEPTED".equals(directRelationship.get().getStatus()) ||
            reverseRelationship.isPresent() && "ACCEPTED".equals(reverseRelationship.get().getStatus())) {
            return FriendStatus.FRIEND;
        }
    
        // Check for pending requests in both directions
        if (directRelationship.isPresent() && "PENDING".equals(directRelationship.get().getStatus())) {
            return FriendStatus.REQUESTSENT;
        } else if (reverseRelationship.isPresent() && "PENDING".equals(reverseRelationship.get().getStatus())) {
            return FriendStatus.REQUESTRECIEVED;
        }
    
        // If there's a "REJECTED" relationship in either direction, they're strangers
        if (directRelationship.isPresent() && "REJECTED".equals(directRelationship.get().getStatus()) ||
            reverseRelationship.isPresent() && "REJECTED".equals(reverseRelationship.get().getStatus())) {
            return FriendStatus.STRANGER;
        }
    
        // Default to STRANGER if no relationship exists
        return FriendStatus.STRANGER;
    }
    

    public void removeFriend(String userId, String friendId) {
        FriendRelationshipId id = new FriendRelationshipId(userId, friendId);
        FriendRelationshipId reverseId = new FriendRelationshipId(friendId, userId);

        Optional<FriendRelationship> relationship = repository.findById(id);

        if (relationship.isEmpty()) {
            relationship = repository.findById(reverseId);
        }

        if (relationship.isPresent() && "ACCEPTED".equals(relationship.get().getStatus())) {
            repository.delete(relationship.get());
        } else {
            throw new IllegalArgumentException("No friendship found to remove.");
        }
    }
}
