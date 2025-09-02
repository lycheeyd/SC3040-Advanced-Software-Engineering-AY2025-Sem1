import 'package:flutter/material.dart';
import '../models/location.dart';
import '../models/currentlocation.dart';
import '../services/apiService.dart';
import 'achievementScreen.dart'; // Import AchievementScreen

class HomeScreen extends StatefulWidget {
  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final ApiService apiService = ApiService();
  List<Location> locations = [];
  List<String> travelMethods = [];
  Location? selectedLocation;
  String? selectedMethod;
  CurrentLocation? userCurrentLocation;
  String? resultMessage;

  bool tripStarted = false; // Track if the trip is started

  @override
  void initState() {
    super.initState();
    _fetchLocations();
    _fetchCurrentLocation();
    _fetchTravelMethods();
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

    String userId = "user123"; // Retrieve the actual user ID from your auth logic

    if (selectedLocation != null && selectedMethod != null) {
      try {
        final metrics = await apiService.startTrip(selectedLocation!, selectedMethod!, userId);
        print('Metrics received: $metrics');

        setState(() {
          resultMessage = 'Calories burned: ${metrics['caloriesBurnt']}, Carbon saved: ${metrics['carbonSaved']} kg, Distance: ${metrics['distance'].toStringAsFixed(2)} km';
          tripStarted = true; // Mark trip as started
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
    if (selectedLocation != null && selectedMethod != null && userCurrentLocation != null) {
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
      tripStarted = false; // Reset trip state
      selectedLocation = null; // Reset location
      selectedMethod = null; // Reset method
      resultMessage = null; // Clear result message
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Travel Planner'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (userCurrentLocation != null)
              Text(
                'Your current location: ${userCurrentLocation!.name} (${userCurrentLocation!.latitude}, ${userCurrentLocation!.longitude})',
                style: TextStyle(fontSize: 16),
              ),
            SizedBox(height: 20),
            DropdownButton<Location>(
              hint: Text('Select a destination'),
              value: selectedLocation,
              onChanged: tripStarted ? null : (Location? newValue) { // Disable dropdown if trip started
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
            DropdownButton<String>(
              hint: Text('Select travel method'),
              value: selectedMethod,
              onChanged: tripStarted ? null : (String? newValue) { // Disable dropdown if trip started
                setState(() {
                  selectedMethod = newValue;
                });
              },
              items: travelMethods.map((String method) {
                return DropdownMenuItem<String>(
                  value: method,
                  child: Text(method),
                );
              }).toList(),
            ),
            SizedBox(height: 20),
            if (!tripStarted)
              ElevatedButton(
                onPressed: _startTrip,
                child: Text('Start Trip'),
              )
            else ...[
              ElevatedButton(
                onPressed: _endTrip,
                child: Text('End Trip'),
              ),
              ElevatedButton(
                onPressed: () {
                  _resetState();
                },
                child: Text('Delete Trip'),
              ),
            ],
            if (resultMessage != null)
              Padding(
                padding: const EdgeInsets.only(top: 20),
                child: Text(resultMessage!, style: TextStyle(fontSize: 18)),
              ),
          ],
        ),
      ),
    );
  }
}
