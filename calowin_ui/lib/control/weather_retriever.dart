import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import 'dart:math';
import 'package:intl/intl.dart';

class WeatherRetriever {
  late double latitude;
  late double longitude;

  String formattedDate =
      DateFormat("yyyy-MM-dd'T'HH:mm:ss").format(DateTime.now());
  // Retrieve weather forecast from API
  Future<String> retrieveWeather() async {
    final url = Uri.parse(
        'https://api.data.gov.sg/v1/environment/2-hour-weather-forecast?$formattedDate');

    try {
      final response = await http.get(url);

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final areaMetadata = data['area_metadata'];
        final forecasts = data['items'][0]['forecasts'];

        // Find the closest area
        final nearestArea = _findNearestArea(latitude, longitude, areaMetadata);

        // Retrieve the forecast for the nearest area
        Map<String, dynamic>? nearestForecast;
        for (var forecast in forecasts) {
          if (forecast['area'] == nearestArea['name']) {
            nearestForecast = forecast;
            break;
          }
        }

        //print(nearestArea['name']);

        if (nearestForecast != null) {
          return nearestForecast['forecast'];
        } else {
          return 'Forecast not available';
        }
      } else if (response.statusCode == 404) {
        return 'Forecast not available';
      } else {
        return 'Failed to fetch weather data';
      }
    } on SocketException {
      return 'Network issue: Unable to reach the server.';
    } on TimeoutException {
      return 'Request timed out. Server might be down or too slow.';
    } catch (e) {
      return 'Error occurred here: $e';
    }
  }

  // Method to calculate the nearest area using the Haversine formula
  Map<String, dynamic> _findNearestArea(
      double userLat, double userLon, List<dynamic> areaMetadata) {
    late Map<String, dynamic> nearestArea;
    double shortestDistance = double.infinity;

    for (var area in areaMetadata) {
      final areaLat = area['label_location']['latitude'];
      final areaLon = area['label_location']['longitude'];
      final distance = _calculateDistance(userLat, userLon, areaLat, areaLon);

      if (distance < shortestDistance) {
        shortestDistance = distance;
        nearestArea = area;
      }
    }

    return nearestArea;
  }

  // Haversine formula to calculate distance between two coordinates
  double _calculateDistance(
      double lat1, double lon1, double lat2, double lon2) {
    const earthRadius = 6371; // Radius of the Earth in kilometers
    final dLat = _degreesToRadians(lat2 - lat1);
    final dLon = _degreesToRadians(lon2 - lon1);

    final a = sin(dLat / 2) * sin(dLat / 2) +
        cos(_degreesToRadians(lat1)) *
            cos(_degreesToRadians(lat2)) *
            sin(dLon / 2) *
            sin(dLon / 2);

    final c = 2 * atan2(sqrt(a), sqrt(1 - a));

    return earthRadius * c;
  }

  // Convert degrees to radians
  double _degreesToRadians(double degrees) {
    return degrees * pi / 180;
  }

  void setLocation(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }
}
