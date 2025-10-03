package com.Services;

import com.models.UserInfo;
import com.repository.UserInfoRepository;
import com.service.UserInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the UserInfoService class.
 * These tests use Mockito to isolate the service from the repository layer.
 */
@ExtendWith(MockitoExtension.class)
class UserInfoServiceTest {

    @Mock
    private UserInfoRepository userInfoRepository;

    @InjectMocks
    private UserInfoService userInfoService;

    // Test data objects, re-initialized before each test
    private UserInfo alice;
    private UserInfo bob;
    private UserInfo charlie;

    @BeforeEach
    void setUp() {
        // Create fresh, consistent test data before each test runs
        alice = createUser("alice_id", "Alice Tan");
        bob = createUser("bob_id", "Bob Lee");
        charlie = createUser("charlie_id", "Charlie Ong");
    }

    /**
     * Helper method to reduce boilerplate code for creating UserInfo objects.
     */
    private UserInfo createUser(String id, String name) {
        UserInfo user = new UserInfo();
        user.setUserId(id);
        user.setName(name);
        return user;
    }

    @Nested
    @DisplayName("Tests for searchByUserIdOrName method")
    class SearchByUserIdOrNameTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   "})
        @DisplayName("should return an empty list for null, empty, or blank search terms")
        void search_WithInvalidTerm_ShouldReturnEmptyList(String invalidSearchTerm) {
            // ACT
            List<UserInfo> result = userInfoService.searchByUserIdOrName(invalidSearchTerm, "any_user_id");

            // ASSERT
            assertThat(result).isEmpty();
            // Verify that the repository is never called for invalid input
            verify(userInfoRepository, never()).searchByUserIdOrName(anyString());
        }

        @Test
        @DisplayName("should filter out the current user from the repository results")
        void search_ShouldFilterOutCurrentUser() {
            // ARRANGE
            String searchTerm = "a";
            String currentUserId = "alice_id"; // Alice is the current user
            when(userInfoRepository.searchByUserIdOrName(searchTerm))
                    .thenReturn(List.of(alice, bob, charlie));

            // ACT
            List<UserInfo> result = userInfoService.searchByUserIdOrName(searchTerm, currentUserId);

            // ASSERT
            // The result should contain Bob and Charlie, but not Alice.
            assertThat(result)
                    .extracting(UserInfo::getUserId)
                    .containsExactly("bob_id", "charlie_id");
            verify(userInfoRepository).searchByUserIdOrName(searchTerm);
        }

        @Test
        @DisplayName("should return all results if current user is not in the list")
        void search_WhenCurrentUserNotInResults_ShouldNotFilterAnyone() {
            // ARRANGE
            String searchTerm = "b";
            String currentUserId = "dave_id"; // A user not present in the results
            when(userInfoRepository.searchByUserIdOrName(searchTerm))
                    .thenReturn(List.of(bob));

            // ACT
            List<UserInfo> result = userInfoService.searchByUserIdOrName(searchTerm, currentUserId);

            // ASSERT
            // The result should be exactly what the repository returned.
            assertThat(result).containsExactly(bob);
            verify(userInfoRepository).searchByUserIdOrName(searchTerm);
        }

        @Test
        @DisplayName("should return an empty list if the repository finds no matches")
        void search_WhenRepoReturnsEmpty_ShouldReturnEmptyList() {
            // ARRANGE
            String searchTerm = "nonexistent";
            when(userInfoRepository.searchByUserIdOrName(searchTerm)).thenReturn(List.of());

            // ACT
            List<UserInfo> result = userInfoService.searchByUserIdOrName(searchTerm, "any_user_id");

            // ASSERT
            assertThat(result).isEmpty();
            verify(userInfoRepository).searchByUserIdOrName(searchTerm);
        }
    }

    @Nested
    @DisplayName("Tests for getUserInfoById method")
    class GetUserInfoByIdTests {

        @Test
        @DisplayName("should return the UserInfo object when the user is found")
        void getUserInfo_WhenFound_ShouldReturnUser() {
            // ARRANGE
            when(userInfoRepository.findById("bob_id")).thenReturn(Optional.of(bob));

            // ACT
            UserInfo result = userInfoService.getUserInfoById("bob_id");

            // ASSERT
            assertThat(result).isEqualTo(bob);
            verify(userInfoRepository).findById("bob_id");
        }

        @Test
        @DisplayName("should return null when the user is not found")
        void getUserInfo_WhenNotFound_ShouldReturnNull() {
            // ARRANGE
            when(userInfoRepository.findById("nobody")).thenReturn(Optional.empty());

            // ACT
            UserInfo result = userInfoService.getUserInfoById("nobody");

            // ASSERT
            assertThat(result).isNull();
            verify(userInfoRepository).findById("nobody");
        }
    }

    @Nested
    @DisplayName("Tests for getUserNameById method")
    class GetUserNameByIdTests {

        @Test
        @DisplayName("should return the user's name when the user is found")
        void getUserName_WhenFound_ShouldReturnName() {
            // ARRANGE
            when(userInfoRepository.findById("charlie_id")).thenReturn(Optional.of(charlie));

            // ACT
            String name = userInfoService.getUserNameById("charlie_id");

            // ASSERT
            assertThat(name).isEqualTo("Charlie Ong");
            verify(userInfoRepository).findById("charlie_id");
        }

        @Test
        @DisplayName("should return null when the user is not found")
        void getUserName_WhenNotFound_ShouldReturnNull() {
            // ARRANGE
            when(userInfoRepository.findById("ghost_id")).thenReturn(Optional.empty());

            // ACT
            String name = userInfoService.getUserNameById("ghost_id");

            // ASSERT
            assertThat(name).isNull();
            verify(userInfoRepository).findById("ghost_id");
        }
    }
}