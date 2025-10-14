import 'package:calowin/common/colors_and_fonts.dart';
import 'package:calowin/control/current_location.dart';
import 'package:calowin/control/page_navigator.dart';
import 'package:calowin/control/park_retriever.dart';
import 'package:calowin/control/weather_retriever.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:loading_animation_widget/loading_animation_widget.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';


//default address 1.3521,103.8198
class WellnessZonePage extends StatefulWidget {
  const WellnessZonePage({super.key});

  @override
  State<WellnessZonePage> createState() => _WellnessZonePageState();
}

class _WellnessZonePageState extends State<WellnessZonePage> {
  //for locations
  CurrentLocation _userCurrentLocation = CurrentLocation();
  late GoogleMapController _mapController;

  //for maps
  Marker? _selectedLocationMarker;
  Marker? _currentLocationMarker;

  //private variables
  int _currentIndex = -1;
  final double _sliderMin = 1;
  final double _sliderMax = 20;
  late Icon _weatherIcon = Icon(Icons.help_outline, color: Colors.grey.shade300);
  late String _weatherForecast = "Loading";
  double _sliderValue = 5;
  final ParkRetriever _parkRetriever = ParkRetriever();
  final WeatherRetriever _weatherRetriever = WeatherRetriever();
  // MODIFIED: This boolean is now used to expand the list item instead of showing a map overlay.
  bool _showWeather = false;
  late String _selectedPark;
  bool _loading = true;

  List<Park> _wellnessZones = [];
  List<Park> _filteredZones = [];


  //functions for GoogleMap
  void _onMapCreated(GoogleMapController controller) {
    _mapController = controller;
  }

