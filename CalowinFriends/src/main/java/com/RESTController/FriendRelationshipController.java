package com.RESTController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Entity.FriendStatus;
import com.DataTransferObject.FriendRelationshipDTO;
import com.Services.FriendRelationshipService;

@RestController
@RequestMapping("/friend-requests")
public class FriendRelationshipController {

    @Autowired
    private FriendRelationshipService friendService;

    // Send Friend Request
    @PostMapping("/send")
    public ResponseEntity<FriendRelationshipDTO> sendRequest(@RequestParam String senderId,
            @RequestParam String receiverId) {
        try {
            FriendRelationshipDTO result = friendService.sendFriendRequest(senderId, receiverId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // View Pending Friend Requests
    @GetMapping("/pending/{receiverId}")
    public ResponseEntity<List<FriendRelationshipDTO>> getPendingRequests(@PathVariable String receiverId) {
        try {
            List<FriendRelationshipDTO> dtoList = friendService.getPendingRequests(receiverId);
            return ResponseEntity.ok(dtoList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Respond to Friend Request (Accept or Reject)
    @PostMapping("/respond")
    public ResponseEntity<FriendRelationshipDTO> respondToRequest(
            @RequestParam String senderId,
            @RequestParam String receiverId,
            @RequestParam String status) {
        try {
            FriendRelationshipDTO result = friendService.respondToRequest(senderId, receiverId, status);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Cancel Friend Request
    @PostMapping("/cancel")
    public ResponseEntity<Void> cancelFriendRequest(@RequestParam String senderId, @RequestParam String receiverId) {
        try {
            friendService.cancelFriendRequest(senderId, receiverId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Accept Friend Request
    @PostMapping("/accept")
    public ResponseEntity<FriendRelationshipDTO> acceptFriendRequest(@RequestParam String senderId,
            @RequestParam String receiverId) {
        try {
            FriendRelationshipDTO result = friendService.acceptFriendRequest(senderId, receiverId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Reject Friend Request
    @PostMapping("/reject")
    public ResponseEntity<FriendRelationshipDTO> rejectFriendRequest(@RequestParam String senderId,
            @RequestParam String receiverId) {
        try {
            FriendRelationshipDTO result = friendService.rejectFriendRequest(senderId, receiverId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Remove Friend
    @PostMapping("/remove")
    public ResponseEntity<Void> removeFriend(@RequestParam String userId, @RequestParam String friendId) {
        try {
            friendService.removeFriend(userId, friendId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // View Friend List
    @GetMapping("/friends/{userId}")
    public ResponseEntity<List<FriendRelationshipDTO>> getFriendList(@PathVariable String userId) {
        try {
            List<FriendRelationshipDTO> dtoList = friendService.getFriendList(userId);
            return ResponseEntity.ok(dtoList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get Relationship Status between two users
    @GetMapping("/status")
    public ResponseEntity<FriendStatus> getRelationshipStatus(
            @RequestParam("userId1") String userId1,
            @RequestParam("userId2") String userId2) {

        try {
            FriendStatus status = friendService.getRelationshipStatus(userId1, userId2);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
