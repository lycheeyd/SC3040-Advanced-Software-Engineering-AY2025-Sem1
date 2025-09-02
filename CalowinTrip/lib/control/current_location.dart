import 'package:geolocator/geolocator.dart';
import 'package:geocoding/geocoding.dart';

class CurrentLocation {
  String? name;
  double? latitude;
  double? longitude;

  Future<void> getCurrentLocation() async {
    bool serviceEnabled;
    LocationPermission permission;

    // Check if location services are enabled
    serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      await Geolocator.openLocationSettings();
      throw Exception('Location services are disabled.');
    }

    // Check location permissions
    permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        throw Exception('Location permissions are denied');
      }
    }

    if (permission == LocationPermission.deniedForever) {
      throw Exception('Location permissions are permanently denied.');
    }

    // Get current location and update latitude and longitude properties
    Position position = await Geolocator.getCurrentPosition(desiredAccuracy: LocationAccuracy.high);
    latitude = position.latitude;
    longitude = position.longitude;

    // Fetch place name using latitude and longitude
    try {
      List<Placemark> placemarks = await placemarkFromCoordinates(latitude!, longitude!);
      if (placemarks.isNotEmpty) {
        name = placemarks.first.name; // Retrieve the name of the location
      }
    } catch (e) {
      print('Error retrieving place name: $e');
    }
  }

  // Method to convert the current location to JSON format
  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'latitude': latitude,
      'longitude': longitude,
    };
  }
}