  Future<void> _setUserLocation() async {
    try {
      await _userCurrentLocation.getCurrentLocation();
      //print("User location: ${_userCurrentLocation.name} Lat: ${_userCurrentLocation.latitude} Long: ${_userCurrentLocation.longitude}");
      setState(() {
        _currentLocationMarker = Marker(
          markerId: MarkerId('currentLocation'),
          position: LatLng(_userCurrentLocation.latitude ?? 0, _userCurrentLocation.longitude ?? 0),
          infoWindow: InfoWindow(title: 'Current Location'),
          icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueGreen), // Set the color
        );
      });

      _mapController.animateCamera(
        CameraUpdate.newLatLngZoom(
          LatLng(_userCurrentLocation.latitude ?? 0, _userCurrentLocation.longitude ?? 0),
          15,
        ),
      );

      _retrieveWellnessZones();
    } catch (e) {
      print('Error initializing location: $e');
    }
  }

  void _retrieveWellnessZones() async {
    //enable loading screen
    setState(() {
      _wellnessZones = [];
      _filteredZones = [];
      _loading = true;
    });

    // uncomment when the server can run
    _wellnessZones = await _parkRetriever.retrievePark(_userCurrentLocation.latitude ?? 1.3521, _userCurrentLocation.longitude ?? 103.8198);

    //disable loading screen after finished loading
    setState(() {
      if (_wellnessZones.isEmpty) {
        _loading = true;
      } else {
        _loading = false;
      }
      _wellnessZones = _wellnessZones;
    });

    _filterWellnessZones(_sliderValue);
  }

  void _filterWellnessZones(double radius) {
    setState(() {
      _filteredZones = _wellnessZones.where((zone) {
        return zone.distance <=
            _sliderValue; // Show only zones within the radius
      }).toList();
      _filteredZones.sort((a, b) => a.distance.compareTo(b.distance));
      _sliderValue = radius;
    });
  }

  // Get const icon based on weather forecast
  void setWeatherIcon() {
    Icon icon;
    switch (_weatherForecast) {
      case 'Fair':
      case 'Fair (Day)':
      case 'Fair (Night)':
      case 'Fair and Warm':
        icon = const Icon(Icons.wb_sunny, color: Colors.yellow);
        break;

      case 'Partly Cloudy':
      case 'Partly Cloudy (Day)':
      case 'Partly Cloudy (Night)':
        icon = const Icon(Icons.wb_cloudy_outlined, color: Colors.white);
        break;

      case 'Cloudy':
        icon =  Icon(Icons.cloud, color: Colors.grey.shade300);
        break;

      case 'Hazy':
      case 'Slightly Hazy':
        icon = const Icon(Icons.deblur, color: Colors.orange);
        break;

      case 'Windy':
        icon = const Icon(Icons.air, color: Colors.blue);
        break;

      case 'Mist':
      case 'Fog':
        icon =  Icon(Icons.blur_on, color: Colors.grey.shade300);
        break;

      case 'Light Rain':
      case 'Moderate Rain':
      case 'Heavy Rain':
        icon = const Icon(Icons.grain, color: Colors.blueAccent);
        break;

      case 'Passing Showers':
      case 'Light Showers':
      case 'Showers':
      case 'Heavy Showers':
        icon = const Icon(Icons.grain, color: Colors.blue);
        break;

      case 'Thundery Showers':
      case 'Heavy Thundery Showers':
      case 'Heavy Thundery Showers with Gusty Winds':
        icon = const Icon(Icons.flash_on, color: Colors.purple);
        break;

      default:
        icon =  Icon(Icons.help_outline, color: Colors.grey.shade300);
    }

    setState(() {
      _weatherIcon = icon;
    });
  }

  void _setWeather(double lat, double lon) async {
    _weatherRetriever.setLocation(lat, lon);
    String forecast = await _weatherRetriever.retrieveWeather();
    setState(() {
      _weatherForecast = forecast;
      setWeatherIcon();
    });
    //print(_weatherForecast);
  }

  void _onListItemTap(
      int index, double? lat, double? lon, String selectedPark) {
    setState(() {
      if (_currentIndex == index) {
        // This allows collapsing the item if tapped again
        _currentIndex = -1;
        _selectedLocationMarker = null;
        _showWeather = false;
      } else {
        _currentIndex = index;
        _showWeather = true;
        _selectedPark = selectedPark;
        _selectedLocationMarker = lat!=null && lon!=null ? Marker(
          markerId: MarkerId('SelectedLocation'),
          position: LatLng(lat, lon),
          infoWindow: InfoWindow(title: selectedPark),
          icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueRed), // Set the color
        ) : null;

        _mapController.animateCamera(
          CameraUpdate.newLatLngZoom(
            LatLng(lat ??_userCurrentLocation.latitude ?? 0, lon ?? _userCurrentLocation.longitude ?? 0),
            12,
          ),
        );
        if (lat == null || lon == null) {
          _weatherForecast = "Weather Not Available";
          _weatherIcon = const Icon(Icons.error_outline_rounded);
        } else {
          _setWeather(lat, lon);
        }
      }
    });
  }

  void _handleGO(Park zone) {
    final pageNavigatorState =
    context.findAncestorStateOfType<PageNavigatorState>();
    //change here
    if (pageNavigatorState != null) {
      pageNavigatorState.navigateToPage(0,params: {'targetName': zone.name , 'targetLat':zone.closestPoint['Lat'], 'targetLong':zone.closestPoint['Lon']}); // Navigate to AddFriendsPage
    }
  }

  // NEW: Refactored build method for list items to improve UI/UX.
  Widget _buildListItem(int index, Park zone) {
    bool isSelected = _currentIndex == index;

    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
      elevation: isSelected ? 6.0 : 2.0,
      color: isSelected ? Color.fromARGB(255, 179, 219, 179) : Colors.white,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
      child: InkWell(
        onTap: () => _onListItemTap(index, zone.closestPoint['Lat'],
            zone.closestPoint['Lon'], zone.name),
        child: Padding(
          padding: const EdgeInsets.all(12.0),
          child: Column(
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Expanded(
                    child: Text(
                      zone.name,
                      style: GoogleFonts.poppins(fontWeight: FontWeight.bold, fontSize: 16),
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                  const SizedBox(width: 10),
                  Row(
                    children: [
                      Text(
                        "${zone.distance.toStringAsFixed(1)} km",
                        style: GoogleFonts.poppins(
                            fontSize: 14, fontWeight: FontWeight.w600),
                      ),
                      const SizedBox(width: 10),
                      SizedBox(
                        width: 60,
                        height: 35,
                        child: ElevatedButton(
                            onPressed: () => _handleGO(zone),
                            style: ElevatedButton.styleFrom(
                              backgroundColor: Colors.purple,
                              padding: const EdgeInsets.symmetric(horizontal: 10),
                              shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(20),
                              ),
                            ),
                            child: const Text(
                              "Go",
                              style: TextStyle(fontSize: 14, color: Colors.white),
                            )),
                      )
                    ],
                  ),
                ],
              ),
              // NEW: Animated visibility for the weather forecast section.
              AnimatedSize(
                duration: const Duration(milliseconds: 300),
                curve: Curves.easeInOut,
                child: isSelected && _showWeather
                    ? Container(
                    padding: const EdgeInsets.only(top: 12.0),
                    child: Column(
                      children: [
                        const Divider(color: Colors.black26),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            SizedBox(
                              width: 40,
                              height: 40,
                              child: Transform.scale(
                                  scale: 1.5, child: _weatherIcon),
                            ),
                            const SizedBox(width: 15),
                            Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                const Text(
                                  "2-hour Forecast",
                                  style: TextStyle(fontSize: 12, color: Colors.black54),
                                ),
                                Text(
                                  _weatherForecast,
                                  style: GoogleFonts.poppins(
                                      color: Colors.black,
                                      fontSize: 16,
                                      fontWeight: FontWeight.w600
                                  ),
                                ),
                              ],
                            ),
                          ],
                        )
                      ],
                    )
                )
                    : const SizedBox.shrink(),
              ),
            ],
          ),
        ),
      ),
    );
  }

  //initial state of this widget
  @override
  void initState() {
    super.initState();
    _setUserLocation();
  }

  // MODIFIED: Entire build method is updated for a cleaner UI.
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[200], // Use a lighter background for contrast
      body: Column(
        children: [
          SizedBox(
            height: 350, // Slightly reduced map height
            child: GoogleMap(
              onMapCreated: _onMapCreated,
              initialCameraPosition: CameraPosition(
                target: LatLng(_userCurrentLocation.latitude ?? 1.3521, _userCurrentLocation.longitude ?? 103.8198),
                zoom: 10,
              ),
              myLocationButtonEnabled: false,
              myLocationEnabled: true,
              mapType: MapType.terrain,
              markers: {
                if (_currentLocationMarker != null) _currentLocationMarker!,
                if (_selectedLocationMarker != null) _selectedLocationMarker!,
              },
            ),
          ),
          // NEW: Moved the slider to a dedicated container.
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
            color: Colors.white,
            child: Column(
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text("Search Radius", style: GoogleFonts.poppins(fontWeight: FontWeight.w600)),
                    Text("${_sliderValue.toStringAsFixed(1)} km", style: GoogleFonts.poppins(fontWeight: FontWeight.bold, color: PrimaryColors.darkGreen)),
                  ],
                ),
                Slider(
                  activeColor: PrimaryColors.darkGreen,
                  inactiveColor: PrimaryColors.dullGreen.withOpacity(0.5),
                  value: _sliderValue,
                  min: _sliderMin,
                  max: _sliderMax,
                  divisions: 190,
                  onChanged: (double value) {
                    _filterWellnessZones(value);
                  },
                ),
              ],
            ),
          ),
          // NEW: A cleaner header for the list section.
          Container(
            color: PrimaryColors.dullGreen,
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  "Nearby Wellness Zones",
                  style: GoogleFonts.poppins(
                      color: Colors.white,
                      fontWeight: FontWeight.bold,
                      fontSize: 16
                  ),
                ),
                IconButton(
                  padding: EdgeInsets.zero,
                  constraints: const BoxConstraints(),
                  iconSize: 24,
                  onPressed: _setUserLocation,
                  icon: const Icon(
                    Icons.refresh,
                    color: Colors.white,
                  ),
                ),
              ],
            ),
          ),
          Expanded(
            child: Stack(
              children: [
                Container(
                  color: Colors.grey[200],
                  child: ListView.builder(
                    padding: const EdgeInsets.only(top: 5, bottom: 5),
                    scrollDirection: Axis.vertical,
                    shrinkWrap: true,
                    itemCount: _filteredZones.length,
                    itemBuilder: (context, index) {
                      Park currentItem = _filteredZones[index];
                      return _buildListItem(index, currentItem);
                    },
                  ),
                ),
                if (_loading)
                  Center(
                    child: LoadingAnimationWidget.discreteCircle(
                        color: PrimaryColors.orange,
                        size: 100,
                        secondRingColor: PrimaryColors.brightGreen,
                        thirdRingColor: PrimaryColors.orange),
                  )
              ],
            ),
          )
        ],
      ),
    );
  }
}