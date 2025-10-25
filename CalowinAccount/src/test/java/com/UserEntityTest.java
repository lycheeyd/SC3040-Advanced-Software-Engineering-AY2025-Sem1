package com;

import com.Account.Entities.UserEntity;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UserEntityTest {

    @Test
    void testNoArgsConstructor() {
        UserEntity user = new UserEntity();
        assertThat(user).isNotNull();
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        UserEntity user = new UserEntity("USER1", "test@test.com", "password123");

        assertThat(user.getUserID()).isEqualTo("USER1");
        assertThat(user.getEmail()).isEqualTo("test@test.com");
        assertThat(user.getPassword()).isEqualTo("password123");
    }

    @Test
    void testSetters() {
        UserEntity user = new UserEntity();
        user.setUserID("USER2");
        user.setEmail("new@test.com");
        user.setPassword("newpass");

        assertThat(user.getUserID()).isEqualTo("USER2");
        assertThat(user.getEmail()).isEqualTo("new@test.com");
        assertThat(user.getPassword()).isEqualTo("newpass");
    }
}