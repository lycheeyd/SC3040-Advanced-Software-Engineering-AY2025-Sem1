import 'dart:convert';
import 'package:calowin/Pages/success_page.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:calowin/common/colors_and_fonts.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import '../control/location.dart';
import '../control/current_location.dart';
import 'package:http/http.dart' as http;
import '../control/apiService.dart';
import '../control/autocomplate_prediction.dart';
import '../control/place_auto_complate_response.dart';

class MapcalcPage extends StatefulWidget {
  const MapcalcPage({super.key});

  @override
  State<MapcalcPage> createState() => _MapcalcPageState();
}

class _MapcalcPageState extends State<MapcalcPage> {
  final ApiService apiService = ApiService();
  List<Location> locations = [];
  List<String> travelMethods = [];
  List<AutocompletePrediction> placePredictions = [];
  List<LatLng> polylineCoordinates = [];
  Polyline? routePolyline;
  Polyline? routePolylineBorder;
  Polyline? routePolylineMain;
  Location? selectedLocation;
  String? selectedMethod;
  CurrentLocation userCurrentLocation = CurrentLocation();
  late GoogleMapController mapController;
  String? apiKey; 
  String? apiMapKey;
  String? apiDirectionKey;
  String? resultMessage;
  String? selectedLocationName; // Added to hold the name of the selected location
  final TextEditingController _searchController = TextEditingController(); // Added controller for TextField

  bool _tripStarted = false;
  late int _currentIndex = 99;

  Marker? selectedLocationMarker;
  Marker? currentLocationMarker;

  @override
  void initState() {
    super.initState();
    _initializeLocation();
    _fetchTravelMethods();
    _retrievePlacesKey();
    _retrieveMapKey();
    _retrieveDirectionsKey();
   }

  Future<void> _retrieveDirectionsKey() async {
    try {
      apiDirectionKey = await apiService.fetchApiKey('Directions API');
      setState(() {}); 
      print('API Key retrieved: $apiDirectionKey');
    } catch (e) {
      print("Error retrieving API Key: $e");
    }
  }

  Future<void> _retrievePlacesKey() async {
    try {
      apiKey = await apiService.fetchApiKey('Places API');
      setState(() {}); 
      print('API Key retrieved: $apiKey');
    } catch (e) {
      print("Error retrieving API Key: $e");
    }
  }

