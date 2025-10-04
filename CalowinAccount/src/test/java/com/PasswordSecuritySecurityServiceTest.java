package com;

import com.Account.Services.PasswordSecurityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PasswordSecurityServiceTest {

    private final PasswordSecurityService passwordSecurityService = new PasswordSecurityService();

    @Test
    @DisplayName("isPasswordValid should pass for a valid password")
    void isPasswordValid_whenPasswordsMatchAndAreValid_thenSucceeds() throws Exception {
        // This should not throw an exception
        passwordSecurityService.isPasswordValid("ValidPass1!", "ValidPass1!");
    }

    @Test
    @DisplayName("isPasswordValid should throw exception for mismatched passwords")
    void isPasswordValid_whenPasswordsDoNotMatch_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            passwordSecurityService.isPasswordValid("ValidPass1!", "DifferentPass1!");
        });
        assertThat(exception.getMessage()).isEqualTo("Password does not match");
    }

    @Test
    @DisplayName("isPasswordValid should throw exception for invalid password pattern")
    void isPasswordValid_whenPasswordIsInvalid_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            passwordSecurityService.isPasswordValid("invalid", "invalid");
        });
        assertThat(exception.getMessage()).contains("Invalid password");
    }

    @Test
    @DisplayName("generateRandomPassword should generate a password of correct length")
    void generateRandomPassword_shouldReturnPasswordOfLength12() {
        String password = passwordSecurityService.generateRandomPassword();
        assertThat(password).hasSize(12);
        // Check if it contains at least one special character (high probability)
        assertThat(password).matches(".*[!@#$%^&*()\\-_=+<>?].*");
    }
}