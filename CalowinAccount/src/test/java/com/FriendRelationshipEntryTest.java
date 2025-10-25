package com;

import com.Account.Entities.FriendRelationshipEntry;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class FriendRelationshipEntryTest {

    @Test
    void testNoArgsConstructor() {
        FriendRelationshipEntry entry = new FriendRelationshipEntry();
        assertThat(entry).isNotNull();
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        LocalDateTime time = LocalDateTime.now();
        FriendRelationshipEntry entry = new FriendRelationshipEntry("USER1", "USER2", time, "FRIEND");

        assertThat(entry.getUserID()).isEqualTo("USER1");
        assertThat(entry.getFriendID()).isEqualTo("USER2");
        assertThat(entry.getFriendedOn()).isEqualTo(time);
        assertThat(entry.getStatus()).isEqualTo("FRIEND");
    }

    @Test
    void testSetters() {
        FriendRelationshipEntry entry = new FriendRelationshipEntry();
        LocalDateTime time = LocalDateTime.now().minusDays(1);

        entry.setUserID("U1");
        entry.setFriendID("U2");
        entry.setFriendedOn(time);
        entry.setStatus("PENDING");

        assertThat(entry.getUserID()).isEqualTo("U1");
        assertThat(entry.getFriendID()).isEqualTo("U2");
        assertThat(entry.getFriendedOn()).isEqualTo(time);
        assertThat(entry.getStatus()).isEqualTo("PENDING");
    }
}