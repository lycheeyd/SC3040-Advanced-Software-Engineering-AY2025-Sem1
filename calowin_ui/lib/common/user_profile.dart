import 'package:flutter/material.dart';

enum UserStatus { FRIEND, REQUESTSENT, STRANGER, REQUESTRECIEVED }

class UserProfile extends ChangeNotifier{
  late String _name;
  late String? _email;
  late String _userID;
  late String? _bio;
  late double? _weight;
  late int? _carbonSaved;
  late int? _calorieBurn;
  late List<String>? _badges;
  late UserStatus? _status;

  // Constructor
  UserProfile({
    required String name,
    String? email,
    required String userID,
    String? bio,
    double? weight,
    int? carbonSaved,
    int? calorieBurn,
    List<String>? badges,
    UserStatus? status,
  }) {
    _name = name;
    _email = email;
    _userID = userID;
    _bio = bio;
    _weight = weight;
    _carbonSaved = carbonSaved;
    _calorieBurn = calorieBurn;
    _badges = badges;
    _status = status;
  }

  static T stringToEnum<T>(String value, List<T> enumValues) {
    return enumValues.firstWhere((e) => e.toString().split('.').last == value, orElse: () => throw ArgumentError('No matching enum value for $value'));
  }


  // Factory constructor for deserialization from LoginResponseDTO
  factory UserProfile.fromJson(Map<String, dynamic> json) {
    return UserProfile(
      name: json['name'] as String,
      email: json['email'] as String?,
      userID: json['userID'] as String,
      bio: json['bio'] as String?,
      weight: (json['weight'] as num?)?.toDouble(),  
      carbonSaved: (json['totalCarbonSaved'] as num?)?.toInt(),
      calorieBurn: (json['totalCalorieBurnt'] as num?)?.toInt(),
      badges: [json['carbonMedal'] as String, json['calorieMedal'] as String]
    );
  }

  factory UserProfile.editfromJson(Map<String, dynamic> json) {
    return UserProfile(
      name: json['name'] as String,
      userID: json['userID'] as String,
      bio: json['bio'] as String?,
      weight: (json['weight'] as num?)?.toDouble(),  
    );
  }

  factory UserProfile.othersFromJson(Map<String, dynamic> json) {
    return UserProfile(
      name: json['name'] as String,
      userID: json['userID'] as String,
      bio: json['bio'] as String?,
      status:  stringToEnum((json['friendStatus']) as String,UserStatus.values),
      carbonSaved: (json['totalCarbonSaved'] as num?)?.toInt(),
      calorieBurn: (json['totalCalorieBurnt'] as num?)?.toInt(),
      badges: [json['carbonMedal'] as String, json['calorieMedal'] as String]
    );
  }

  factory UserProfile.receiverFromJson(Map<String, dynamic> json) {
    return UserProfile(
      name: json['friendUserName'] as String,
      userID: json['friendUserId'] as String,
    );
  }

  factory UserProfile.senderFromJson(Map<String, dynamic> json) {
    return UserProfile(
      name: json['userName'] as String,
      userID: json['userId'] as String,
    );
  }

  factory UserProfile.userInfoFromJson(Map<String, dynamic> json) {
    return UserProfile(
      name: json['name'] as String,
      userID: json['userId'] as String,
    );
  }

  //function to be called when made changes to the current user to alert all other pages
  void updateProfile(){
    // print("Update Profile called, the new profile as follow:");
    // print("name: $_name");
    // print("bio: $_bio");
    // print("weight: $_weight");
    notifyListeners();
  }

  UserProfile copyProfile(UserProfile profile) {
    return UserProfile(
      name: _name = profile.getName(),
      email: _email = profile.getEmail(),
      userID: _userID = profile.getUserID(),
      bio: _bio = profile.getBio(),
      weight: _weight = profile.getWeight(),
      carbonSaved: _carbonSaved = profile.getCarbonSaved(),
      calorieBurn: _calorieBurn = profile.getCalorieBurn(),
      badges: _badges = profile.getBadges(),
      status: _status = profile.getStatus(),
    );
  }


  // Getters
  String getName() => _name;
  UserStatus? getStatus() => _status;
  String? getEmail() => _email;
  String getUserID() => _userID;
  String getBio() => _bio ?? "";
  double getWeight() => _weight ?? 0;
  int getCarbonSaved() => _carbonSaved ?? 0;
  int getCalorieBurn() => _calorieBurn ?? 0;
  List<String> getBadges() => _badges ?? [];

  // Setters
  void setName(String name) {
    _name = name;
  }

  void setBio(String bio) {
    _bio = bio;
  }

  void setWeight(double weight) {
    _weight = weight;
  }

  void setCarbonSaved(int carbonSaved) {
    _carbonSaved = carbonSaved;
  }

  void setCalorieBurn(int calorieBurn) {
    _calorieBurn = calorieBurn;
  }

  void setBadges(List<String> badges) {
    _badges = badges;
  }

  void setStatus(UserStatus status){
    _status = status;
  }
}
