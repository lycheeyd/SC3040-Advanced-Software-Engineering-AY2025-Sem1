package com.Database.CalowinDB;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Account.Entities.AchievementEntry;

@Repository
public interface AchievementRepository extends JpaRepository<AchievementEntry, String> {
    Optional<AchievementEntry> findByUserID(String userID);
    void deleteByUserID(String userID);
}