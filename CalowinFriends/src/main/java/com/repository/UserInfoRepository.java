package com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.models.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, String> {

    // Find users by either user_id or name (case-insensitive search)
    @Query("SELECT u FROM UserInfo u WHERE LOWER(u.userId) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<UserInfo> searchByUserIdOrName(@Param("searchTerm") String searchTerm);
}
