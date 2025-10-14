import 'dart:convert';
import 'package:calowin/Pages/success_page.dart';
import 'package:calowin/common/custom_scaffold.dart';
import 'package:calowin/common/dualbutton_dialog.dart';
import 'package:calowin/common/singlebutton_dialog.dart';
import 'package:calowin/common/user_profile.dart';
import 'package:flutter/material.dart';
import 'package:calowin/common/colors_and_fonts.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import '../control/location.dart';
import '../control/current_location.dart';
import 'package:http/http.dart' as http;
import '../control/apiService.dart';
import '../control/autocomplate_prediction.dart';
import '../control/place_auto_complate_response.dart';
import 'package:geocoding/geocoding.dart' as geocoding;

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
  String? selectedLocationName;
  final TextEditingController _searchController = TextEditingController();
  Map<String,dynamic>? metrics;

  Location? targetLocation;
  bool _tripStarted = false;
  int _currentIndex = -1;

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
  }

  Future<void> _handleMapTap(LatLng tappedPoint) async {
    try {
      List<geocoding.Placemark> placemarks = await geocoding.placemarkFromCoordinates(
        tappedPoint.latitude,
        tappedPoint.longitude,
      );

      if (placemarks.isNotEmpty) {
        geocoding.Placemark place = placemarks.first;
        String locationName = place.street ?? place.name ?? 'Unnamed Location';

        setState(() {
          selectedLocation = Location(
            name: locationName,
            latitude: tappedPoint.latitude,
            longitude: tappedPoint.longitude,
          );
          _searchController.text = locationName;

          selectedLocationMarker = Marker(
            markerId: MarkerId(tappedPoint.toString()),
            position: tappedPoint,
            infoWindow: InfoWindow(title: locationName),
          );

          placePredictions.clear();
        });

        _getDirections(
          LatLng(userCurrentLocation.latitude ?? 1.3521, userCurrentLocation.longitude ?? 103.8198),
          tappedPoint,
        );
      }
    } catch (e) {
      print("Error getting location from tap: $e");
    }
  }

  @override
  void didUpdateWidget(MapcalcPage oldWidget){
    super.didUpdateWidget(oldWidget);
    if(widget.targetName != null && widget.targetName != oldWidget.targetName){

      final destinationLocation = Location(
        name: widget.targetName!,
        latitude: widget.targetLat!,
        longitude: widget.targetLong!,
      );

      final destinationLatLng = LatLng(destinationLocation.latitude, destinationLocation.longitude);

      setState(() {
        selectedLocation = destinationLocation;
        _searchController.text = destinationLocation.name;
        selectedLocationMarker = Marker(
          markerId: MarkerId(destinationLocation.name),
          position: destinationLatLng,
          infoWindow: InfoWindow(title: destinationLocation.name),
        );
        placePredictions.clear();
      });

      mapController.animateCamera(
        CameraUpdate.newLatLngZoom(destinationLatLng, 15),
      );

      _getDirections(
        LatLng(userCurrentLocation.latitude ?? 1.3521, userCurrentLocation.longitude ?? 103.8198),
        destinationLatLng,
      );
    }
  }


  Future<void> _retrieveDirectionsKey() async {
    try {
      apiDirectionKey = await apiService.fetchApiKey('Directions API');
      setState(() {});
    } catch (e) {
      print("Error retrieving API Key: $e");
    }
  }

  Future<void> _retrievePlacesKey() async {
    try {
      apiKey = await apiService.fetchApiKey('Places API');
      setState(() {});
    } catch (e) {
      print("Error retrieving API Key: $e");
    }
  }

  Future<void> _retrieveMapKey() async {
    try {
      apiMapKey = await apiService.fetchApiKey('Maps SDK Android API');
      print(apiMapKey);
      setState(() {});
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
          position: LatLng(userCurrentLocation.latitude ?? 1.3521, userCurrentLocation.longitude ?? 103.8198),
          infoWindow: InfoWindow(title: 'Current Location'),
          icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueGreen),
        );
      });
    } catch (e) {
      print('Error initializing location: $e');
    }
  }

  void placeAutocomplete(String query) async {
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
        selectedLocationName = json['result']['name'];
        _searchController.text = selectedLocationName!;
        selectedLocation = Location(
          name: selectedLocationName!,
          latitude: latitude,
          longitude: longitude,
        );
      });
      FocusScope.of(context).unfocus();
      _getDirections(
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

  // NEW: A reusable function to show a loading spinner dialog.
  void _showLoadingDialog({String message = "Loading..."}) {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return Dialog(
          child: Padding(
            padding: const EdgeInsets.all(20.0),
            child: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                const CircularProgressIndicator(),
                const SizedBox(width: 20),
                Text(message),
              ],
            ),
          ),
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
    final directionsUrl = Uri.parse(
        '$baseUrl/trips/directions?origin=${origin.latitude},${origin.longitude}&destination=${destination.latitude},${destination.longitude}'
    );

    final response = await http.get(directionsUrl);

    if (response.statusCode == 200) {
      final json = jsonDecode(response.body);
      final polylinePoints = json["routes"][0]["overview_polyline"]["points"];
      polylineCoordinates = _decodePolyline(polylinePoints);

      setState(() {
        routePolylineBorder = Polyline(
          polylineId: PolylineId("routeBorder"),
          color: Color.fromARGB(255, 33, 87, 168),
          width: 8,
          points: polylineCoordinates,
        );

        routePolylineMain = Polyline(
          polylineId: PolylineId("routeMain"),
          color: Color.fromARGB(255, 69, 146, 255),
          width: 5,
          points: polylineCoordinates,
        );
      });
    } else {
      print("Error fetching directions: ${response.body}");
    }
  }

  void _onMapCreated(GoogleMapController controller) {
    mapController = controller;
    _initializeLocation();
    mapController.animateCamera(
      CameraUpdate.newLatLngZoom(
        LatLng(userCurrentLocation.latitude ?? 1.3521, userCurrentLocation.longitude ?? 103.8198),
        15,
      ),
    );
  }

  void _handleSearch(String query) {
    if (query.isNotEmpty) {
      placeAutocomplete(query);
    } else {
      setState(() {
        placePredictions.clear();
      });
    }
  }

  // MODIFIED: _startTrip now shows a loading spinner.
  Future<void> _startTrip() async {
    if (selectedLocation != null && selectedMethod != null) {
      _showLoadingDialog(message: "Starting Trip..."); // Show spinner
      try {
        metrics = await apiService.startTrip(selectedLocation!, selectedMethod!, profile.getUserID(), userCurrentLocation);
        if (mounted) Navigator.of(context).pop(); // Hide spinner

        if (metrics == null) {
          throw("Metrics not retrieved");
        } else {
          setState(() {
            resultMessage = 'Calories burned: ${metrics!['caloriesBurnt']}, Carbon saved: ${metrics!['carbonSaved']} g, Distance: ${metrics!['distance'].toStringAsFixed(2)} km';
            _tripStarted = true;
          });
        }
      } catch (e) {
        if (mounted) Navigator.of(context).pop(); // Hide spinner on error
        print('Error starting trip: $e');
      }
    } else {
      _showSelectionWarning();
    }
  }

  Future<void> _retrieveMetrics() async {
    if (selectedLocation != null && selectedMethod != null) {
      try {
        metrics = await apiService.retrieveMetrics(
            selectedLocation!,
            selectedMethod!,
            profile.getUserID(),
            userCurrentLocation
        );
        if (metrics != null) {
          setState(() {
            resultMessage = 'Calories burned: ${metrics?['caloriesBurnt']}, Carbon saved: ${metrics?['carbonSaved']} kg, Distance: ${metrics?['distance'].toStringAsFixed(2)} km';
            _tripStarted = false;
          });
        } else {
          throw Exception('Failed to retrieve metrics');
        }
      } catch (e) {
        print('Error retrieving metrics: $e');
      }
    }
  }

  void _handleCancel() {
    setState(() {
      _resetState();
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
              Navigator.of(context).pop();
              _navigateToAchievement();
            },
            onCancel: Navigator.of(context).pop
        );
      },
    );
  }

  // MODIFIED: _navigateToAchievement now shows a loading spinner.
  void _navigateToAchievement() async {
    if (selectedLocation != null &&
        selectedMethod != null &&
        userCurrentLocation != null) {
      _showLoadingDialog(message: "Ending Trip..."); // Show spinner

      final distance = metrics?['distance'];
      final caloriesBurnt = metrics?['caloriesBurnt'];
      final carbonSaved = metrics?['carbonSaved'];

      try {
        await apiService.addTripMetrics(carbonSaved, caloriesBurnt, profile.getUserID());
        if(mounted) {
          Navigator.of(context).pop(); // Hide spinner
          FocusScope.of(context).unfocus();

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
            _resetState();
          });
        }
      } catch (e) {
        if (mounted) Navigator.of(context).pop(); // Hide spinner on error
        print('Error sending trip metrics: $e');
      }
    }
  }

  void _onItemTapped(int index) {
    if (_tripStarted) return;
    setState(() {
      _currentIndex = index;
      selectedMethod = travelMethods[index];
      _retrieveMetrics();
    });
  }

  void _resetState() {
    setState(() {
      widget.targetName = null;
      metrics = null;
      _tripStarted = false;
      selectedLocation = null;
      selectedMethod = null;
      _currentIndex = -1;
      resultMessage = null;
      _searchController.clear();
      selectedLocationMarker = null;
      polylineCoordinates.clear();
      routePolylineBorder = null;
      routePolylineMain = null;
      placePredictions.clear();
      if(mapController != null) {
        mapController.animateCamera(
          CameraUpdate.newLatLngZoom(
            LatLng(userCurrentLocation.latitude ?? 1.3521, userCurrentLocation.longitude ?? 103.8198),
            15,
          ),
        );
      }
    });
    FocusScope.of(context).unfocus();
  }

  Widget _transportIconBuilder(IconData icon, String title, int index) {
    bool isSelected = _currentIndex == index;
    return GestureDetector(
      onTap: () => _onItemTapped(index),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 200),
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        decoration: BoxDecoration(
          color: isSelected ? PrimaryColors.darkGreen : Colors.grey[200],
          borderRadius: BorderRadius.circular(20),
          boxShadow: isSelected ? [
            BoxShadow(
              color: Colors.black.withOpacity(0.2),
              blurRadius: 4,
              offset: const Offset(0, 2),
            )
          ] : [],
        ),
        child: Row(
          children: [
            Icon(
              icon,
              color: isSelected ? Colors.white : Colors.black,
              size: 20,
            ),
            const SizedBox(width: 8),
            Text(
              title,
              style: TextStyle(
                  fontSize: 14,
                  fontWeight: FontWeight.bold,
                  color: isSelected ? Colors.white : Colors.black),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildMetricsDisplay() {
    if (metrics == null) return const SizedBox.shrink();

    return Container(
      padding: const EdgeInsets.all(16.0),
      decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(12),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.1),
              blurRadius: 8,
              offset: const Offset(0, 4),
            )
          ]
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: [
          _metricItem(Icons.local_fire_department, "${metrics!['caloriesBurnt']}", "kcal", Colors.orange),
          _metricItem(Icons.eco, "${metrics!['carbonSaved']}", "g Saved", Colors.green),
          _metricItem(Icons.straighten, "${metrics!['distance'].toStringAsFixed(2)}", "km", Colors.blue),
        ],
      ),
    );
  }

  Widget _metricItem(IconData icon, String value, String unit, Color color) {
    return Column(
      children: [
        Icon(icon, color: color, size: 28),
        const SizedBox(height: 4),
        Text(value, style: GoogleFonts.poppins(fontSize: 18, fontWeight: FontWeight.bold)),
        Text(unit, style: GoogleFonts.poppins(fontSize: 12, color: Colors.grey[600])),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    return CustomScaffold(
      body: Scaffold(
        resizeToAvoidBottomInset: false,
        body: Stack(
          children: [
            GoogleMap(
              onTap:_handleMapTap,
              padding: EdgeInsets.only(bottom: MediaQuery.of(context).size.height * 0.3),
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
            if (placePredictions.isNotEmpty)
              SafeArea(
                child: Container(
                  margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                  color: Colors.white.withOpacity(0.95),
                  child: ListView.builder(
                    shrinkWrap: true,
                    padding: EdgeInsets.zero,
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
            DraggableScrollableSheet(
              initialChildSize: 0.3,
              minChildSize: 0.15,
              maxChildSize: 0.6,
              builder: (BuildContext context, ScrollController scrollController) {
                return Container(
                  decoration: BoxDecoration(
                    color: Colors.grey[100],
                    borderRadius: const BorderRadius.vertical(top: Radius.circular(20)),
                    boxShadow: [
                      BoxShadow(
                        color: Colors.black.withOpacity(0.2),
                        blurRadius: 10,
                      ),
                    ],
                  ),
                  child: ListView(
                    controller: scrollController,
                    padding: const EdgeInsets.all(16.0),
                    children: [
                      Center(
                        child: Container(
                          width: 40,
                          height: 5,
                          decoration: BoxDecoration(
                            color: Colors.grey[300],
                            borderRadius: BorderRadius.circular(12),
                          ),
                        ),
                      ),
                      const SizedBox(height: 16),
                      TextField(
                        controller: _searchController,
                        onChanged: _handleSearch,
                        enabled: !_tripStarted,
                        decoration: InputDecoration(
                          filled: true,
                          fillColor: Colors.white,
                          labelText: 'Search for a location',
                          prefixIcon: const Icon(Icons.search),
                          suffixIcon: _searchController.text.isNotEmpty
                              ? IconButton(icon: const Icon(Icons.clear), onPressed: _resetState)
                              : null,
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(25),
                            borderSide: BorderSide.none,
                          ),
                        ),
                      ),
                      const SizedBox(height: 16),
                      SingleChildScrollView(
                        scrollDirection: Axis.horizontal,
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            _transportIconBuilder(Icons.directions_walk, "Walk", 0),
                            const SizedBox(width: 8),
                            _transportIconBuilder(Icons.pedal_bike, "Bicycle", 1),
                            const SizedBox(width: 8),
                            _transportIconBuilder(Icons.directions_bus, "Bus", 2),
                            const SizedBox(width: 8),
                            _transportIconBuilder(Icons.directions_car, "Car", 3),
                          ],
                        ),
                      ),
                      const SizedBox(height: 20),
                      if (metrics != null) _buildMetricsDisplay(),
                      const SizedBox(height: 20),
                      if (_tripStarted)
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            ElevatedButton.icon(
                              style: ElevatedButton.styleFrom(
                                backgroundColor: Colors.red,
                                foregroundColor: Colors.white,
                                padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
                                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                              ),
                              onPressed: _handleCancel,
                              icon: const Icon(Icons.cancel),
                              label: const Text("Cancel Trip"),
                            ),
                            ElevatedButton.icon(
                              style: ElevatedButton.styleFrom(
                                backgroundColor: PrimaryColors.brightGreen,
                                foregroundColor: Colors.white,
                                padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
                                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                              ),
                              onPressed: _endTrip,
                              icon: const Icon(Icons.flag),
                              label: const Text("End Trip"),
                            ),
                          ],
                        )
                      else
                        SizedBox(
                          width: double.infinity,
                          child: ElevatedButton(
                            style: ElevatedButton.styleFrom(
                              backgroundColor: PrimaryColors.darkGreen,
                              foregroundColor: Colors.white,
                              padding: const EdgeInsets.symmetric(vertical: 16),
                              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                            ),
                            onPressed: (selectedLocation != null && selectedMethod != null) ? _startTrip : null,
                            child: Text("Start Trip", style: GoogleFonts.poppins(fontSize: 16, fontWeight: FontWeight.bold)),
                          ),
                        ),
                    ],
                  ),
                );
              },
            ),
          ],
        ),
      ),
    );
  }
}