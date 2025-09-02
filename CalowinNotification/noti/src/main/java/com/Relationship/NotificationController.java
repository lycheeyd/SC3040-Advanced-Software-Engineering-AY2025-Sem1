package com.Relationship;

import com.Relationship.Managers.FriendRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
//import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final FriendRelationshipService friendRelationshipService;

    @Autowired
    public NotificationController(FriendRelationshipService friendRelationshipService) {
        this.friendRelationshipService = friendRelationshipService;
    }

    @GetMapping("/friend-requests/{userId}")
    public List<String> getIncomingFriendRequests(@PathVariable("userId") String userId) {
        return friendRelationshipService.getFriendRequestsForUser(userId);
    }
}
