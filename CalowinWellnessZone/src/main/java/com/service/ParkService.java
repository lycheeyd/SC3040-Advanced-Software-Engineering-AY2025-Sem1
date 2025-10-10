package com.service;

import com.client.NParkApiClient;
import com.model.NPark;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Service
public class ParkService {

    private Map<String, Double> userCoordinate = new HashMap<>();
    private List<NPark> parks = new ArrayList<>();

    @Autowired
    private NParkApiClient parkApiService;

    public List<NPark> findNearbyParks(double userLat, double userLon) throws Exception {
        parkApiService.initiateDownload();
        String responseData = parkApiService.getResponseData();
        String errorMessage = parkApiService.getErrorMessage();

        if (errorMessage != null && !errorMessage.isEmpty()) {
            throw new Exception("Data download error: " + errorMessage);
        }

        if (responseData != null && !responseData.isEmpty()) {
            userCoordinate.put("Lat", userLat);
            userCoordinate.put("Lon", userLon);
            return extractParks(responseData, userCoordinate);
        } else {
            throw new Exception("No response data received from downloader.");
        }
    }

    private List<NPark> extractParks(String jsonData, Map<String, Double> userCoordinate) {
        JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray featuresArray = jsonObject.getJSONArray("features");

        for (int i = 0; i < featuresArray.length(); i++) {
            JSONObject feature = featuresArray.getJSONObject(i);
            JSONObject properties = feature.getJSONObject("properties");

            // Extract the park or nature reserve name from the properties
            String description = properties.getString("Description");
            String name = extractParkName(description);

            // Extract coordinates from the geometry section
            JSONObject geometry = feature.getJSONObject("geometry");
            List<Map<String, Double>> coordinates = extractCoordinates(geometry);

            // Create a new NPark instance and add it to the list
            if (!coordinates.isEmpty()) {
                NPark park = new NPark(coordinates, userCoordinate, name);
                parks.add(park);
            }
        }
        return parks;
    }

    private String extractParkName(String description) {
        int nameIndex = description.indexOf("<th>NAME</th> <td>") + "<th>NAME</th> <td>".length();
        int endIndex = description.indexOf("</td>", nameIndex);
        return description.substring(nameIndex, endIndex).trim();
    }

    private List<Map<String, Double>> extractCoordinates(JSONObject geometry) {
        List<Map<String, Double>> coordinates = new ArrayList<>();
        String type = geometry.optString("type");
        JSONArray pointsArray = geometry.optJSONArray("coordinates");

        try {
            if ("Polygon".equalsIgnoreCase(type) && pointsArray != null) {
                addCoordinatesFromPointsArray(pointsArray, coordinates);
            } else if ("MultiPolygon".equalsIgnoreCase(type) && pointsArray != null) {
                for (int i = 0; i < pointsArray.length(); i++) {
                    JSONArray polygonArray = pointsArray.getJSONArray(i);
                    addCoordinatesFromPointsArray(polygonArray, coordinates);
                }
            } else {
                System.out.println("Unsupported geometry type: " + type);
            }
        } catch (Exception e) {
            System.out.println("Error extracting coordinates: " + e.getMessage());
        }
        return coordinates;
    }

    private void addCoordinatesFromPointsArray(JSONArray pointsArray, List<Map<String, Double>> coordinates) {
        if (pointsArray == null || pointsArray.length() == 0)
            return;

        // Assuming the Polygon structure has one or more rings, with the first ring
        // being the outer boundary
        for (int i = 0; i < pointsArray.length(); i++) {
            JSONArray ringArray = pointsArray.optJSONArray(i);
            if (ringArray != null) {
                for (int j = 0; j < ringArray.length(); j++) {
                    JSONArray pointArray = ringArray.optJSONArray(j);
                    if (pointArray != null && pointArray.length() >= 2) {
                        Map<String, Double> coordinate = new HashMap<>();
                        coordinate.put("Lat", pointArray.optDouble(1));
                        coordinate.put("Lon", pointArray.optDouble(0));
                        coordinates.add(coordinate);
                    }
                }
            }
        }
    }

    private void sortParks() {
        if (parks.isEmpty()) {
            System.out.println("No parks found.");
        } else
            Collections.sort(parks, Comparator.comparingDouble(NPark::getDistance));
    }

    // Additional methods to set user coordinates can be added here
    public void setUserCoordinate(double lat, double lon) {
        userCoordinate.put("Lat", lat);
        userCoordinate.put("Lon", lon);
    }

    public List<NPark> getParks() {
        return parks;
    }

    public void printAllParks() {
        sortParks();
        if (parks.isEmpty()) {
            System.out.println("No parks found.");
        } else {
            for (NPark park : parks) {
                System.out.println("Name: " + park.getName() + " Distance: " + park.getDistance() + " Closest Point: "
                        + park.getClosestPoint());
            }
        }
    }

}