import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:calowin/common/colors_and_fonts.dart';
import '../models/location.dart';
import '../models/currentlocation.dart';
import '../services/apiService.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'achievementScreen.dart'; // Import AchievementScreen

class MapcalcPage extends StatefulWidget {
  const MapcalcPage({super.key});

  @override
  State<MapcalcPage> createState() => _MapcalcPageState();
}

class _MapcalcPageState extends State<MapcalcPage> {
  final ApiService apiService = ApiService();
  List<Location> locations = [];
  List<String> travelMethods = [];
  Location? selectedLocation;
  String? selectedMethod;
  CurrentLocation? userCurrentLocation;
  String? resultMessage;
  late GoogleMapController mapController;

  late int _currentIndex = 99; // To unselect transport method
  late bool _tripStarted = false;

  @override
  void initState() {
    retrieveApiKey();
    super.initState();
    _fetchLocations();
    _fetchCurrentLocation();
    _fetchTravelMethods();
  }

  

  // Fetch API key
  Future<void> retrieveApiKey() async {
  try {
    String apiKey = await apiService.fetchApiKey('Maps SDK Android API');
    print('API Key: $apiKey');

  } catch (e) {
    print('Error retrieving API Key: $e');
  }
}

  Future<void> _fetchLocations() async {
    try {
      locations = await apiService.fetchAvailableLocations();
      setState(() {});
    } catch (e) {
      print('Error fetching locations: $e');
    }
  }

  Future<void> _fetchCurrentLocation() async {
    try {
      userCurrentLocation = await apiService.fetchCurrentLocation();
      setState(() {});
    } catch (e) {
      print('Error fetching current location: $e');
    }
  }

  Future<void> _fetchTravelMethods() async {
    try {
      travelMethods = await apiService.fetchTravelMethods();
      setState(() {});
    } catch (e) {
      print('Error fetching travel methods: $e');
    }
  }

  Future<void> _startTrip() async {
    String userId =
        "user123"; // Retrieve the actual user ID from your auth logic

    if (selectedLocation != null && selectedMethod != null) {
      try {
        final metrics = await apiService.startTrip(
            selectedLocation!, selectedMethod!, userId);
        print('Metrics received: $metrics');

        setState(() {
          resultMessage =
              'Calories burned: ${metrics['caloriesBurnt']}, Carbon saved: ${metrics['carbonSaved']} kg, Distance: ${metrics['distance'].toStringAsFixed(2)} km';
          _tripStarted = true; // Mark trip as started
        });
      } catch (e) {
        print('Error starting trip: $e');
      }
    } else {
      print('Please select a location and travel method.');
    }
  }

  void _endTrip() {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text(
            'End the trip and earn your rewards?',
            style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
          ),
          content: Text(
            'We don\'t encourage cheating!',
            style: TextStyle(fontSize: 16),
          ),
          actions: <Widget>[
            TextButton(
              onPressed: () {
                Navigator.of(context).pop(); // Close the pop-up
              },
              child: Text('Cancel'),
            ),
            TextButton(
              onPressed: () {
                Navigator.of(context).pop(); // Close the pop-up
                _navigateToAchievement(); // Go to achievement screen
              },
              child: Text('Yes'),
            ),
          ],
        );
      },
    );
  }

  void _navigateToAchievement() async {
    if (selectedLocation != null &&
        selectedMethod != null &&
        userCurrentLocation != null) {
      final metrics = resultMessage!.split(',');
      final distance = double.parse(metrics[2].split(': ')[1].split(' ')[0]);
      final caloriesBurnt = int.parse(metrics[0].split(': ')[1]);
      final carbonSaved = int.parse(metrics[1].split(': ')[1].split(' ')[0]);

      try {
        // Send trip metrics to the backend
        await apiService.addTripMetrics(carbonSaved, caloriesBurnt);
        print('Trip metrics sent successfully.');

        // Navigate to AchievementScreen and pass metrics
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => AchievementScreen(
              caloriesBurnt: caloriesBurnt,
              carbonSaved: carbonSaved,
              distance: distance,
              destination: selectedLocation!.name,
              currentLocation: userCurrentLocation!.name,
              tripMethod: selectedMethod!,
            ),
          ),
        ).then((_) {
          _resetState(); // Reset state when coming back to home screen
        });
      } catch (e) {
        print('Error sending trip metrics: $e');
      }
    }
  }

  void _resetState() {
    setState(() {
      _tripStarted = false; // Reset trip state
      selectedLocation = null; // Reset location
      selectedMethod = null; // Reset method
      _currentIndex = 99; // Reset transport method selection
      resultMessage = null; // Clear result message
    });
  }

  void _onItemTapped(int index) {
    setState(() {
      _currentIndex = index;
      selectedMethod =
          travelMethods[index]; // Set selected method based on tapped index
    });
  }

  Widget _transportIconBuilder(IconData icon, String title, int index) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        IconButton(
          onPressed: () => _onItemTapped(index),
          icon: Icon(
            icon,
            color: _currentIndex == index ? Colors.black : Colors.grey.shade700,
          ),
        ),
        Text(
          title,
          style: TextStyle(
            fontSize: 10,
            color: _currentIndex == index ? Colors.black : Colors.grey.shade700,
          ),
        ),
      ],
    );
  }

  void _handleSearch(String address) {
    // Your search handling logic goes here
  }

