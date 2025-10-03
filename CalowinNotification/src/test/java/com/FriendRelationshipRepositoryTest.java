package com;

import com.Relationship.Entity.FriendRelationshipEntity;
import com.Database.CalowinDB.FriendRelationshipRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This is an INTEGRATION TEST for the FriendRelationshipRepository.
 * It uses @SpringBootTest to load the full application context and connect to a database
 * to verify that the custom SQL query works correctly.
 */
@SpringBootTest
@Transactional // Ensures all database operations are rolled back after each test
class FriendRelationshipRepositoryTest {

    // TestEntityManager is a helper provided by Spring Boot for JPA tests.
    // It allows us to set up data in the database before our test runs.
    @Autowired
    private TestEntityManager entityManager;

    // The actual repository bean that we want to test.
    @Autowired
    private FriendRelationshipRepository friendRelationshipRepository;

    @Test
    @DisplayName("findPendingFriendRequests should return only the IDs of users with 'REQUESTSENT' status for the correct user")
    void findPendingFriendRequests_shouldReturnCorrectIds() {
        // --- ARRANGE ---
        // Set up the test data in the database.

        // 1. A pending request FOR our target user ("user123"). This SHOULD be found.
        FriendRelationshipEntity pendingRequest = new FriendRelationshipEntity();
        pendingRequest.setUniqueID("sender001"); // The user who sent the request
        pendingRequest.setFriendUniqueID("user123"); // The user who received the request
        pendingRequest.setStatus("REQUESTSENT");
        pendingRequest.setFriendedOn("2025-10-03");
        entityManager.persist(pendingRequest);

        // 2. An already accepted friend of our target user. This should NOT be found.
        FriendRelationshipEntity acceptedFriend = new FriendRelationshipEntity();
        acceptedFriend.setUniqueID("sender002");
        acceptedFriend.setFriendUniqueID("user123");
        acceptedFriend.setStatus("ACCEPTED");
        acceptedFriend.setFriendedOn("2025-10-01");
        entityManager.persist(acceptedFriend);

        // 3. A pending request for a DIFFERENT user. This should NOT be found.
        FriendRelationshipEntity anotherUserRequest = new FriendRelationshipEntity();
        anotherUserRequest.setUniqueID("sender003");
        anotherUserRequest.setFriendUniqueID("user456"); // A different receiver
        anotherUserRequest.setStatus("REQUESTSENT");
        anotherUserRequest.setFriendedOn("2025-10-03");
        entityManager.persist(anotherUserRequest);

        // Ensure all data is written to the database before we query it
        entityManager.flush();

        // --- ACT ---
        // Call the actual repository method we want to test
        List<String> foundRequests = friendRelationshipRepository.findPendingFriendRequests("user123");

        // --- ASSERT ---
        // Verify that the query returned exactly what we expected
        assertThat(foundRequests).isNotNull();
        assertThat(foundRequests).hasSize(1); // It should only find one request
        assertThat(foundRequests.get(0)).isEqualTo(pendingRequest.getUniqueID()); // It should be the ID of the sender
    }
}