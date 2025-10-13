import 'dart:convert';
import 'package:calowin/common/user_profile.dart';
import 'package:http/http.dart' as http;

class FriendsController {
  final String _baseUrl =
      'https://sc3040G5-CalowinFriends.hf.space'; // Replace with your backend URL

  // Function to retrieve LeaderboardItems based on user's coordinates
  Future<List<UserProfile>> retrieveFriendList(String userId) async {
    final url = Uri.parse('$_baseUrl/friend-requests/friends/$userId');
    print(url);

    try {
      final response = await http.get(url);
      print(response.body);

      if (response.statusCode == 200) {
        List<dynamic> data = jsonDecode(response.body);
        //print(data);
        // Convert the JSON response to a list of LeaderboardItem objects
        List<UserProfile> friends = data
            .map((item) => UserProfile.receiverFromJson(item))
            .toList();
        
        return friends;
      } else {
        // Handle error responses
        //print(response.body);
        print('Failed to retrieve friend list: ${response.statusCode}');
        return [];
      }
    } catch (e) {
      // Handle network or parsing errors
      print('Error occurred while retrieving friend list: $e');
      return [];
    }
  }

  Future<List<UserProfile>> retrieveRequesterList(String userId) async {
    final url = Uri.parse('$_baseUrl/friend-requests/pending/$userId');
    //print(url);

    try {
      final response = await http.get(url);
      if (response.statusCode == 200) {
        List<dynamic> data = jsonDecode(response.body);
        //print(data);
        // Convert the JSON response to a list of LeaderboardItem objects
        List<UserProfile> requester = data
            .map((item) => UserProfile.senderFromJson(item))
            .toList();
        
        return requester;
      } else {
        // Handle error responses
        //print(response.body);
        print('Failed to retrieve requester list: ${response.statusCode}');
        return [];
      }
    } catch (e) {
      // Handle network or parsing errors
      print('Error occurred while requester friend list: $e');
      return [];
    }
  }

  Future<bool> removeFriend(String userId,String otherUserId) async {
    final url = Uri.parse('$_baseUrl/friend-requests/remove?userId=$userId&friendId=$otherUserId');

    try {
      final response = await http.post(url);
      if (response.statusCode == 200) {
        //print(response.body);  
        return true;
      } else {
        // Handle error responses
        //print(response.body);
        print('Failed to remove friend: ${response.statusCode}');
        return false;
      }
    } catch (e) {
      // Handle network or parsing errors
      print('Error occurred while removing friend: $e');
      return false;
    }
  }

  Future<bool> requestFriend(String userId,String otherUserId) async {
    final url = Uri.parse('$_baseUrl/friend-requests/send?senderId=$userId&receiverId=$otherUserId');

    try {
      final response = await http.post(url);
      if (response.statusCode == 200) {
        //print(response.body);  
        return true;
      } else {
        // Handle error responses
        //print(response.body);
        print('Failed to remove friend: ${response.statusCode}');
        return false;
      }
    } catch (e) {
      // Handle network or parsing errors
      print('Error occurred while removing friend: $e');
      return false;
    }
  }

  Future<bool> cancelRequest(String userId,String otherUserId) async {
    final url = Uri.parse('$_baseUrl/friend-requests/cancel?senderId=$userId&receiverId=$otherUserId');

    try {
      final response = await http.post(url);
      if (response.statusCode == 200) {
        //print(response.body);  
        return true;
      } else {
        // Handle error responses
        print(response.body);
        print('Failed to cancel request: ${response.statusCode}');
        return false;
      }
    } catch (e) {
      // Handle network or parsing errors
      print('Error occurred while cancelling request: $e');
      return false;
    }
  }

  Future<bool> acceptFriend(String userId,String otherUserId) async {
    final url = Uri.parse('$_baseUrl/friend-requests/accept?senderId=$otherUserId&receiverId=$userId');

    try {
      final response = await http.post(url);
      if (response.statusCode == 200) {
        //print(response.body);  
        return true;
      } else {
        // Handle error responses
        print(response.body);
        print('Failed to accept friend: ${response.statusCode}');
        return false;
      }
    } catch (e) {
      // Handle network or parsing errors
      print('Error occurred while accepting friend: $e');
      return false;
    }
  }

  Future<bool> rejectFriend(String userId,String otherUserId) async {
    final url = Uri.parse('$_baseUrl/friend-requests/reject?senderId=$otherUserId&receiverId=$userId');

    try {
      final response = await http.post(url);
      if (response.statusCode == 200) {
        //print(response.body);  
        return true;
      } else {
        // Handle error responses
        print(response.body);
        print('Failed to reject friend: ${response.statusCode}');
        return false;
      }
    } catch (e) {
      // Handle network or parsing errors
      print('Error occurred while reject friend: $e');
      return false;
    }
  }

  Future<List<UserProfile>> searchUser(String search, String userId) async {
    final url = Uri.parse('$_baseUrl/api/users/search?searchTerm=$search&currentUserId=$userId');

    try {
      final response = await http.get(url);
      if (response.statusCode == 200) {
        List<dynamic> data = jsonDecode(response.body);
        //print(data);
        // Convert the JSON response to a list of LeaderboardItem objects
        List<UserProfile> result = data
            .map((item) => UserProfile.userInfoFromJson(item))
            .toList();
        return result;
      } else {
        // Handle error responses
        //print(response.body);
        print('Failed to retrieve search result: ${response.statusCode}');
        return [];
      }
    } catch (e) {
      // Handle network or parsing errors
      print('Error occurred while retrieving search result: $e');
      return [];
    }
  }
}