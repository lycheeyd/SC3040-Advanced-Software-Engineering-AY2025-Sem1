package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.models.UserInfoEntity;
import com.service.UserInfoService;

@RestController
@RequestMapping("/api/users")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * Endpoint to search for users by user ID or name, excluding the current user from the results.
     * 
     * @param searchTerm the term to search by (user ID or name)
     * @param currentUserId the ID of the current user (to be excluded from results)
     * @return a list of matching users excluding the current user
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserInfoEntity>> searchUsers(@RequestParam String searchTerm, @RequestParam String currentUserId) {
        List<UserInfoEntity> results = userInfoService.searchByUserIdOrName(searchTerm, currentUserId);
        return ResponseEntity.ok(results);
    }
}
