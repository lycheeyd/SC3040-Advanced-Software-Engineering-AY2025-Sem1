package com;

import com.Account.Entities.FriendStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

// Specify the client we are testing
@RestClientTest(ExternalServiceClient.class)
@AutoConfigureWebClient(registerRestTemplate = true)
// Provide a dummy property for the URL prefix, as it's required by the client bean
@TestPropertySource(properties = "friend.module.urlPrefix=http://mock-friend-service.com/api")
class ExternalServiceClientTest {

    @Autowired
    private ExternalServiceClient externalServiceClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("getFriendStatus should return FriendStatus on successful API call")
    void getFriendStatus_whenApiReturnsSuccess_shouldReturnFriendStatus() throws Exception {
        // Arrange
        String selfID = "user1";
        String otherID = "user2";
        String expectedUrl = "http://mock-friend-service.com/api/friend-requests/status?userId1=user1&userId2=user2";

        // Expect a GET request to the URL and respond with a JSON success message
        // The body "FRIEND" is how the FriendStatus enum will be serialized to JSON
        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(FriendStatus.FRIEND), MediaType.APPLICATION_JSON));

        // Act
        FriendStatus status = externalServiceClient.getFriendStatus(selfID, otherID);

        // Assert
        assertThat(status).isEqualTo(FriendStatus.FRIEND);
        mockServer.verify(); // Ensures that the expected request was made
    }

    @Test
    @DisplayName("getFriendStatus should throw RuntimeException on server error")
    void getFriendStatus_whenApiReturnsError_shouldThrowRuntimeException() {
        // Arrange
        String selfID = "user1";
        String otherID = "user2";
        String expectedUrl = "http://mock-friend-service.com/api/friend-requests/status?userId1=user1&userId2=user2";

        // Expect a GET request and respond with a 500 Internal Server Error
        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            externalServiceClient.getFriendStatus(selfID, otherID);
        });

        assertThat(exception.getMessage()).isEqualTo("Failed to retrieve friend status");
        mockServer.verify();
    }
}