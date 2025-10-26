package com.service;

import com.client.NParkApiClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class NParkApiClientTest {

    private MockWebServer mockWebServer;
    private NParkApiClient apiClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        // Get the mock server's URL
        String baseUrl = mockWebServer.url("/").toString();
        String initiateUrl = baseUrl + "initiate";
        String pollUrl = baseUrl + "poll";

        // Instantiate the client using our new test constructor
        apiClient = new NParkApiClient("test-dataset-id", initiateUrl, pollUrl);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("Should fetch data successfully on happy path")
    void initiateDownload_happyPath() {
        // Arrange
        // 1. Mock the initiate call (returns 201)
        mockWebServer.enqueue(new MockResponse().setResponseCode(201));

        // 2. Mock the poll call (returns 201 with a URL to the data)
        String dataUrl = mockWebServer.url("/real-data.json").toString();
        String pollBody = "{\"code\":0, \"data\":{\"url\":\"" + dataUrl + "\"}}";
        mockWebServer.enqueue(new MockResponse().setResponseCode(201).setBody(pollBody));

        // 3. Mock the final data call (returns 200 with GeoJSON)
        String fakeJsonData = "{\"type\":\"FeatureCollection\",\"features\":[]}";
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(fakeJsonData));

        // Act
        apiClient.initiateDownload();

        // Assert
        assertThat(apiClient.getResponseData()).isEqualTo(fakeJsonData);
        assertThat(apiClient.getErrorMessage()).isEmpty();
    }

    @Test
    @DisplayName("Should set error message if initiate call fails")
    void initiateDownload_whenInitiateFails() {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setResponseCode(500)); // Server error

        // Act
        apiClient.initiateDownload();

        // Assert
        assertThat(apiClient.getResponseData()).isEmpty();
        assertThat(apiClient.getErrorMessage()).contains("Failed to initiate download");
    }

    @Test
    @DisplayName("Should set error message if poll call fails")
    void initiateDownload_whenPollFails() {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setResponseCode(201)); // Initiate OK
        mockWebServer.enqueue(new MockResponse().setResponseCode(500)); // Poll fails

        // Act
        apiClient.initiateDownload();

        // Assert
        assertThat(apiClient.getResponseData()).isEmpty();
        assertThat(apiClient.getErrorMessage()).contains("Failed to poll for download URL");
    }

    @Test
    @DisplayName("Should set error message if data download fails")
    void initiateDownload_whenDataDownloadFails() {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setResponseCode(201)); // Initiate OK

        String dataUrl = mockWebServer.url("/real-data.json").toString();
        String pollBody = "{\"code\":0, \"data\":{\"url\":\"" + dataUrl + "\"}}";
        mockWebServer.enqueue(new MockResponse().setResponseCode(201).setBody(pollBody)); // Poll OK

        mockWebServer.enqueue(new MockResponse().setResponseCode(500)); // Data download fails

        // Act
        apiClient.initiateDownload();

        // Assert
        assertThat(apiClient.getResponseData()).isEmpty();
        assertThat(apiClient.getErrorMessage()).contains("Error downloading data");
    }
}