@override
Widget build(BuildContext context) {
  return Scaffold(
    backgroundColor: PrimaryColors.dullGreen,
    body: Column(
      children: [
        Padding(
          padding: const EdgeInsets.symmetric(vertical: 10, horizontal: 20),
          child: TextField(
            onChanged: _handleSearch,
            decoration: InputDecoration(
              border: OutlineInputBorder(),
              labelText: 'Search for a location',
              suffixIcon: IconButton(
                icon: const Icon(Icons.search),
                onPressed: () {
                  // Perform search action here
                },
              ),
            ),
          ),
        ),
        if (userCurrentLocation != null)
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 20),
            child: Text(
              'Your current location: ${userCurrentLocation!.name} (${userCurrentLocation!.latitude}, ${userCurrentLocation!.longitude})',
              style: TextStyle(fontSize: 16),
            ),
          ),
        SizedBox(height: 20),
        DropdownButton<Location>(
          hint: Text('Select a destination'),
          value: selectedLocation,
          onChanged: _tripStarted ? null : (Location? newValue) {
            setState(() {
              selectedLocation = newValue;
            });
          },
          items: locations.map((Location loc) {
            return DropdownMenuItem<Location>(
              value: loc,
              child: Text(loc.name),
            );
          }).toList(),
        ),
        SizedBox(height: 20),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: [
            _transportIconBuilder(Icons.directions_walk, "Walk", 0),
            _transportIconBuilder(Icons.pedal_bike, "Bicycle", 1),
            _transportIconBuilder(Icons.directions_bus, "Bus", 2),
            _transportIconBuilder(Icons.directions_car, "Car", 3),
          ],
        ),
        const SizedBox(height: 15),
        Container(
          width: MediaQuery.of(context).size.width,
          height: MediaQuery.of(context).size.width + 30,
          color: Colors.white,
          child: GoogleMap(
            onMapCreated: (GoogleMapController controller) {
              mapController = controller;
              if (userCurrentLocation != null) {
                mapController.animateCamera(
                  CameraUpdate.newLatLng(
                    LatLng(userCurrentLocation!.latitude,
                        userCurrentLocation!.longitude),
                  ),
                );
              }
            },
            initialCameraPosition: CameraPosition(
              target: LatLng(0, 0), // Default position; adjust as necessary
              zoom: 12,
            ),
            markers: selectedLocation != null
                ? {
                    Marker(
                      markerId: MarkerId('destination'),
                      position: LatLng(
                        selectedLocation!.latitude,
                        selectedLocation!.longitude,
                      ),
                    ),
                  }
                : {},
          ),
        ),
        Expanded(
          child: SizedBox(
            child: !_tripStarted // Check which buttons to display
                ? Center(
                    child: SizedBox(
                      width: 200,
                      child: ElevatedButton(
                        onPressed: _startTrip,
                        child: const Text('Start Trip'),
                      ),
                    ),
                  )
                : Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      SizedBox(
                        width: 150,
                        height: 40,
                        child: ElevatedButton(
                          onPressed: () {
                            // Add confirmation dialog before deletion
                            _showDeleteConfirmationDialog();
                          },
                          style: ElevatedButton.styleFrom(
                            elevation: 0,
                            backgroundColor: Colors.red,
                            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 3),
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(10),
                            ),
                          ),
                          child: Text(
                            "Delete Trip",
                            style: GoogleFonts.roboto(fontSize: 16, color: Colors.white),
                          ),
                        ),
                      ),
                      SizedBox(
                        width: 150,
                        height: 40,
                        child: ElevatedButton(
                          onPressed: _endTrip,
                          style: ElevatedButton.styleFrom(
                            elevation: 0,
                            backgroundColor: Colors.green,
                            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 3),
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(10),
                            ),
                          ),
                          child: Text(
                            "End Trip",
                            style: GoogleFonts.roboto(fontSize: 16, color: Colors.white),
                          ),
                        ),
                      ),
                    ],
                  ),
          ),
        ),
        if (resultMessage != null) ...[
          Text(resultMessage!, style: TextStyle(fontSize: 16)),
          SizedBox(height: 20),
        ],
      ],
    ),
  );
}

// Show a confirmation dialog for deleting a trip
void _showDeleteConfirmationDialog() {
  showDialog(
    context: context,
    builder: (BuildContext context) {
      return AlertDialog(
        title: Text('Delete Trip'),
        content: Text('Are you sure you want to delete this trip?'),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.of(context).pop(); // Close the dialog
            },
            child: Text('Cancel'),
          ),
          TextButton(
            onPressed: () {
              _resetState(); // Call the method to reset the trip state
              Navigator.of(context).pop(); // Close the dialog
            },
            child: Text('Delete'),
          ),
        ],
      );
    },
  );
}
}