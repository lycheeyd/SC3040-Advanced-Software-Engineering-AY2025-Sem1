package com.Services;


import com.ENUM.FriendRequestStatus;
import com.ENUM.FriendStatus;
import com.dto.AchievementDTO;
import com.dto.FriendRelationshipDTO;
import com.models.Achievement;
import com.models.FriendRelationship;
import com.models.FriendRelationshipId;
import com.models.UserInfo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This test class covers POJOs (Models, DTOs) and Enums
 * just to satisfy code coverage for getters, setters, constructors,
 * equals, and hashCode.
 */
class DataObjectsTest {

    @Test
    void testUserInfoModel() {
        UserInfo user = new UserInfo();
        user.setUserId("id");
        user.setName("name");
        user.setWeight(70.5f);
        user.setBio("bio");

        assertThat(user.getUserId()).isEqualTo("id");
        assertThat(user.getName()).isEqualTo("name");
        assertThat(user.getWeight()).isEqualTo(70.5f);
        assertThat(user.getBio()).isEqualTo("bio");
    }

    @Test
    void testAchievementModel() {
        Achievement ach = new Achievement();
        ach.setUserId("id");
        ach.setTotalCarbonSaved(100);
        ach.setTotalCalorieBurnt(2000);
        ach.setCarbonMedal("GOLD");
        ach.setCalorieMedal("SILVER");

        assertThat(ach.getUserId()).isEqualTo("id");
        assertThat(ach.getTotalCarbonSaved()).isEqualTo(100);
        assertThat(ach.getTotalCalorieBurnt()).isEqualTo(2000);
        assertThat(ach.getCarbonMedal()).isEqualTo("GOLD");
        assertThat(ach.getCalorieMedal()).isEqualTo("SILVER");
    }

    @Test
    void testFriendRelationshipIdModel() {
        FriendRelationshipId id1 = new FriendRelationshipId("userA", "userB");
        FriendRelationshipId id2 = new FriendRelationshipId("userA", "userB");
        FriendRelationshipId id3 = new FriendRelationshipId(); // For default constructor
        id3.setUniqueId("userA");
        id3.setFriendUniqueId("userB");

        assertThat(id1.getUniqueId()).isEqualTo("userA");
        assertThat(id1.getFriendUniqueId()).isEqualTo("userB");
        assertThat(id1).isEqualTo(id2);
        assertThat(id1).isEqualTo(id3);
        assertThat(id1).hasSameHashCodeAs(id2);
        assertThat(id1.equals("string")).isFalse();
        assertThat(id1.equals(null)).isFalse();
        assertThat(id1.equals(new FriendRelationshipId("userA", "userC"))).isFalse();
    }

    @Test
    void testFriendRelationshipModel() {
        FriendRelationshipId id = new FriendRelationshipId("userA", "userB");
        LocalDateTime now = LocalDateTime.now();
        FriendRelationship rel = new FriendRelationship();
        rel.setId(id);
        rel.setStatus("ACCEPTED");
        rel.setFriendedOn(now);

        assertThat(rel.getId()).isEqualTo(id);
        assertThat(rel.getStatus()).isEqualTo("ACCEPTED");
        assertThat(rel.getFriendedOn()).isEqualTo(now);
    }

    @Test
    void testAchievementDTO() {
        // Test parameterized constructor
        AchievementDTO dto1 = new AchievementDTO("id", "name", 100, 2000, "G", "S");
        assertThat(dto1.getUserName()).isEqualTo("name");

        // Test default constructor and setters/getters
        AchievementDTO dto2 = new AchievementDTO();
        dto2.setUserId("id");
        dto2.setUserName("name");
        dto2.setTotalCarbonSaved(100);
        dto2.setTotalCalorieBurnt(2000);
        dto2.setCarbonMedal("G");
        dto2.setCalorieMedal("S");

        assertThat(dto2.getUserId()).isEqualTo("id");
        assertThat(dto2.getUserName()).isEqualTo("name");
        assertThat(dto2.getTotalCarbonSaved()).isEqualTo(100);
        assertThat(dto2.getTotalCalorieBurnt()).isEqualTo(2000);
        assertThat(dto2.getCarbonMedal()).isEqualTo("G");
        assertThat(dto2.getCalorieMedal()).isEqualTo("S");
    }

    @Test
    void testFriendRelationshipDTO() {
        // Test parameterized constructor
        FriendRelationshipDTO dto1 = new FriendRelationshipDTO("uid", "uname", "fid", "fname", "STATUS");
        assertThat(dto1.getUserName()).isEqualTo("uname");

        // Test default constructor and setters/getters
        FriendRelationshipDTO dto2 = new FriendRelationshipDTO();
        dto2.setUserId("uid");
        dto2.setUserName("uname");
        dto2.setFriendUserId("fid");
        dto2.setFriendUserName("fname");
        dto2.setStatus("STATUS");

        assertThat(dto2.getUserId()).isEqualTo("uid");
        assertThat(dto2.getUserName()).isEqualTo("uname");
        assertThat(dto2.getFriendUserId()).isEqualTo("fid");
        assertThat(dto2.getFriendUserName()).isEqualTo("fname");
        assertThat(dto2.getStatus()).isEqualTo("STATUS");
    }

    @Test
    void testEnums() {
        // This covers the static `valueOf` methods
        assertEquals(FriendStatus.FRIEND, FriendStatus.valueOf("FRIEND"));
        assertEquals(FriendStatus.REQUESTSENT, FriendStatus.valueOf("REQUESTSENT"));

        assertEquals(FriendRequestStatus.PENDING, FriendRequestStatus.valueOf("PENDING"));
        assertEquals(FriendRequestStatus.ACCEPTED, FriendRequestStatus.valueOf("ACCEPTED"));
    }
}