import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:calowin/control/words2widget_converter.dart';

class LeaderboardItem {
  final String _name;
  final String _userId;
  final int _carbonPoint;
  final int _caloriePoint;
  final Image? _carbonMedal;
  final Image? _calorieMedal;

  LeaderboardItem({
    required String name,
    required String userId,
    required int caloriePoint,
    required int carbonPoint,
    required Image? calorieMedal,
    required Image? carbonMedal,
  })  : _name = name,
        _userId = userId,
        _carbonPoint = carbonPoint,
        _caloriePoint = caloriePoint,
        _calorieMedal = calorieMedal,
        _carbonMedal = carbonMedal;

  // Getters
  String get name => _name;
  String get userId => _userId;
  int get carbonPoint => _carbonPoint;
  int get caloriePoint => _caloriePoint;
  Image? get carbonMedal => _carbonMedal;
  Image? get calorieMedal => _calorieMedal;



  factory LeaderboardItem.fromJson(Map<String, dynamic> json) {
    return LeaderboardItem(
      userId: json['userId'],
      carbonPoint: json['totalCarbonSaved'],
      caloriePoint: json['totalCalorieBurnt'],
      carbonMedal: Words2widgetConverter.convert(json['carbonMedal']),
      calorieMedal: Words2widgetConverter.convert(json['calorieMedal']),
      name: json['userName'],
    );
  }
}

class LeaderboardRetriever {
  final String _baseUrl =
      'https://sc3040G5-CalowinSpringNode.hf.space'; // Replace with your backend URL

  // Function to retrieve LeaderboardItems based on user's coordinates
  Future<List<LeaderboardItem>> retrieveCarbonLeaderboard(String userId) async {
    final url = Uri.parse('$_baseUrl/carbon?userId=$userId');
    //print(url);

    try {
      final response = await http.get(url);

      if (response.statusCode == 200) {
        List<dynamic> data = jsonDecode(response.body);

        // Convert the JSON response to a list of LeaderboardItem objects
        List<LeaderboardItem> leaderboardItems = data
            .map((item) => LeaderboardItem.fromJson(item))
            .toList();
        
        return leaderboardItems;
      } else {
        // Handle error responses
        print('Failed to retrieve carbon leaderboard: ${response.statusCode}');
        return [];
      }
    } catch (e) {
      // Handle network or parsing errors
      print('Error occurred while retrieving carbon leaderboard: $e');
      return [];
    }
  }

  Future<List<LeaderboardItem>> retrieveCalorieLeaderboard(String userId) async {
    final url = Uri.parse('$_baseUrl/calories?userId=$userId');
    //print(url);

    try {
      final response = await http.get(url);

      if (response.statusCode == 200) {
        List<dynamic> data = jsonDecode(response.body);

        // Convert the JSON response to a list of LeaderboardItem objects
        List<LeaderboardItem> leaderboardItems = data
            .map((item) => LeaderboardItem.fromJson(item))
            .toList();
        
        return leaderboardItems;
      } else {
        // Handle error responses
        print('Failed to retrieve calorie leaderboard: ${response.statusCode}');
        return [];
      }
    } catch (e) {
      // Handle network or parsing errors
      print('Error occurred while retrieving carbon leaderboard: $e');
      return [];
    }
  }
}
