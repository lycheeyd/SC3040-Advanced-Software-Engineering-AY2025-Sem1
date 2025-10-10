package com;

import com.Database.CalowinDB.CalowinDBConfig;
import com.Database.CalowinDB.CalowinDBProperties;
import com.Database.DataSourceConfig;
import com.Database.MyBeans;
import com.Database.CalowinDB.FriendRelationshipRepository;
import com.RelationshipNotification.Entity.FriendRelationshipEntity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This is an INTEGRATION TEST for the FriendRelationshipRepository.
 * It uses @SpringBootTest to load the full application context and connect to a
 * database
 * to verify that the custom SQL query works correctly.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ DataSourceConfig.class, CalowinDBConfig.class, MyBeans.class })
@EnableConfigurationProperties(CalowinDBProperties.class)
@Transactional(transactionManager = "calowinDBTransactionManager")
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
        // 1. A pending request FOR our target user ("user123"). This SHOULD be found.
        FriendRelationshipEntity pendingRequest = new FriendRelationshipEntity();
        pendingRequest.setUniqueID("sender01"); // Using an 8-char-or-less ID
        pendingRequest.setFriendUniqueID("user123");
        pendingRequest.setStatus("REQUESTSENT");
        pendingRequest.setFriendedOn("2025-10-03");
        entityManager.persist(pendingRequest);

        // 2. An already accepted friend. This should NOT be found.
        FriendRelationshipEntity acceptedFriend = new FriendRelationshipEntity();
        acceptedFriend.setUniqueID("sender02");
        acceptedFriend.setFriendUniqueID("user123");
        acceptedFriend.setStatus("ACCEPTED");
        acceptedFriend.setFriendedOn("2025-10-01");
        entityManager.persist(acceptedFriend);

        // 3. A pending request for a DIFFERENT user. This should NOT be found.
        FriendRelationshipEntity anotherUserRequest = new FriendRelationshipEntity();
        anotherUserRequest.setUniqueID("sender03");
        anotherUserRequest.setFriendUniqueID("user456"); // A different receiver
        anotherUserRequest.setStatus("REQUESTSENT");
        anotherUserRequest.setFriendedOn("2025-10-03");
        entityManager.persist(anotherUserRequest);

        entityManager.flush();

        // --- ACT ---
        List<String> foundRequests = friendRelationshipRepository.findPendingFriendRequests("user123");

        // --- ASSERT ---
        assertThat(foundRequests).isNotNull();
        assertThat(foundRequests).hasSize(1);
        assertThat(foundRequests.get(0)).isEqualTo(pendingRequest.getUniqueID());
    }
}