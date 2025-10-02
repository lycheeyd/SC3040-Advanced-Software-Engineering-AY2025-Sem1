package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Entity.AchievementEntity;

public interface AchievementRepository extends JpaRepository<AchievementEntity, String> {

}