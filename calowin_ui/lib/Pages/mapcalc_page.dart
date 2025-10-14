import 'dart:convert';
import 'package:calowin/Pages/success_page.dart';
import 'package:calowin/common/dualbutton_dialog.dart';
import 'package:calowin/common/singlebutton_dialog.dart';
import 'package:calowin/common/user_profile.dart';
import 'package:flutter/material.dart';
import 'package:calowin/common/colors_and_fonts.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import '../control/location.dart';
import '../control/current_location.dart';
import 'package:http/http.dart' as http;
import '../control/apiService.dart';
import '../control/autocomplate_prediction.dart';
import '../control/place_auto_complate_response.dart';

// ignore: must_be_immutable
class MapcalcPage extends StatefulWidget {
  final UserProfile profile;
  final double? targetLat;
  final double? targetLong;
  String? targetName;
  MapcalcPage({
    super.key,
    this.targetLat,
    this.targetLong,
    this.targetName,
    required this.profile,
  });

  @override
  State<MapcalcPage> createState() => _MapcalcPageState();
}

class _MapcalcPageState extends State<MapcalcPage> {
  late UserProfile profile;
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
  Map<String,dynamic>? metrics;

  // to set the location passed from wellness zone
  Location? targetLocation;

  bool _tripStarted = false;
  late int _currentIndex = 99;

  Marker? selectedLocationMarker;
  Marker? currentLocationMarker;

  @override
  void initState() {
    super.initState();
    profile = widget.profile;
    _fetchTravelMethods();
    _retrievePlacesKey();
    _retrieveMapKey();
    _retrieveDirectionsKey();
    _initializeLocation();
    //this is the directing from wellness zone
  }

  @override
  void didUpdateWidget(MapcalcPage oldWidget){
    super.didUpdateWidget(oldWidget);
    if(oldWidget.targetName != widget.targetName && widget.targetName != "" && widget.targetName != null){
      _initializeLocation();
      setState(() {
        _searchController.text = widget.targetName ?? "";
        if(widget.targetName!=null) _handleSearch(_searchController.text);
        targetLocation = Location(name: widget.targetName ?? "", latitude: widget.targetLat ?? 1.3521, longitude:widget.targetLong ?? 103.8198);
      });
    }
  }


  Future<void> _retrieveDirectionsKey() async {
    try {
      apiDirectionKey = await apiService.fetchApiKey('Directions API');
      setState(() {});
      //print('API Key retrieved: $apiDirectionKey');
    } catch (e) {
      //print("Error retrieving API Key: $e");
    }
  }

  Future<void> _retrievePlacesKey() async {
    try {
      apiKey = await apiService.fetchApiKey('Places API');
      setState(() {});
      //print('API Key retrieved: $apiKey');
    } catch (e) {
      //print("Error retrieving API Key: $e");
    }
  }

  Future<void> _retrieveMapKey() async {
    try {
      apiMapKey = await apiService.fetchApiKey('Maps SDK Android API');
      print(apiMapKey);
      setState(() {});
      //print('API Key retrieved: $apiMapKey');
    } catch (e) {
      //print("Error retrieving API Key: $e");
    }
  }

  Future<void> _fetchTravelMethods() async {
    try {
      travelMethods = await apiService.fetchTravelMethods();
      setState(() {});
    } catch (e) {
      //print('Error fetching travel methods: $e');
    }
  }

