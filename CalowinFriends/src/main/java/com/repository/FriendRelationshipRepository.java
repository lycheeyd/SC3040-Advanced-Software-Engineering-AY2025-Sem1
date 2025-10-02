package com.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Entity.FriendRelationshipEntity;
import com.Entity.FriendRelationshipIdEntity;
@Repository
public interface FriendRelationshipRepository extends JpaRepository<FriendRelationshipEntity, FriendRelationshipIdEntity> {

    // Check if a friend relationship exists with the composite key
    boolean existsById(FriendRelationshipIdEntity id);

    // Find relationships by FriendUniqueId and status (inside composite key)
    List<FriendRelationshipEntity> findByIdFriendUniqueIdAndStatus(String friendUniqueId, String status);

    // Find a friend relationship by the composite key
    Optional<FriendRelationshipEntity> findById(FriendRelationshipIdEntity id);
    
    // Find relationships by either UniqueId or FriendUniqueId and status (using composite key fields)
    List<FriendRelationshipEntity> findByIdUniqueIdOrIdFriendUniqueIdAndStatus(String uniqueId, String friendUniqueId, String status);

    List<FriendRelationshipEntity> findByIdUniqueIdOrIdFriendUniqueId(String uniqueId, String friendUniqueId);

}
