package com.WellnessZone;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NParkExtracter {
    private Map<String, Double> userCoordinate = new HashMap<>();
    private List<NPark> parks = new ArrayList<>();
    private NParkDataDownloader downloader = new NParkDataDownloader("d_77d7ec97be83d44f61b85454f844382f");

    public NParkExtracter(double userLat, double userLong) throws Exception {
        userCoordinate.put("Lat", userLat);
        userCoordinate.put("Lon", userLong);
        downloader.initiateDownload();
        String responseData = downloader.getResponseData();
        String errorMessage = downloader.getErrorMessage();
        // System.out.println("Response Data: " + responseData);
        if (errorMessage != null && !errorMessage.isEmpty()) {
            throw new Exception("Data download error: " + errorMessage);
        }

        if (responseData != null && !responseData.isEmpty()) {
            extractParks(responseData);
        } else {
            throw new Exception("No response data received from downloader.");
        }
    }

    private void extractParks(String jsonData) {
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
            NPark park = new NPark(coordinates, userCoordinate, name);
            parks.add(park);
        }
    }

    private String extractParkName(String description) {
        // Extract the park or nature reserve name from the description
        int nameIndex = description.indexOf("<th>NAME</th> <td>") + "<th>NAME</th> <td>".length();
        int endIndex = description.indexOf("</td>", nameIndex);
        return description.substring(nameIndex, endIndex).trim();
    }

    private List<Map<String, Double>> extractCoordinates(JSONObject geometry) {
        List<Map<String, Double>> coordinates = new ArrayList<>();
        String type = geometry.optString("type");
        JSONArray pointsArray = geometry.optJSONArray("coordinates");

        try {
            if ("Polygon".equalsIgnoreCase(type)) {
                // Extract from a single Polygon
                if (pointsArray != null) {
                    addCoordinatesFromPointsArray(pointsArray, coordinates);
                }
            } else if ("MultiPolygon".equalsIgnoreCase(type)) {
                // Extract from multiple Polygons in a MultiPolygon
                if (pointsArray != null) {
                    for (int i = 0; i < pointsArray.length(); i++) {
                        JSONArray polygonArray = pointsArray.getJSONArray(i);
                        addCoordinatesFromPointsArray(polygonArray, coordinates);
                    }
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
        if (pointsArray == null || pointsArray.length() == 0) {
            System.out.println("No coordinates available.");
            return;
        }

        // Assuming the Polygon structure has one or more rings, with the first ring
        // being the outer boundary
        for (int i = 0; i < pointsArray.length(); i++) {
            JSONArray ringArray = pointsArray.optJSONArray(i);
            if (ringArray != null) {
                for (int j = 0; j < ringArray.length(); j++) {
                    JSONArray pointArray = ringArray.optJSONArray(j);
                    if (pointArray != null && pointArray.length() >= 2) {
                        double longitude = pointArray.optDouble(0);
                        double latitude = pointArray.optDouble(1);

                        // Create a map with "Lat" and "Lon" keys
                        Map<String, Double> coordinate = new HashMap<>();
                        coordinate.put("Lat", latitude);
                        coordinate.put("Lon", longitude);

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

    // can uncomment this for testing
    // public static void main(String[] args) {
    // NParkExtracter retriever = new NParkExtracter(1.385170, 103.79615);
    // retriever.printAllParks();
    // }
}
