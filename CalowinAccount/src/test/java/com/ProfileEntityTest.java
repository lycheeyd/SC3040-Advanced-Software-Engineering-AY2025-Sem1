package com;

import com.Account.Entities.ProfileEntity;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ProfileEntityTest {

    @Test
    void testNoArgsConstructor() {
        ProfileEntity profile = new ProfileEntity();
        assertThat(profile).isNotNull();
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        ProfileEntity profile = new ProfileEntity("USER1", "Test User", 70.5f, "A bio");

        assertThat(profile.getUserID()).isEqualTo("USER1");
        assertThat(profile.getName()).isEqualTo("Test User");
        assertThat(profile.getWeight()).isEqualTo(70.5f);
        assertThat(profile.getBio()).isEqualTo("A bio");
    }

    @Test
    void testSetters() {
        ProfileEntity profile = new ProfileEntity();
        profile.setUserID("USER2");
        profile.setName("New Name");
        profile.setWeight(80.0f);
        profile.setBio("New bio");

        assertThat(profile.getUserID()).isEqualTo("USER2");
        assertThat(profile.getName()).isEqualTo("New Name");
        assertThat(profile.getWeight()).isEqualTo(80.0f);
        assertThat(profile.getBio()).isEqualTo("New bio");
    }

    @Test
    void testUpdateProfile() {
        ProfileEntity profile = new ProfileEntity("USER1", "Test User", 70.5f, "A bio");
        profile.updateProfile("Updated Name", 75.0f, "Updated bio");

        assertThat(profile.getName()).isEqualTo("Updated Name");
        assertThat(profile.getWeight()).isEqualTo(75.0f);
        assertThat(profile.getBio()).isEqualTo("Updated bio");
    }
}