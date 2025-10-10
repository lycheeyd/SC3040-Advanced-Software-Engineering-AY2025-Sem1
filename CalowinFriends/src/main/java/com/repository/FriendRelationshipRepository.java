package com.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.models.FriendRelationship;
import com.models.FriendRelationshipId;

@Repository
public interface FriendRelationshipRepository extends JpaRepository<FriendRelationship, FriendRelationshipId> {

    // Check if a friend relationship exists with the composite key
    boolean existsById(FriendRelationshipId id);

    // Find relationships by FriendUniqueId and status (inside composite key)
    List<FriendRelationship> findByIdFriendUniqueIdAndStatus(String friendUniqueId, String status);

    // Find a friend relationship by the composite key
    Optional<FriendRelationship> findById(FriendRelationshipId id);

    // Find relationships by either UniqueId or FriendUniqueId and status (using
    // composite key fields)
    List<FriendRelationship> findByIdUniqueIdOrIdFriendUniqueIdAndStatus(String uniqueId, String friendUniqueId,
            String status);

    List<FriendRelationship> findByIdUniqueIdOrIdFriendUniqueId(String uniqueId, String friendUniqueId);

}
