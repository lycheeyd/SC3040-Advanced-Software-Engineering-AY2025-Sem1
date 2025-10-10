package com; // <-- CORRECTED PACKAGE

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

// Import all necessary static methods from Mockito
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

    // Mock all service dependencies of the controller
    @MockBean
    private AccountManagementService accountManagementService;
    @MockBean
    private PasswordManagementService passwordManagementService;
    @MockBean
    private ProfileManagementService profileManagementService;
    @MockBean
    private OTPService otpService;

    @MockBean
    private RestTemplate restTemplate; //

    @Nested
    @DisplayName("POST /account/signup")
    class SignupTests {

        @Test
        @DisplayName("should return 201 Created on successful signup")
        void signup_success() throws Exception {
            // Arrange
            // POPULATE THE ENTIRE DTO
            SignupDTO signupDTO = new SignupDTO();
            signupDTO.setEmail("newuser@example.com");
            signupDTO.setName("New User");
            signupDTO.setPassword("a-valid-password");
            signupDTO.setConfirm_password("a-valid-password");
            signupDTO.setWeight(70f);

            LoginResponseDTO responseDTO = new LoginResponseDTO("USER123", "newuser@example.com", "New User", 70f, "",
                    0, 0, "No Medal", "No Medal");

            // This mock will now be triggered and return the responseDTO
            when(accountManagementService.signup(anyString(), anyString(), anyString(), anyString(), anyFloat()))
                    .thenReturn(responseDTO);

            // Act & Assert
            mockMvc.perform(post("/account/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signupDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value("Signup successful"))
                    .andExpect(jsonPath("$.UserObject.email").value("newuser@example.com")); // This should now pass
        }

        @Test
        @DisplayName("should return 400 Bad Request if user already exists")
        void signup_userExists() throws Exception {
            // Arrange
            // POPULATE THE ENTIRE DTO
            SignupDTO signupDTO = new SignupDTO();
            signupDTO.setEmail("existing@example.com");
            signupDTO.setName("Existing User");
            signupDTO.setPassword("some-password");
            signupDTO.setConfirm_password("some-password");
            signupDTO.setWeight(75.0f);

            // This mock will now be triggered correctly
            when(accountManagementService.signup(anyString(), anyString(), anyString(), anyString(), anyFloat()))
                    .thenThrow(new RuntimeException("User already exists"));

            // Act & Assert
            mockMvc.perform(post("/account/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signupDTO)))
                    .andExpect(status().isBadRequest()) // This should now pass
                    .andExpect(jsonPath("$").value("Signup failed: User already exists"));
        }
    }

    @Nested
    @DisplayName("POST /account/login")
    class LoginTests {
        @Test
        @DisplayName("should return 200 OK for valid credentials")
        void login_success() throws Exception {
            // Arrange
            LoginDTO loginDTO = new LoginDTO();
            loginDTO.setEmail("test@example.com");
            loginDTO.setPassword("encryptedPassword");

            LoginResponseDTO responseDTO = new LoginResponseDTO("USER1234", "test@example.com", "Test User", 70f, "", 0,
                    0, "No Medal", "No Medal");
            when(accountManagementService.login(anyString(), anyString())).thenReturn(responseDTO);

            // Act & Assert
            mockMvc.perform(post("/account/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Login successful"))
                    .andExpect(jsonPath("$.UserObject.email").value("test@example.com"));
        }

        @Test
        @DisplayName("should return 401 Unauthorized for invalid credentials")
        void login_failure() throws Exception {
            // Arrange
            // POPULATE THE DTO
            LoginDTO loginDTO = new LoginDTO();
            loginDTO.setEmail("wrong@example.com");
            loginDTO.setPassword("wrongpassword");

            // This mock will now be triggered by the non-null arguments
            when(accountManagementService.login(anyString(), anyString()))
                    .thenThrow(new RuntimeException("Invalid email or password"));

            // Act & Assert
            mockMvc.perform(post("/account/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginDTO)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$").value("Invalid email or password"));
        }
    }

    @Nested
    @DisplayName("POST /account/delete-account")
    class DeleteAccountTests {
        @Test
        @DisplayName("should return 200 OK on successful account deletion")
        void deleteAccount_success() throws Exception {
            // Arrange
            DeleteAccountDTO deleteDTO = new DeleteAccountDTO();
            deleteDTO.setUserID("USER123");
            deleteDTO.setEmail("test@example.com");
            deleteDTO.setOtpCode("123456");

            doNothing().when(accountManagementService).deleteAccount(anyString(), anyString(), anyString());

            // Act & Assert
            mockMvc.perform(post("/account/delete-account")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(deleteDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value("Account deleted"));
        }

        @Test
        @DisplayName("should return 401 Unauthorized on invalid OTP")
        void deleteAccount_invalidOtp() throws Exception {
            // Arrange
            // Populate the DTO with non-null values
            DeleteAccountDTO deleteDTO = new DeleteAccountDTO();
            deleteDTO.setUserID("test-user-id");
            deleteDTO.setEmail("test@example.com");
            deleteDTO.setOtpCode("invalid-otp");

            // This mock will now be triggered correctly
            doThrow(new RuntimeException("Invalid OTP"))
                    .when(accountManagementService).deleteAccount(anyString(), anyString(), anyString());

            // Act & Assert
            mockMvc.perform(post("/account/delete-account")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(deleteDTO)))
                    .andExpect(status().isUnauthorized()) // This assertion should now pass
                    .andExpect(jsonPath("$").value("Invalid OTP"));
        }
    }

    @Nested
    @DisplayName("GET /account/view-profile/{selfID}")
    class ViewProfileTests {
        @Test
        @DisplayName("should return 200 OK and profile data for existing user")
        void viewProfile_self_success() throws Exception {
            // Arrange
            String selfId = "USER123";
            LoginResponseDTO responseDTO = new LoginResponseDTO(selfId, "test@example.com", "Test User", 70f, "bio", 10,
                    20, "Bronze", "Bronze");
            when(profileManagementService.viewProfile(selfId)).thenReturn(responseDTO);

            // Act & Assert
            mockMvc.perform(get("/account/view-profile/{selfID}", selfId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Self Profile retrieved successfully"))
                    .andExpect(jsonPath("$.UserObject.userID").value(selfId));
        }

        @Test
        @DisplayName("should return 404 Not Found for non-existing user")
        void viewProfile_self_notFound() throws Exception {
            // Arrange
            String selfId = "UNKNOWN";
            when(profileManagementService.viewProfile(selfId)).thenThrow(new RuntimeException("User not found"));

            // Act & Assert
            mockMvc.perform(get("/account/view-profile/{selfID}", selfId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$").value("User not found"));
        }
    }
}