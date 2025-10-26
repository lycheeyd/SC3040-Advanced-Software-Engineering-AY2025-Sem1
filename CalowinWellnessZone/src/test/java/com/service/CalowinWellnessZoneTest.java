package com.service;

import com.client.NParkApiClient; // <-- 1. Import this
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean; // <-- 2. Import this

@SpringBootTest
class CalowinWellnessZoneTest {

    @MockBean // <-- 3. Add this annotation
    private NParkApiClient nParkApiClient;

    @Test
    void contextLoads() {
        // By mocking the NParkApiClient, the Spring context
        // can now load successfully.
    }
}