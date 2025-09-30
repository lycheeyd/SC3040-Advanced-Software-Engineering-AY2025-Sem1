package com.Relationship.Managers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import com.Database.CalowinDB.FriendRelationshipRepository;

import java.util.List;
import java.util.Map;

@Service
public class FriendRelationshipService {

    @Autowired
    @Qualifier("calowinDBTransactionManager")
    private PlatformTransactionManager calowinDBTransactionManager;

    @Autowired
    private FriendRelationshipRepository friendRelationshipRepository;

    public List<String> getFriendRequestsForUser(String userId) {
        // Call the repository to execute the query based on userId
        return friendRelationshipRepository.findPendingFriendRequests(userId);
    }
}
