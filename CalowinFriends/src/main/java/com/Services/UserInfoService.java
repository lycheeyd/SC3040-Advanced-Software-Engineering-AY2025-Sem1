package com.Services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Entity.UserInfoEntity;
import com.repository.UserInfoRepository;

@Service
public class UserInfoService {

    @Autowired
    private UserInfoRepository userInfoRepository;

    /**
     * Searches for users by user ID or name, excluding the current user from the
     * results.
     * 
     * @param searchTerm    the search term (user ID or name)
     * @param currentUserId the ID of the current user
     * @return a list of UserInfo matching the search term, excluding the current
     *         user
     */
    public List<UserInfoEntity> searchByUserIdOrName(String searchTerm, String currentUserId) {
        // Return an empty list if the search term is empty
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }

        return userInfoRepository.searchByUserIdOrName(searchTerm).stream()
                .filter(userInfo -> !userInfo.getUserId().equals(currentUserId)) // Exclude the current user
                .collect(Collectors.toList());
    }

    /**
     * Fetches the UserInfo object by user ID.
     * 
     * @param userId the ID of the user
     * @return the UserInfo object, or null if not found
     */
    public UserInfoEntity getUserInfoById(String userId) {
        Optional<UserInfoEntity> userInfoOptional = userInfoRepository.findById(userId);
        return userInfoOptional.orElse(null);
    }

    /**
     * Fetches the user's name based on the user ID.
     * 
     * @param userId the ID of the user
     * @return the name of the user, or null if not found
     */
    public String getUserNameById(String userId) {
        Optional<UserInfoEntity> userInfoOptional = userInfoRepository.findById(userId);
        return userInfoOptional.map(UserInfoEntity::getName).orElse(null);
    }
}
