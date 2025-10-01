package com.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class NParkApiClient {
    private String datasetId = ""; // d_77d7ec97be83d44f61b85454f844382f for this specific data
    private String initiateUrl = "";

    public NParkApiClient(String datasetId) {
        this.datasetId = datasetId;
        this.initiateUrl = "https://api-open.data.gov.sg/v1/public/api/datasets/" + datasetId + "/initiate-download";
    }

    private String responseData = "";
    private String errorMessage = "";

    public void initiateDownload() {
        try {
            HttpURLConnection connection = setupConnection(initiateUrl, "GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {  // Status code 201
                String response = readResponse(connection);
                JSONObject initiateData = new JSONObject(response);

                // Check if the initiation was successful
                if (initiateData.getInt("code") == 0) {
                    pollDownload(datasetId);
                } else {
                    errorMessage = initiateData.getString("errMsg");
                    responseData = "";
                }
            } else {
                errorMessage = "Error initiating download: HTTP status " + responseCode;
                responseData = "";
            }

        } catch (MalformedURLException e) {
            errorMessage = "Malformed URL: " + e.getMessage();
        } catch (IOException e) {
            errorMessage = "I/O Error during initiation: " + e.getMessage();
        } catch (JSONException e) {
            errorMessage = "JSON Parsing Error: " + e.getMessage();
        } catch (Exception e) {
            errorMessage = "Unexpected error: " + e.getMessage();
        }
    }

    private void pollDownload(String datasetId) {
        String pollUrl = "https://api-open.data.gov.sg/v1/public/api/datasets/" + datasetId + "/poll-download";
        try {
            HttpURLConnection connection = setupConnection(pollUrl, "GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {  // Status code 201
                String response = readResponse(connection);
                JSONObject pollData = new JSONObject(response);

                // Check if the data is ready
                if (pollData.getInt("code") == 0) {
                    String downloadUrl = pollData.getJSONObject("data").getString("url");
                    downloadData(downloadUrl);
                } else {
                    errorMessage = pollData.getString("errMsg");
                    responseData = "";
                }
            } else {
                errorMessage = "Error polling download: HTTP status " + responseCode;
                responseData = "";
            }

        } catch (MalformedURLException e) {
            errorMessage = "Malformed URL in polling: " + e.getMessage();
        } catch (IOException e) {
            errorMessage = "I/O Error during polling: " + e.getMessage();
        } catch (JSONException e) {
            errorMessage = "JSON Parsing Error in polling response: " + e.getMessage();
        } catch (Exception e) {
            errorMessage = "Unexpected error during polling: " + e.getMessage();
        }
    }

    private void downloadData(String url) {
        try {
            HttpURLConnection connection = setupConnection(url, "GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {  // Status code 200
                responseData = readResponse(connection);
                errorMessage = "";
            } else {
                errorMessage = "Error downloading data: HTTP status " + responseCode;
                responseData = "";
            }

        } catch (MalformedURLException e) {
            errorMessage = "Malformed URL in download: " + e.getMessage();
        } catch (IOException e) {
            errorMessage = "I/O Error during download: " + e.getMessage();
        } catch (Exception e) {
            errorMessage = "Unexpected error during download: " + e.getMessage();
        }
    }

    private HttpURLConnection setupConnection(String urlString, String requestMethod) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(requestMethod);
        return connection;
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    public String getResponseData() {
        return responseData;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