  Future<void> _initializeLocation() async {
    try {
      await userCurrentLocation.getCurrentLocation();
      setState(() {
        //default address 1.3521,103.8198
        currentLocationMarker = Marker(
          markerId: MarkerId('currentLocation'),
          position: LatLng(userCurrentLocation.latitude ?? 1.3521, userCurrentLocation.longitude ?? 103.8198),
          infoWindow: InfoWindow(title: 'Current Location'),
          icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueGreen), // Set the color
        );
      });
      //print("Initial location: ${userCurrentLocation.name}");
      mapController.animateCamera(
        CameraUpdate.newLatLngZoom(
          LatLng(userCurrentLocation.latitude ?? 1.3521, userCurrentLocation.longitude ?? 103.8198),
          15,
        ),
      );

    } catch (e) {
      print('Error initializing location: $e');
    }
  }

  void placeAutocomplete(String query) async {
    // Uri uri = Uri.https(
    //   "maps.googleapis.com",
    //   'maps/api/place/autocomplete/json',
    //   {
    //     "input": query,
    //     "key": apiKey!,
    //   },
    // );
    Uri uri = Uri.parse('https://sc3040G5-CalowinTrip.hf.space/trips/places/autocomplete?input=$query');

    String? response = await ApiService.fetchUrl(uri);
    if (response != null) {
      PlaceAutocompleteResponse result = PlaceAutocompleteResponse.parseAutocompleteResult(response);
      setState(() {
        placePredictions = result.predictions ?? [];
      });
    }
  }

  void getPlaceDetails(String placeId) async {
    setState(() {
      _currentIndex = -1;
    });

    final String baseUrl = "https://sc3040G5-CalowinTrip.hf.space";
    Uri uri = Uri.parse('$baseUrl/trips/places/details?place_id=$placeId');

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
        //default address 1.3521,103.8198
        LatLng(userCurrentLocation.latitude ?? 1.3521, userCurrentLocation.longitude ?? 103.8198),
        destination,
      );
    }

  }

  void _showSelectionWarning() {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return SinglebuttonDialog(
          title: "Select Location and Travel Method",
          content: "Please select both a location and a travel method before starting the trip.",
          onConfirm: Navigator.of(context).pop,
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
    final String baseUrl = "https://sc3040G5-CalowinTrip.hf.space";

    // CONSTRUCT THE URL TO YOUR OWN BACKEND
    final directionsUrl = Uri.parse(
        '$baseUrl/trips/directions?origin=${origin.latitude},${origin.longitude}&destination=${destination.latitude},${destination.longitude}'
    );

    // The API key is no longer exposed here. The call is now safe.
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
    //print("Now searching for: $query");
  }

  void _retrieveMerics() async {
    // setState(() {
    //   metrics = {};
    //   metrics!['caloriesBurnt'] = 300;
    //   metrics!['carbonSaved'] = 20;
    //   metrics!['distance'] = 3.2;
    // });
    //call and retrive metrics from backend
    if(selectedLocation != null && selectedMethod != null){
      try {
        metrics = await apiService.retrieveMetrics(selectedLocation!, selectedMethod!, profile.getUserID(), userCurrentLocation);
        //print('Metrics received: $metrics');
        setState(() {
          resultMessage =
          'Calories burned: ${metrics!['caloriesBurnt']}, Carbon saved: ${metrics!['carbonSaved']} kg, Distance: ${metrics!['distance'].toStringAsFixed(2)} km';
          _tripStarted = true;
        });
      } catch (e) {
        print('Error starting trip: $e');
      }
    } else {
      _showSelectionWarning();
    }
  }

  Future<void> _startTrip() async {

    if(selectedLocation != null && selectedMethod != null){
      try {
        metrics = await apiService.startTrip(selectedLocation!, selectedMethod!, profile.getUserID(), userCurrentLocation);
        //print('Metrics received: $metrics');

        if(metrics == null) {throw("Metrics not retrieved");}
        else {
          setState(() {
            resultMessage =
            'Calories burned: ${metrics!['caloriesBurnt']}, Carbon saved: ${metrics!['carbonSaved']} g, Distance: ${metrics!['distance'].toStringAsFixed(2)} km';
            _tripStarted = true;
          });
        }
      } catch (e) {
        print('Error starting trip: $e');
      }
    } else {
      _showSelectionWarning();
    }
  }

  // This method will be called when the user selects a travel method
  Future<void> _retrieveMetrics() async {// Update with the actual user ID

    // Ensure both location and method are selected
    if (selectedLocation != null && selectedMethod != null) {
      try {
        // Call the API service to send data to the backend
        metrics = await apiService.retrieveMetrics(
            selectedLocation!,
            selectedMethod!,  // The travel method being selected
            profile.getUserID(),
            userCurrentLocation
        );

        // Handle the response and display metrics
        if (metrics != null) {
          setState(() {
            resultMessage =
            'Calories burned: ${metrics?['caloriesBurnt']}, Carbon saved: ${metrics?['carbonSaved']} kg, Distance: ${metrics?['distance'].toStringAsFixed(2)} km';
            _tripStarted = false; // No need to start a trip for this calculation
          });
        } else {
          // Handle case if no metrics were returned
          throw Exception('Failed to retrieve metrics');
        }
      } catch (e) {
        print('Error retrieving metrics: $e');
      }
    } else {
      _showSelectionWarning(); // Show a warning if no location or method is selected
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
        return DualbuttonDialog(
            title: 'End the trip and earn your rewards?',
            content: "We don't encourage cheating!",
            onConfirm: () {
              Navigator.of(context).pop(); // Close the pop-up
              _navigateToAchievement(); // Go to achievement screen
            },
            onCancel: Navigator.of(context).pop
        );
      },
    );
  }

  void _navigateToAchievement() async {
    if (selectedLocation != null &&
        selectedMethod != null &&
        userCurrentLocation != null) {
      final distance = metrics?['distance'];
      final caloriesBurnt = metrics?['caloriesBurnt'];
      final carbonSaved = metrics?['carbonSaved'];

      try {
        // Send trip metrics to the backend
        await apiService.addTripMetrics(carbonSaved, caloriesBurnt,profile.getUserID());
        //print('Trip metrics sent successfully.');
        if(mounted)
        {FocusScope.of(context).unfocus();
        // Navigate to AchievementScreen and pass metrics
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => SuccessPage(
              userId: profile.getUserID(),
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
        });}
      } catch (e) {
        print('Error sending trip metrics: $e');
      }
    }
  }

  void _onItemTapped(int index) {
    setState(() {
      _currentIndex = index;
      selectedMethod = travelMethods[index];
      _retrieveMetrics();
    });
  }

