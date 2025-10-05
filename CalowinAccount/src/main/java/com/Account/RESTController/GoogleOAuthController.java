package com.Account.RESTController;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.gmail.GmailScopes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Account.Entities.EmailServiceProperties;

import java.io.StringReader;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

/**
 * Controller to facilitate manual OAuth flow for generating a Google Refresh Token.
 * THIS IS FOR DEVELOPMENT/DEBUGGING PURPOSES ONLY.
 */
@RestController
@RequestMapping("/oauth/google")
public class GoogleOAuthController {

    private final EmailServiceProperties emailProperties;
    private static final String REDIRECT_URI = "http://127.0.0.1:7860/oauth/google/callback";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "CalowinAuthHelper";

    @Autowired
    public GoogleOAuthController(EmailServiceProperties emailProperties) {
        this.emailProperties = emailProperties;
    }

    private GoogleAuthorizationCodeFlow createFlow() throws Exception {
        // Construct the client secrets object from properties
        String secretsJson = String.format(
                "{\"web\":{\"client_id\":\"%s\",\"client_secret\":\"%s\",\"auth_uri\":\"https://accounts.google.com/o/oauth2/auth\",\"token_uri\":\"https://oauth2.googleapis.com/token\"}}",
                emailProperties.getClientId(),
                emailProperties.getClientSecret()
        );

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new StringReader(secretsJson));

        return new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                clientSecrets,
                Collections.singletonList(GmailScopes.GMAIL_SEND))
                .setAccessType("offline") // This is crucial for getting a refresh token
                .setApprovalPrompt("force") // Forces prompt even if already approved
                .build();
    }

    /**
     * Step 1: Generates the Google Authorization URL for the user to visit.
     * Returns: A JSON response containing the URL.
     */
    @GetMapping("/auth-url")
    public ResponseEntity<Map<String, String>> getAuthorizationUrl() {
        try {
            GoogleAuthorizationCodeFlow flow = createFlow();
            String url = flow.newAuthorizationUrl()
                    .setRedirectUri(REDIRECT_URI)
                    .build();

            Map<String, String> response = new HashMap<>();
            response.put("message", "Open this URL in your browser to authorize access:");
            response.put("authorization_url", url);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "Error generating auth URL: " + e.getMessage()));
        }
    }

    /**
     * Step 2: The redirect URI that Google calls back after authorization.
     * Exchanges the authorization 'code' for a 'refreshToken'.
     * Displays the refresh token to the user.
     */
    @GetMapping("/callback")
    public ResponseEntity<Map<String, String>> handleCallback(@RequestParam("code") String code) {
        Map<String, String> response = new HashMap<>();

        try {
            GoogleAuthorizationCodeFlow flow = createFlow();

            GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                    .setRedirectUri(REDIRECT_URI)
                    .execute();

            String refreshToken = tokenResponse.getRefreshToken();

            if (refreshToken == null) {
                response.put("error", "Failed to retrieve Refresh Token. Access Token received successfully, but Refresh Token was null. This usually means 'approval_prompt' was not set to 'force' or access was already granted without 'offline' scope.");
                return ResponseEntity.status(500).body(response);
            }

            response.put("message", "SUCCESS! Your new Refresh Token is:");
            response.put("refresh_token", refreshToken);

            // Note: You must update your application.properties file with this token!

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Error exchanging code for token: " + e.getMessage());
            response.put("details", "Ensure your Client ID/Secret and Redirect URI are correct.");
            return ResponseEntity.status(500).body(response);
        }
    }
}
