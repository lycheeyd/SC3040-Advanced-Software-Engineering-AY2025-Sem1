package com.service;

import com.repository.ApiKeyRepository;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyService(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    public String getApiKey(String keyName) {
        return apiKeyRepository.getApiKeyFromDatabase(keyName)
                .orElse("API Key not found for key name: " + keyName);
    }
}