package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.models.Achievement;

public interface AchievementRepository extends JpaRepository<Achievement, String> {

}