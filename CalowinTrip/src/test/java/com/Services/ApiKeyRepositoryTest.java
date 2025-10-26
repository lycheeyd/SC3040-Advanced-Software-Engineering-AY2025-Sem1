package com.Services;


import com.repository.ApiKeyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiKeyRepositoryTest {

    @Mock
    private DataSource dataSource;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private ApiKeyRepository apiKeyRepository;

    @BeforeEach
    void setUp() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    @DisplayName("getApiKeyFromDatabase should return key when found")
    void getApiKey_whenKeyFound_shouldReturnKey() throws SQLException {
        // Arrange
        String keyName = "Places API";
        String expectedKey = "AIza...";
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("ApiKey")).thenReturn(expectedKey);

        // Act
        Optional<String> actualKey = apiKeyRepository.getApiKeyFromDatabase(keyName);

        // Assert
        assertThat(actualKey).isPresent().contains(expectedKey);
    }

    @Test
    @DisplayName("getApiKeyFromDatabase should return empty when key not found")
    void getApiKey_whenKeyNotFound_shouldReturnEmpty() throws SQLException {
        // Arrange
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // Act
        Optional<String> actualKey = apiKeyRepository.getApiKeyFromDatabase("BAD_KEY");

        // Assert
        assertThat(actualKey).isEmpty();
    }

    @Test
    @DisplayName("getApiKeyFromDatabase should throw RuntimeException on SQLException")
    void getApiKey_onSqlException_shouldThrowRuntimeException() throws SQLException {
        // Arrange
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("DB error"));

        // Act & Assert
        assertThatThrownBy(() -> {
            apiKeyRepository.getApiKeyFromDatabase("ANY_KEY");
        }).isInstanceOf(RuntimeException.class)
                .hasMessage("Error retrieving API key from the database.");
    }
}