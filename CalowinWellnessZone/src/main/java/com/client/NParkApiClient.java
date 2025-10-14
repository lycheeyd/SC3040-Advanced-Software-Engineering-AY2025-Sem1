package com.client;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class NParkApiClient {

    private final String datasetId;
    private final String initiateUrl;
    private final String pollUrl;
    private final HttpClient client;

    private String responseData = "";
    private String errorMessage = "";

    public NParkApiClient(@Value("${np.park.datasetId}") String datasetId) {
        this.datasetId = datasetId;
        this.initiateUrl = "https://api-open.data.gov.sg/v1/public/api/datasets/" + datasetId + "/initiate-download";
        this.pollUrl = "https://api-open.data.gov.sg/v1/public/api/datasets/" + datasetId + "/poll-download";
        this.client = HttpClient.newHttpClient();
    }

    public void initiateDownload() {
        try {
            // Step 1: Initiate the download
            HttpRequest initialRequest = HttpRequest.newBuilder().uri(URI.create(initiateUrl)).build();

            client.sendAsync(initialRequest, HttpResponse.BodyHandlers.ofString())
                    // Step 2: Poll for the download URL
                    .thenCompose(initiateResponse -> {
                        if (initiateResponse.statusCode() != 201) {
                            throw new RuntimeException("Failed to initiate download. Status: " + initiateResponse.statusCode());
                        }
                        HttpRequest pollRequest = HttpRequest.newBuilder().uri(URI.create(pollUrl)).build();
                        return client.sendAsync(pollRequest, HttpResponse.BodyHandlers.ofString());
                    })
                    // Step 3: Download the actual data
                    .thenCompose(pollResponse -> {
                        if (pollResponse.statusCode() != 201) {
                            throw new RuntimeException("Failed to poll for download URL. Status: " + pollResponse.statusCode());
                        }
                        JSONObject jsonResponse = new JSONObject(pollResponse.body());
                        if (jsonResponse.getInt("code") != 0) {
                            throw new RuntimeException("API Error: " + jsonResponse.getString("errMsg"));
                        }
                        String finalDataUrl = jsonResponse.getJSONObject("data").getString("url");
                        HttpRequest dataRequest = HttpRequest.newBuilder().uri(URI.create(finalDataUrl)).build();
                        return client.sendAsync(dataRequest, HttpResponse.BodyHandlers.ofString());
                    })
                    .whenComplete((dataResponse, error) -> {
                        if (error != null) {
                            this.errorMessage = "Failed to retrieve data: " + error.getMessage();
                            this.responseData = "";
                        } else if (dataResponse.statusCode() != 200) {
                            this.errorMessage = "Error downloading data: HTTP status " + dataResponse.statusCode();
                            this.responseData = "";
                        } else {
                            this.responseData = dataResponse.body();
                            this.errorMessage = "";
                        }
                    }).join(); // .join() waits for the async process to complete

        } catch (Exception e) {
            this.errorMessage = "Unexpected error during download: " + e.getMessage();
            this.responseData = "";
        }
    }

    public String getResponseData() {
        return responseData;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}