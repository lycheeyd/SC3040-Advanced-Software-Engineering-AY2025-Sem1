import 'dart:convert';
import 'package:calowin/common/user_profile.dart';
import 'package:http/http.dart' as http;

class UserRetriever {
  Future<UserProfile> retrieveFriend(String userId, String otherId) async {
    final String _baseUrl = 'https://sc3040G5-CalowinSpringNode.hf.space/central/account/view-profile'; // Replace with your backend URL
    // Endpoint format: /selfid/otherid
    final url = Uri.parse('$_baseUrl/$userId/$otherId');
    //print(url);

    try {
      final response = await http.get(url);

      if (response.statusCode == 200) {
        var data = jsonDecode(response.body);

        //print(data);
        // Parse JSON response into a UserProfile object
        data = data['UserObject'];
        UserProfile user = UserProfile.othersFromJson(data);

        return user;
      } else {
        // Handle error responses
        print('Failed to retrieve user: ${response.statusCode}, ${response.body}');
        return UserProfile(name: "Error retrieving user", userID: "Error retrieving user");
      }
    } catch (e) {
      // Handle network or parsing errors
      print('Error occurred while retrieving user: $e');
      return UserProfile(name: "Unable to connect to server", userID: "Unable to connect to server");
    }
  }

  Future<UserProfile> retrieveSelf(String userId) async {
    final String _baseUrl = 'https://sc3040G5-CalowinSpringNode.hf.space/central/account/view-profile'; // Replace with your backend URL
    // Endpoint format: /selfid/otherid
    final url = Uri.parse('$_baseUrl/$userId');
    //print(url);

    try {
      final response = await http.get(url);

      if (response.statusCode == 200) {
        var data = jsonDecode(response.body);

        //print(data);
        // Parse JSON response into a UserProfile object
        data = data['UserObject'];
        UserProfile user = UserProfile.fromJson(data);

        return user;
      } else {
        // Handle error responses
        print('Failed to retrieve user: ${response.statusCode}, ${response.body}');
        return UserProfile(name: "Error retrieving user", userID: "Error retrieving user");
      }
    } catch (e) {
      // Handle network or parsing errors
      print('Error occurred while retrieving user: $e');
      return UserProfile(name: "Unable to connect to server", userID: "Unable to connect to server");
    }
  }
}
