package com;

import com.Account.Entities.ProfileEntity;
import com.Account.Managers.AccountManagementService;
import com.Account.Managers.PasswordManagementService;
import com.Account.Managers.ProfileManagementService;
import com.Account.RESTController.HttpReqController;
import com.Account.Services.OTPService;
import com.DataTransferObject.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// Import all necessary static methods
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.web.client.RestTemplate;

@WebMvcTest(HttpReqController.class)
class HttpReqControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountManagementService accountManagementService;
    @MockBean
    private PasswordManagementService passwordManagementService;
    @MockBean
    private ProfileManagementService profileManagementService;
    @MockBean
    private OTPService otpService;

    @MockBean
    private RestTemplate restTemplate;

    // --- Helper DTOs for tests ---
    private SignupDTO createSignupDTO() {
        SignupDTO dto = new SignupDTO();
        dto.setEmail("newuser@example.com");
        dto.setName("New User");
        dto.setPassword("a-valid-password");
        dto.setConfirm_password("a-valid-password");
        dto.setWeight(70f);
        return dto;
    }

    private LoginDTO createLoginDTO() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("encryptedPassword");
        return dto;
    }

    private DeleteAccountDTO createDeleteAccountDTO() {
        DeleteAccountDTO dto = new DeleteAccountDTO();
        dto.setUserID("USER123");
        dto.setEmail("test@example.com");
        dto.setOtpCode("123456");
        return dto;
    }

    // --- Tests for each endpoint ---

    @Nested
    @DisplayName("POST /account/signup")
    class SignupTests {
        // Tests remain the same as previously generated and passed
        @Test
        @DisplayName("should return 201 Created on successful signup")
        void signup_success() throws Exception {
            LoginResponseDTO responseDTO = new LoginResponseDTO("USER123", "newuser@example.com", "New User", 70f, "",
                    0, 0, "No Medal", "No Medal");
            when(accountManagementService.signup(anyString(), anyString(), anyString(), anyString(), anyFloat()))
                    .thenReturn(responseDTO);

            mockMvc.perform(post("/account/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createSignupDTO())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.UserObject.email").value("newuser@example.com"));
        }

        @Test
        @DisplayName("should return 400 Bad Request if user already exists")
        void signup_userExists() throws Exception {
            when(accountManagementService.signup(anyString(), anyString(), anyString(), anyString(), anyFloat()))
                    .thenThrow(new RuntimeException("User already exists"));

            mockMvc.perform(post("/account/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createSignupDTO())))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$").value("Signup failed: User already exists"));
        }

        @Test
        @DisplayName("should return 500 Internal Server Error on unexpected Exception")
        void signup_onException_shouldReturn500() throws Exception {
            when(accountManagementService.signup(anyString(), anyString(), anyString(), anyString(), anyFloat()))
                    .thenThrow(new Exception("Database connection failed")); // Generic Exception

            mockMvc.perform(post("/account/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createSignupDTO())))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$").value("Error during signup: Database connection failed"));
        }
    }

    @Nested
    @DisplayName("POST /account/login")
    class LoginTests {
        // Tests remain the same as previously generated and passed
        @Test
        @DisplayName("should return 200 OK for valid credentials")
        void login_success() throws Exception {
            LoginResponseDTO responseDTO = new LoginResponseDTO("USER1234", "test@example.com", "Test User", 70f, "", 0,
                    0, "No Medal", "No Medal");
            when(accountManagementService.login(anyString(), anyString())).thenReturn(responseDTO);

            mockMvc.perform(post("/account/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createLoginDTO())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.UserObject.email").value("test@example.com"));
        }

        @Test
        @DisplayName("should return 401 Unauthorized for invalid credentials")
        void login_failure() throws Exception {
            when(accountManagementService.login(anyString(), anyString()))
                    .thenThrow(new RuntimeException("Invalid email or password"));

            mockMvc.perform(post("/account/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createLoginDTO())))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$").value("Invalid email or password"));
        }

        @Test
        @DisplayName("should return 500 Internal Server Error on unexpected Exception")
        void login_onException_shouldReturn500() throws Exception {
            when(accountManagementService.login(anyString(), anyString()))
                    .thenThrow(new Exception("AES decryption error")); // Generic Exception

            mockMvc.perform(post("/account/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createLoginDTO())))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$").value("Error during login: AES decryption error"));
        }
    }

    @Nested
    @DisplayName("POST /account/send-otp")
    class SendOtpTests {
        // Test remains the same as previously generated and passed
        @Test
        @DisplayName("should return 500 Internal Server Error on unexpected Exception")
        void sendOtp_onException_shouldReturn500() throws Exception {
            SendOtpDTO dto = new SendOtpDTO();
            dto.setEmail("test@test.com");
            dto.setType(com.Account.Entities.ActionType.SIGN_UP);

            doThrow(new Exception("Email service down"))
                    .when(otpService).sendOtpCode(anyString(), any());

            mockMvc.perform(post("/account/send-otp")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$").value("Error sending OTP: Email service down"));
        }
    }

    @Nested
    @DisplayName("POST /account/verify-otp")
    class VerifyOtpTests {
        @Test
        @DisplayName("should return 500 Internal Server Error on unexpected RuntimeException") // Changed Exception type
        void verifyOtp_onException_shouldReturn500() throws Exception {
            VerifyOtpDTO dto = new VerifyOtpDTO();
            dto.setEmail("test@test.com");
            dto.setOtpCode("123456");
            dto.setType(com.Account.Entities.ActionType.SIGN_UP);

            // *** FIX: Throw RuntimeException instead of checked Exception ***
            when(otpService.verifyOTP(anyString(), anyString(), any()))
                    .thenThrow(new RuntimeException("DB connection error")); // Use RuntimeException

            mockMvc.perform(post("/account/verify-otp")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$").value("Error validating OTP: DB connection error")); // Message remains the same
        }
        // Add tests for 200 OK and 401 Unauthorized if not already present
        @Test
        @DisplayName("should return 200 OK for valid OTP")
        void verifyOtp_success() throws Exception {
            VerifyOtpDTO dto = new VerifyOtpDTO();
            dto.setEmail("test@test.com");
            dto.setOtpCode("123456");
            dto.setType(com.Account.Entities.ActionType.SIGN_UP);

            when(otpService.verifyOTP(anyString(), anyString(), any())).thenReturn(true);

            mockMvc.perform(post("/account/verify-otp")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value("OTP valid"));
        }

        @Test
        @DisplayName("should return 401 Unauthorized for invalid OTP")
        void verifyOtp_invalid() throws Exception {
            VerifyOtpDTO dto = new VerifyOtpDTO();
            dto.setEmail("test@test.com");
            dto.setOtpCode("wrong");
            dto.setType(com.Account.Entities.ActionType.SIGN_UP);

            when(otpService.verifyOTP(anyString(), anyString(), any())).thenReturn(false);

            mockMvc.perform(post("/account/verify-otp")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$").value("Invalid OTP"));
        }
    }

    @Nested
    @DisplayName("POST /account/change-password")
    class ChangePasswordTests {
        // Test remains the same as previously generated and passed
        @Test
        @DisplayName("should return 500 Internal Server Error on unexpected Exception")
        void changePassword_onException_shouldReturn500() throws Exception {
            ChangePasswordDTO dto = new ChangePasswordDTO();
            dto.setUserID("USER123");
            dto.setOldPassword("old");
            dto.setNewPassword("new");
            dto.setConfirm_newPassword("new");

            doThrow(new Exception("AES error"))
                    .when(passwordManagementService).changePassword(anyString(), anyString(), anyString(), anyString());

            mockMvc.perform(post("/account/change-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$").value("An error occurred: AES error"));
        }
        // Add tests for 200 OK and 400 Bad Request if not already present
        @Test
        @DisplayName("should return 200 OK on successful password change")
        void changePassword_success() throws Exception {
            ChangePasswordDTO dto = new ChangePasswordDTO();
            dto.setUserID("USER123");
            dto.setOldPassword("old");
            dto.setNewPassword("new");
            dto.setConfirm_newPassword("new");

            doNothing().when(passwordManagementService).changePassword(anyString(), anyString(), anyString(), anyString());

            mockMvc.perform(post("/account/change-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value("Password changed successfully"));
        }

        @Test
        @DisplayName("should return 400 Bad Request on RuntimeException (e.g., wrong old password)")
        void changePassword_runtimeError() throws Exception {
            ChangePasswordDTO dto = new ChangePasswordDTO();
            dto.setUserID("USER123");
            dto.setOldPassword("wrong");
            dto.setNewPassword("new");
            dto.setConfirm_newPassword("new");

            doThrow(new RuntimeException("Wrong password"))
                    .when(passwordManagementService).changePassword(anyString(), anyString(), anyString(), anyString());

            mockMvc.perform(post("/account/change-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$").value("Wrong password"));
        }
    }

    @Nested
    @DisplayName("POST /account/forgot-password")
    class ForgotPasswordTests {
        // Test remains the same as previously generated and passed
        @Test
        @DisplayName("should return 500 Internal Server Error on unexpected Exception")
        void forgotPassword_onException_shouldReturn500() throws Exception {
            ForgotPasswordDTO dto = new ForgotPasswordDTO();
            dto.setEmail("test@test.com");
            dto.setOtpCode("123456");

            doThrow(new Exception("Email service down"))
                    .when(passwordManagementService).forgotPassword(anyString(), anyString());

            mockMvc.perform(post("/account/forgot-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$").value("Error sending new password: Email service down"));
        }
        // Add tests for 200 OK and 401 Unauthorized if not already present
        @Test
        @DisplayName("should return 200 OK on successful forgot password flow")
        void forgotPassword_success() throws Exception {
            ForgotPasswordDTO dto = new ForgotPasswordDTO();
            dto.setEmail("test@test.com");
            dto.setOtpCode("123456");

            doNothing().when(passwordManagementService).forgotPassword(anyString(), anyString());

            mockMvc.perform(post("/account/forgot-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value("New password is sent to your email"));
        }

        @Test
        @DisplayName("should return 401 Unauthorized on RuntimeException (e.g., invalid OTP)")
        void forgotPassword_runtimeError() throws Exception {
            ForgotPasswordDTO dto = new ForgotPasswordDTO();
            dto.setEmail("test@test.com");
            dto.setOtpCode("wrong");

            doThrow(new RuntimeException("Invalid OTP"))
                    .when(passwordManagementService).forgotPassword(anyString(), anyString());

            mockMvc.perform(post("/account/forgot-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$").value("Invalid OTP"));
        }
    }

    @Nested
    @DisplayName("POST /account/edit-profile")
    class EditProfileTests {
        @Test
        @DisplayName("should return 500 Internal Server Error on unexpected Exception")
        void editProfile_onException_shouldReturn500() throws Exception {
            EditProfileDTO dto = new EditProfileDTO();
            dto.setUserID("USER123");
            dto.setName("New Name");
            dto.setWeight(75.0f); // Make sure all relevant fields are set
            dto.setBio("New bio");

            // *** FIX: Use more specific matchers or ensure DTO properties match service call ***
            // Using any() is generally fine, but let's ensure the mock is correctly configured.
            // The previous failure suggests the mock might not have been hit.
            // Ensure ProfileManagementService declares throws Exception
            when(profileManagementService.editProfile(eq(dto.getUserID()), eq(dto.getName()), eq(dto.getWeight()), eq(dto.getBio())))
                    .thenThrow(new Exception("DB error")); // Generic Exception

            mockMvc.perform(post("/account/edit-profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isInternalServerError()) // Should now get 500
                    .andExpect(jsonPath("$").value("An error occurred: DB error"));
        }

        // Add tests for 200 OK and 400 Bad Request if not already present
        @Test
        @DisplayName("should return 200 OK on successful profile edit")
        void editProfile_success() throws Exception {
            EditProfileDTO dto = new EditProfileDTO();
            dto.setUserID("USER123");
            dto.setName("New Name");
            dto.setWeight(75.0f);
            dto.setBio("New bio");

            ProfileEntity updatedProfile = new ProfileEntity(dto.getUserID(), dto.getName(), dto.getWeight(), dto.getBio());

            when(profileManagementService.editProfile(anyString(), anyString(), anyFloat(), anyString()))
                    .thenReturn(updatedProfile);

            mockMvc.perform(post("/account/edit-profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Profile updated successfully"))
                    .andExpect(jsonPath("$.UserObject.name").value("New Name"));
        }

        @Test
        @DisplayName("should return 400 Bad Request on RuntimeException (e.g., user not found)")
        void editProfile_runtimeError() throws Exception {
            EditProfileDTO dto = new EditProfileDTO();
            dto.setUserID("UNKNOWN"); // The ID expected to cause the error
            dto.setName("New Name");
            dto.setWeight(75.0f); // Set weight explicitly
            dto.setBio("New bio"); // Set bio explicitly

            // Mock the service call with specific matchers for the expected failing input
            when(profileManagementService.editProfile(
                    eq("UNKNOWN"),        // Expecting this specific userID
                    eq(dto.getName()),    // Expecting the name from the DTO
                    eq(dto.getWeight()),  // Expecting the weight from the DTO
                    eq(dto.getBio())      // Expecting the bio from the DTO
            )).thenThrow(new RuntimeException("User not found"));

            mockMvc.perform(post("/account/edit-profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))) // Send the fully populated DTO
                    .andExpect(status().isBadRequest()) // Expect 400
                    .andExpect(jsonPath("$").value("User not found"));
        }
    }

    @Nested
    @DisplayName("POST /account/delete-account")
    class DeleteAccountTests {
        // (Existing + new 500 test remain the same as previously generated and passed)
        @Test
        @DisplayName("should return 200 OK on successful account deletion")
        void deleteAccount_success() throws Exception {
            doNothing().when(accountManagementService).deleteAccount(anyString(), anyString(), anyString());

            mockMvc.perform(post("/account/delete-account")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDeleteAccountDTO())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value("Account deleted")); // Match exact response
        }

        @Test
        @DisplayName("should return 401 Unauthorized on invalid OTP")
        void deleteAccount_invalidOtp() throws Exception {
            doThrow(new RuntimeException("Invalid OTP"))
                    .when(accountManagementService).deleteAccount(anyString(), anyString(), anyString());

            mockMvc.perform(post("/account/delete-account")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDeleteAccountDTO())))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$").value("Invalid OTP"));
        }

        @Test
        @DisplayName("should return 500 Internal Server Error on unexpected Exception")
        void deleteAccount_onException_shouldReturn500() throws Exception {
            doThrow(new Exception("Transaction failed"))
                    .when(accountManagementService).deleteAccount(anyString(), anyString(), anyString());

            mockMvc.perform(post("/account/delete-account")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDeleteAccountDTO())))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$").value("Error deleting account: Transaction failed"));
        }
    }

    @Nested
    @DisplayName("GET /account/view-profile/{selfID}/{otherID}")
    class ViewOtherProfileTests {
        // Test remains the same as previously generated and passed
        @Test
        @DisplayName("should return 500 Internal Server Error on unexpected Exception")
        void viewProfile_other_onException_shouldReturn500() throws Exception {
            when(profileManagementService.viewProfile(anyString(), anyString()))
                    .thenThrow(new Exception("External service timeout")); // Generic Exception

            mockMvc.perform(get("/account/view-profile/{selfID}/{otherID}", "SELF1", "OTHER1"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$").value("An error occurred: External service timeout"));
        }
        // Add tests for 200 OK and 404 Not Found if not already present
        @Test
        @DisplayName("should return 200 OK for existing other user")
        void viewProfile_other_success() throws Exception {
            ViewProfileResponseDTO responseDTO = new ViewProfileResponseDTO("OTHER1", "Other User", "bio", com.Account.Entities.FriendStatus.STRANGER, 0,0,"","");
            when(profileManagementService.viewProfile(eq("SELF1"), eq("OTHER1")))
                    .thenReturn(responseDTO);

            mockMvc.perform(get("/account/view-profile/{selfID}/{otherID}", "SELF1", "OTHER1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Profile retrieved successfully"))
                    .andExpect(jsonPath("$.UserObject.userID").value("OTHER1"));
        }

        @Test
        @DisplayName("should return 404 Not Found for non-existing other user")
        void viewProfile_other_notFound() throws Exception {
            when(profileManagementService.viewProfile(eq("SELF1"), eq("UNKNOWN")))
                    .thenThrow(new RuntimeException("User not found"));

            mockMvc.perform(get("/account/view-profile/{selfID}/{otherID}", "SELF1", "UNKNOWN"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$").value("User not found"));
        }
    }

    @Nested
    @DisplayName("GET /account/view-profile/{selfID}")
    class ViewSelfProfileTests {
        // (Existing + new 500 test remain the same as previously generated and passed)
        @Test
        @DisplayName("should return 200 OK and profile data for existing user")
        void viewProfile_self_success() throws Exception {
            String selfId = "USER123";
            LoginResponseDTO responseDTO = new LoginResponseDTO(selfId, "test@example.com", "Test User", 70f, "bio", 10,
                    20, "Bronze", "Bronze");
            when(profileManagementService.viewProfile(selfId)).thenReturn(responseDTO);

            mockMvc.perform(get("/account/view-profile/{selfID}", selfId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Self Profile retrieved successfully")) // Match exact message
                    .andExpect(jsonPath("$.UserObject.userID").value(selfId));
        }

        @Test
        @DisplayName("should return 404 Not Found for non-existing user")
        void viewProfile_self_notFound() throws Exception {
            String selfId = "UNKNOWN";
            when(profileManagementService.viewProfile(selfId)).thenThrow(new RuntimeException("User not found"));

            mockMvc.perform(get("/account/view-profile/{selfID}", selfId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$").value("User not found"));
        }

        @Test
        @DisplayName("should return 500 Internal Server Error on unexpected Exception")
        void viewProfile_self_onException_shouldReturn500() throws Exception {
            String selfId = "USER123";
            when(profileManagementService.viewProfile(selfId))
                    .thenThrow(new Exception("DB error")); // Generic Exception

            mockMvc.perform(get("/account/view-profile/{selfID}", selfId))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$").value("An error occurred: DB error"));
        }
    }
}