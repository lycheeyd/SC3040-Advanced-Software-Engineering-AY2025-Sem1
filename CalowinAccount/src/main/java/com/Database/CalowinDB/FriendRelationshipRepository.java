package com.Database.CalowinDB;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Account.Entities.FriendRelationshipEntry;

@Repository
public interface FriendRelationshipRepository extends JpaRepository<FriendRelationshipEntry, String> {
    Optional<FriendRelationshipEntry> findByUserID(String userID);

    @Modifying
    @Query("DELETE FROM FriendRelationshipEntry f WHERE f.userID = :userID")
    void deleteByUserID(@Param("userID") String userID);
}