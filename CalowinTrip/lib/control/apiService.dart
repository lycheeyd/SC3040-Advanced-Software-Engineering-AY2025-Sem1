import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'location.dart';
import 'current_location.dart';

class ApiService {
  final String baseUrl = "http://192.168.18.71:8080"; // VM URL
  ApiService();

  static Future<String?> fetchUrl(Uri uri, {Map<String, String>? headers}) async {
    try {
      final response = await http.get(uri, headers:headers);
      if(response.statusCode == 200){
        return response.body;
      }
    } catch (e){
      debugPrint(e.toString());
    }
    return null;
  } 

  Future<String> fetchApiKey(String keyName) async {
  final response = await http.get(Uri.parse('$baseUrl/api/keys/$keyName'));

  if (response.statusCode == 200) {
    // Return the API key from the response
    return response.body; // The body contains the API key as a string
  } else {
    throw Exception('Failed to load API key: ${response.body}');
  }
}
  // Fetch available travel methods
  Future<List<String>> fetchTravelMethods() async {
    final response = await http.get(Uri.parse('$baseUrl/trips/methods'));
    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);
      return List<String>.from(data);
    } else {
      throw Exception('Failed to load travel methods');
    }
  }

 // Start a trip with a given destination and travel method
Future<Map<String, dynamic>> startTrip(
    Location destination,
    String method,
    String userId,
    CurrentLocation currentLocation,
  ) async {
    final url = Uri.parse('$baseUrl/trips/start');

    // Construct the request body
    final requestBody = {
      'userId': userId,
      'destination': {
        'name': destination.name,
        'latitude': destination.latitude,
        'longitude': destination.longitude,
      },
      'travelMethod': method,
      'currentLocation': {
        'latitude': currentLocation.latitude,
        'longitude': currentLocation.longitude,
        'name': currentLocation.name,
      },
    };

    // Print the values being posted to the backend for debugging
    print('Posting the following data to the backend at $url:');
    print(jsonEncode(requestBody));

    // Make the POST request
    final response = await http.post(
      url,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(requestBody),
    );

    // Check for a successful response and handle it
    if (response.statusCode == 200) {
      print('Trip started successfully. Response data: ${response.body}');
      return jsonDecode(response.body);
    } else {
      // Handle unsuccessful response and log details
      print('Failed to start trip. Status code: ${response.statusCode}');
      print('Error response body: ${response.body}');
      throw Exception('Failed to start trip: ${response.body}');
    }
  }



  // Fetch achievement progress from the backend
  Future<Map<String, dynamic>> getAchievementProgress() async {
    final response =
        await http.get(Uri.parse(baseUrl + "/achievements/progress"));

    if (response.statusCode == 200) {
      return json.decode(response.body);
    } else {
      throw Exception("Failed to load achievements");
    }
  }

  // Send trip metrics to the backend
  Future<void> addTripMetrics(int carbonSaved, int caloriesBurnt) async {
    final response = await http.post(Uri.parse(baseUrl +
        "/achievements/addTripMetrics?carbonSaved=$carbonSaved&caloriesBurnt=$caloriesBurnt"));

    if (response.statusCode != 200) {
      throw Exception("Failed to add trip metrics");
    }
  }

  
}