  Future<void> _retrieveMapKey() async {
    try {
      apiMapKey = await apiService.fetchApiKey('Maps SDK Android API');
      setState(() {}); 
      print('API Key retrieved: $apiMapKey');
    } catch (e) {
      print("Error retrieving API Key: $e");
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

  Future<void> _initializeLocation() async {
    try {
      await userCurrentLocation.getCurrentLocation();
      setState(() {
        currentLocationMarker = Marker(
          markerId: MarkerId('currentLocation'),
          position: LatLng(userCurrentLocation.latitude ?? 0, userCurrentLocation.longitude ?? 0),
          infoWindow: InfoWindow(title: 'Current Location'),
          icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueGreen), // Set the color

        );
      });

      if (mapController != null) {
        mapController.animateCamera(
          CameraUpdate.newLatLngZoom(
            LatLng(userCurrentLocation.latitude ?? 0, userCurrentLocation.longitude ?? 0),
            15,
          ),
        );
      }
    } catch (e) {
      print('Error initializing location: $e');
    }
  }

  void placeAutocomplete(String query) async {
    Uri uri = Uri.https(
      "maps.googleapis.com",
      'maps/api/place/autocomplete/json',
      {
        "input": query,
        "key": apiKey!,
      },
    );

    String? response = await ApiService.fetchUrl(uri);
    if (response != null) {
      PlaceAutocompleteResponse result = PlaceAutocompleteResponse.parseAutocompleteResult(response);
      setState(() {
        placePredictions = result.predictions ?? [];
      });
    }
  }

  void getPlaceDetails(String placeId) async {
    Uri uri = Uri.https(
      "maps.googleapis.com",
      'maps/api/place/details/json',
      {
        "place_id": placeId,
        "key": apiKey!,
      },
    );

    String? response = await ApiService.fetchUrl(uri);
    if (response != null) {
      final json = jsonDecode(response);
      final location = json['result']['geometry']['location'];
      double latitude = location['lat'];
      double longitude = location['lng'];

      LatLng destination = LatLng(latitude, longitude);


      mapController.animateCamera(
        CameraUpdate.newLatLng(LatLng(latitude, longitude)),
      );

      setState(() {
        selectedLocationMarker = Marker(
          markerId: MarkerId(placeId),
          position: LatLng(latitude, longitude),
          infoWindow: InfoWindow(title: json['result']['name']),
        );
        placePredictions.clear();
        selectedLocationName = json['result']['name']; // Store the selected location name
        _searchController.text = selectedLocationName!; // Update the TextField with the selected location name
        selectedLocation = Location(
          name: selectedLocationName!,
          latitude: latitude,
          longitude: longitude,
        ); // Create a Location object
      });
          FocusScope.of(context).unfocus();
          _getDirections(
          LatLng(userCurrentLocation.latitude ?? 0, userCurrentLocation.longitude ?? 0),
              destination,
    );
    }
    
  }

  void _showSelectionWarning() {
  showDialog(
    context: context,
    builder: (BuildContext context) {
      return AlertDialog(
        title: Text(
          'Select Location and Travel Method',
          style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
        ),
        content: Text(
          'Please select both a location and a travel method before starting the trip.',
          style: TextStyle(fontSize: 16),
        ),
        actions: <Widget>[
          TextButton(
            onPressed: () {
              Navigator.of(context).pop(); // Close the dialog
            },
            child: Text('OK'),
          ),
        ],
      );
    },
  );
}

List<LatLng> _decodePolyline(String polyline) {
  List<LatLng> points = [];
  int index = 0, len = polyline.length;
  int lat = 0, lng = 0;

  while (index < len) {
    int b, shift = 0, result = 0;
    do {
      b = polyline.codeUnitAt(index++) - 63;
      result |= (b & 0x1F) << shift;
      shift += 5;
    } while (b >= 0x20);
    int dlat = (result & 1) != 0 ? ~(result >> 1) : (result >> 1);
    lat += dlat;

    shift = 0;
    result = 0;
    do {
      b = polyline.codeUnitAt(index++) - 63;
      result |= (b & 0x1F) << shift;
      shift += 5;
    } while (b >= 0x20);
    int dlng = (result & 1) != 0 ? ~(result >> 1) : (result >> 1);
    lng += dlng;

    points.add(LatLng(lat / 1E5, lng / 1E5));
  }
  return points;
}


  Future<void> _getDirections(LatLng origin, LatLng destination) async {
  final directionsUrl = Uri.https("maps.googleapis.com", "maps/api/directions/json", {
    "origin": "${origin.latitude},${origin.longitude}",
    "destination": "${destination.latitude},${destination.longitude}",
    "key": apiDirectionKey,
  });

  final response = await http.get(directionsUrl);
  if (response.statusCode == 200) {
    final json = jsonDecode(response.body);
    final polylinePoints = json["routes"][0]["overview_polyline"]["points"];
    polylineCoordinates = _decodePolyline(polylinePoints);
    
    setState(() {
      // Create the thicker polyline for the border
    routePolylineBorder = Polyline(
      polylineId: PolylineId("routeBorder"),
      color: Colors.black, // Border color
      width: 8, // Border thickness (should be larger)
      points: polylineCoordinates,
    );

    // Create the main polyline for the inner color
    routePolylineMain = Polyline(
      polylineId: PolylineId("routeMain"),
      color: Colors.blue, // Main line color
      width: 5, // Main line thickness (should be smaller)
      points: polylineCoordinates,
    );
    });
  } else {
    print("Error fetching directions: ${response.body}");
  }
}

  void _onMapCreated(GoogleMapController controller) {
    mapController = controller;
  }

  void _handleSearch(String query) {
    placeAutocomplete(query);
  }

  Future<void> _startTrip() async {
    String userId = "user1234"; 
    
    if(selectedLocation != null && selectedMethod != null){
      try {
        final metrics = await apiService.startTrip(selectedLocation!, selectedMethod!, userId, userCurrentLocation);
       print('Metrics received: $metrics');
      
    
        setState(() {
          resultMessage =
              'Calories burned: ${metrics['caloriesBurnt']}, Carbon saved: ${metrics['carbonSaved']} kg, Distance: ${metrics['distance'].toStringAsFixed(2)} km';
          _tripStarted = true; 
        });
      } catch (e) {
        print('Error starting trip: $e');
      }
    } else {
        _showSelectionWarning();
    }
  }

  void _handleCancel() {
    setState(() {
      _resetState(); // Reset state when coming back to home screen

    });
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
            builder: (context) => SuccessPage(
              caloriesBurnt: caloriesBurnt,
              carbonSaved: carbonSaved,
              distance: distance,
              destination: selectedLocation!.name,
              currentLocation: "Gy",
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

  void _onItemTapped(int index) {
    setState(() {
      _currentIndex = index;
      selectedMethod = travelMethods[index];
    });
  }

  void _resetState() {
  setState(() {
    _tripStarted = false; // Reset trip state
    selectedLocation = null; // Reset location
    selectedMethod = null; // Reset method
    _currentIndex = 99; // Reset transport method selection
    resultMessage = null; // Clear result message
    _searchController.clear(); // Clear the search field
  });

  FocusScope.of(context).unfocus(); // Unfocus the search field
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
              color: _currentIndex == index ? Colors.black : Colors.grey.shade700),
        ),
      ],
    );
  }

 @override
  Widget build(BuildContext context) {
    return Scaffold(
      resizeToAvoidBottomInset: false,
      backgroundColor: PrimaryColors.dullGreen,
      body: Stack(
        children: [
          Column(
            children: [
              Padding(
                padding: const EdgeInsets.symmetric(vertical: 10, horizontal: 20),
                child: TextField(
                  controller: _searchController,
                  onChanged: _handleSearch,
                  decoration: InputDecoration(
                    border: OutlineInputBorder(borderRadius: BorderRadius.circular(25)),
                    labelText: 'Search for a location',
                    prefixIcon: const Icon(Icons.search),
                  ),
              enabled: !_tripStarted, // Disable search field if trip has started

                ),
              ),
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
              SizedBox(
                height: 400,
                child: Stack(
                  children: [
                    GoogleMap(
                      onMapCreated: _onMapCreated,
                      initialCameraPosition: CameraPosition(
                        target: LatLng(userCurrentLocation.latitude ?? 0, userCurrentLocation.longitude ?? 0),
                        zoom: 15,
                      ),
                      markers: {
                        if (currentLocationMarker != null) currentLocationMarker!,
                        if (selectedLocationMarker != null) selectedLocationMarker!,
                      },
                      polylines: {
                      if (routePolylineBorder != null) routePolylineBorder!,
                      if (routePolylineMain != null) routePolylineMain!,
                    },
                    ),
                    if (_tripStarted)
                      const Center(
                        child: CircularProgressIndicator(),
                      ),
                  ],
                ),
              ),
              const SizedBox(height: 15),
              if (_tripStarted)
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    ElevatedButton(
                      onPressed: _endTrip,
                      child: Text('End Trip', style: GoogleFonts.poppins(fontSize: 16)),
                    ),
                    ElevatedButton(
                      onPressed: _handleCancel,
                      child: Text('Cancel', style: GoogleFonts.poppins(fontSize: 16)),
                    ),
                  ],
                )
              else
                ElevatedButton(
                  onPressed: _startTrip, // Enable only if both are selected
                  child: Text('Start Trip', style: GoogleFonts.poppins(fontSize: 16)),
                ),
            ],
          ),
          
          if (placePredictions.isNotEmpty)
            Positioned(
              top: 100,
              left: 20,
              right: 20,
              child: Container(
                padding: const EdgeInsets.all(8.0),
                color: Colors.white,
                child: ListView.builder(
                  shrinkWrap: true,
                  itemCount: placePredictions.length,
                  itemBuilder: (context, index) {
                    return ListTile(
                      title: Text(placePredictions[index].description ?? ''),
                      onTap: () {
                        getPlaceDetails(placePredictions[index].placeId!);
                      },
                    );
                  },
                ),
              ),
            ),
        ],
      ),
    );
  }
} 
