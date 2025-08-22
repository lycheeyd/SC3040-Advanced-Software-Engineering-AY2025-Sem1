import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/location.dart';
import '../models/currentlocation.dart';
// Adjust the path based on your project structure

class ApiService {
  //final String baseUrl = "http://172.21.146.188:8080/api"; // VM URL
  final String baseUrl = "http://10.91.236.36:8080"; 
  ApiService();

  // Fetch API key by key name
Future<String> fetchApiKey(String keyName) async {
  final response = await http.get(Uri.parse('$baseUrl/api/keys/$keyName'));

  if (response.statusCode == 200) {
    // Return the API key from the response
    return response.body; // The body contains the API key as a string
  } else {
    throw Exception('Failed to load API key: ${response.body}');
  }
}

  // Fetch available locations
  Future<List<Location>> fetchAvailableLocations() async {
    final response = await http.get(Uri.parse('$baseUrl/trips/locations'));
    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);
      return data
          .map(
              (loc) => Location(loc['name'], loc['latitude'], loc['longitude']))
          .toList();
    } else {
      throw Exception('Failed to load locations');
    }
  }

  // Fetch current location
  Future<CurrentLocation> fetchCurrentLocation() async {
    final response =
        await http.get(Uri.parse('$baseUrl/trips/current-location'));
    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return CurrentLocation(
        name: data['name'],
        latitude: data['latitude'],
        longitude: data['longitude'],
      );
    } else {
      throw Exception('Failed to load current location');
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
      Location destination, String method, String user_id) async {
    final response = await http.post(
      Uri.parse('$baseUrl/trips/start'),
      headers: {'Content-Type': 'application/json'},
      body: json.encode({
        'userId': user_id, // Include userId

        'destination': {
          'name': destination.name,
          'latitude': destination.latitude,
          'longitude': destination.longitude,
        },
        'travelMethod': method,
      }),
    );

    if (response.statusCode == 200) {
      return json.decode(response.body);
    } else {
      throw Exception('Failed to start trip');
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
