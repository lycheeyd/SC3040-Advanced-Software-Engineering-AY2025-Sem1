package com.Database.CalowinDB;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Account.Entities.TripsEntry;

@Repository
public interface TripsRepository extends JpaRepository<TripsEntry, String> {
    Optional<TripsEntry> findByUserID(String userID);

    @Modifying
    @Query("DELETE FROM TripsEntry t WHERE t.userID = :userID")
    void deleteByUserID(@Param("userID") String userID);
}
    