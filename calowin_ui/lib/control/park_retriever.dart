import 'dart:convert';
import 'package:http/http.dart' as http;

class Park {
  final String name;
  final double distance;
  final Map<String, double> closestPoint;

  Park(
      {required this.name, required this.distance, required this.closestPoint});

  factory Park.fromJson(Map<String, dynamic> json) {
    return Park(
      name: json['name'],
      distance: json['distance'],
      closestPoint: {
        "Lat": json['closestPoint']['Lat'],
        "Lon": json['closestPoint']['Lon']
      },
    );
  }
}

class ParkRetriever {
  final String _baseUrl =
      'http://172.21.146.188:8080/central/wellness/parks'; // Replace with your backend URL

  // Function to retrieve parks based on user's coordinates
  Future<List<Park>> retrievePark(double userLat, double userLon) async {
    final url = Uri.parse('$_baseUrl?lat=$userLat&lon=$userLon');
    //print(url);
    //final url = Uri.parse('$_baseUrl/$userLat/$userLon');
    try {
      final response = await http.get(url);

      if (response.statusCode == 200) {
        List<dynamic> data = jsonDecode(response.body);

        // Convert the JSON response to a list of Park objects
        List<Park> parks = data.map((park) => Park.fromJson(park)).toList();
        return parks;
      } else {
        // Handle error responses
        print('Failed to retrieve parks: ${response.statusCode}');
        return [];
      }
    } catch (e) {
      // Handle network or parsing errors
      print('Error occurred while retrieving parks: $e');
      return [];
    }
  }
}
