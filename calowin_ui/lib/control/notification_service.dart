import 'dart:convert';
import 'package:http/http.dart' as http;
import 'dart:io';

class NotificationService {
  final String baseUrl = "https://sc3040G5-CalowinNotification.hf.space"; // Update with actual base URL

  Future<List<String>> fetchFriendRequests(String userId) async {
    final url = '$baseUrl/notifications/friend-requests/$userId';
    print(url);

    try {
      final response = await http.get(Uri.parse(url));

      if (response.statusCode == 200) {
        // Parse the JSON response
        List<dynamic> data = jsonDecode(response.body);
        final newdata = data.map((item) => item.toString()).toList();
        print(newdata);
        return newdata;
      } else {
        print("Server returned an error: ${response.statusCode} ${response.reasonPhrase}");
        throw HttpException("Failed to load notifications. Status code: ${response.statusCode}");
      }
    } on SocketException catch (_) {
      print("Network error: Unable to reach the server.");
      return [];
    } on FormatException {
      print("Data format error: Unable to parse the response.");
      return [];
    } catch (e) {
      print("Unexpected error: $e");
      return [];
    }
  }
}
