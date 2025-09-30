package com.example;

import com.repository.ApiKeyRepository;
import com.service.ApiKeyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {

    // Create a mock of the ApiKeyRepository dependency
    @Mock
    private ApiKeyRepository apiKeyRepository;

    // Create an instance of ApiKeyService and inject the mock repository into it
    @InjectMocks
    private ApiKeyService apiKeyService;

    @Test
    @DisplayName("getApiKey should return the API key when the key exists")
    void getApiKey_whenKeyExists_shouldReturnApiKey() {
        // Arrange
        String keyName = "GOOGLE_MAPS_API_KEY";
        String expectedApiKey = "AIzaSyC...exampleKey...3g";

        // Stub the repository method to return an Optional containing the key
        when(apiKeyRepository.getApiKeyFromDatabase(keyName)).thenReturn(Optional.of(expectedApiKey));

        // Act
        String actualApiKey = apiKeyService.getApiKey(keyName);

        // Assert
        // Check that the returned value is the expected API key
        assertThat(actualApiKey).isEqualTo(expectedApiKey);

        // Verify that the findApiKeyByName method was called exactly once with the correct argument
        verify(apiKeyRepository).getApiKeyFromDatabase(keyName);
    }

    @Test
    @DisplayName("getApiKey should return a 'not found' message when the key does not exist")
    void getApiKey_whenKeyDoesNotExist_shouldReturnNotFoundMessage() {
        // Arrange
        String keyName = "NON_EXISTENT_KEY";
        String expectedMessage = "API Key not found for key name: " + keyName;

        // Stub the repository method to return an empty Optional
        when(apiKeyRepository.getApiKeyFromDatabase(keyName)).thenReturn(Optional.empty());

        // Act
        String actualMessage = apiKeyService.getApiKey(keyName);

        // Assert
        // Check that the returned value is the expected error message
        assertThat(actualMessage).isEqualTo(expectedMessage);

        // Verify that the findApiKeyByName method was called exactly once with the correct argument
        verify(apiKeyRepository).getApiKeyFromDatabase(keyName);
    }
}