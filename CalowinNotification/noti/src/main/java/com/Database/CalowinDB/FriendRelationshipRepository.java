package com.Database.CalowinDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Relationship.Entity.FriendRelationship;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface FriendRelationshipRepository extends JpaRepository<FriendRelationship,String>{

    //List<FriendRelationship> findByFriendUniqueIdAndStatus(String friendUniqueId, String status);
 
    @Query(value = "SELECT Friend_Unique_ID FROM FriendRelationship WHERE Friend_Unique_ID = :userId AND status = 'REQUESTSENT'", nativeQuery = true)
    List<String> findPendingFriendRequests(@Param("userId") String userId);

    // List<FriendRelationship> findPendingFriendRequests(@Param("userId") String userId);

    //List<FriendRelationship> findAllRecords();

}

