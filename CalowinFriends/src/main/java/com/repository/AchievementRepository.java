package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.models.AchievementEntity;

public interface AchievementRepository extends JpaRepository<AchievementEntity, String> {

}