//default address 1.3521,103.8198
  void _resetState() {
    setState(() {
      widget.targetName = null;
      metrics = null;
      _tripStarted = false; // Reset trip state
      selectedLocation = null; // Reset location
      selectedMethod = null; // Reset method
      _currentIndex = -1; // Reset transport method selection
      resultMessage = null; // Clear result message
      _searchController.clear(); // Clear the search field
      selectedLocationMarker = null;
      polylineCoordinates = [];
      routePolylineBorder = null;
      routePolylineMain = null;
      mapController.animateCamera(
        CameraUpdate.newLatLngZoom(
          LatLng(userCurrentLocation.latitude ?? 1.3521, userCurrentLocation.longitude ?? 103.8198),
          15,
        ),
      );
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
                      myLocationEnabled: true,
                      myLocationButtonEnabled: true,
                      mapType: MapType.terrain,
                      onMapCreated: _onMapCreated,
                      initialCameraPosition: CameraPosition(
                        target: LatLng(userCurrentLocation.latitude ?? 1.3521, userCurrentLocation.longitude ?? 103.8198),
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
                    Align(
                        alignment: Alignment.topLeft,
                        child: IconButton(onPressed: _initializeLocation, icon: Icon(Icons.refresh),iconSize: 30,)
                    ),
                    if(metrics != null && _currentIndex<4 && _currentIndex >=0) Align(
                        alignment: Alignment.bottomLeft,
                        child: Padding(
                          padding: const EdgeInsets.only(left: 0),
                          child: Container(
                            decoration: BoxDecoration(
                                color: Color.fromARGB(149, 90, 232, 125),
                                border: Border.all()
                            ),
                            height: 150,
                            width: 140,
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text('Calories burned:',style: TextStyle(fontSize: 15,fontWeight: FontWeight.w500)),
                                Text("${metrics!['caloriesBurnt']} kcal",style: TextStyle(fontWeight: FontWeight.bold,fontSize: 15)),
                                const SizedBox(height: 7),
                                Text("Carbon saved:",style: TextStyle(fontSize: 15,fontWeight: FontWeight.w500)),
                                Text("${metrics!['carbonSaved']} g",style: TextStyle(fontWeight: FontWeight.bold,fontSize: 15)),
                                const SizedBox(height: 7),
                                Text("Distance:",style: TextStyle(fontSize: 15,fontWeight: FontWeight.w500)),
                                Text("${metrics!['distance'].toStringAsFixed(2)} km",style: TextStyle(fontWeight: FontWeight.bold,fontSize: 15))
                              ],
                            ),
                          ),
                        )
                    )
                  ],
                ),
              ),
              const SizedBox(height: 15),
              if (_tripStarted)
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    SizedBox(
                      height: 40,
                      width: 150,
                      child: ElevatedButton(
                        style: ElevatedButton.styleFrom(
                          elevation: 0,
                          foregroundColor: Colors.white,
                          backgroundColor: Colors.red,
                          shape: RoundedRectangleBorder(
                            borderRadius:
                            BorderRadius.circular(10), // Rounded corners
                          ),
                        ),
                        onPressed: _handleCancel,
                        child: const Text("Cancel Trip"),
                      ),
                    ),
                    SizedBox(
                      height: 40,
                      width: 150,
                      child: ElevatedButton(
                        style: ElevatedButton.styleFrom(
                          elevation: 0,
                          foregroundColor: Colors.white,
                          backgroundColor: PrimaryColors.brightGreen,
                          shape: RoundedRectangleBorder(
                            borderRadius:
                            BorderRadius.circular(10), // Rounded corners
                          ),
                        ),
                        onPressed: _endTrip,
                        child: const Text("End Trip"),
                      ),
                    ),
                  ],
                )
              else
                SizedBox(
                  height: 40,
                  width: 170,
                  child: ElevatedButton(
                    style: ElevatedButton.styleFrom(
                      elevation: 0,
                      foregroundColor: Colors.white,
                      backgroundColor: PrimaryColors.darkGreen,
                      shape: RoundedRectangleBorder(
                        borderRadius:
                        BorderRadius.circular(10), // Rounded corners
                      ),
                    ),
                    onPressed: _startTrip,
                    child: const Text("Start"),
                  ),
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