package com.Services;


import com.controller.ApiKeyController;
import com.service.ApiKeyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApiKeyController.class)
class ApiKeyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApiKeyService apiKeyService;

    @Test
    @DisplayName("GET /api/keys/{keyName} should return the key")
    void getApiKey_shouldReturnKey() throws Exception {
        // Arrange
        String keyName = "Places API";
        String keyValue = "AIza...ThisIsADangerousKey";
        when(apiKeyService.getApiKey(keyName)).thenReturn(keyValue);

        // Act & Assert
        mockMvc.perform(get("/api/keys/{keyName}", keyName))
                .andExpect(status().isOk())
                .andExpect(content().string(keyValue));
    }
}