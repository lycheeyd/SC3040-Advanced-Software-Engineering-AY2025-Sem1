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

    //for testing purposes
//     _wellnessZones =[
//   Park(
//     name: "East Coast Park",
//     distance: 5.2,
//     closestPoint: {
//       "Lat": 1.3012,
//       "Lon": 103.9123,
//     },
//   ),
//   Park(
//     name: "MacRitchie Reservoir Park",
//     distance: 8.1,
//     closestPoint: {
//       "Lat": 1.3427,
//       "Lon": 103.8205,
//     },
//   ),
//   Park(
//     name: "Bishan-Ang Mo Kio Park",
//     distance: 6.5,
//     closestPoint: {
//       "Lat": 1.3725,
//       "Lon": 103.8446,
//     },
//   ),
//   Park(
//     name: "Gardens by the Bay",
//     distance: 4.0,
//     closestPoint: {
//       "Lat": 1.2816,
//       "Lon": 103.8636,
//     },
//   ),
//   Park(
//     name: "Bukit Timah Nature Reserve",
//     distance: 10.3,
//     closestPoint: {
//       "Lat": 1.3483,
//       "Lon": 103.7767,
//     },
//   ),
//   Park(
//     name: "Singapore Botanic Gardens",
//     distance: 3.7,
//     closestPoint: {
//       "Lat": 1.3138,
//       "Lon": 103.8159,
//     },
//   ),
//   Park(
//     name: "Fort Canning Park",
//     distance: 2.5,
//     closestPoint: {
//       "Lat": 1.2956,
//       "Lon": 103.8454,
//     },
//   ),
//   Park(
//     name: "Pasir Ris Park",
//     distance: 12.0,
//     closestPoint: {
//       "Lat": 1.3837,
//       "Lon": 103.9465,
//     },
//   ),
//   Park(
//     name: "Sembawang Park",
//     distance: 15.0,
//     closestPoint: {
//       "Lat": 1.4581,
//       "Lon": 103.8262,
//     },
//   ),
//   Park(
//     name: "Labrador Nature Reserve",
//     distance: 7.0,
//     closestPoint: {
//       "Lat": 1.2686,
//       "Lon": 103.8029,
//     },
//   ),
// ];

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

      case 'Partly Cloudy':
      case 'Partly Cloudy (Day)':
      case 'Partly Cloudy (Night)':
        icon = const Icon(Icons.cloud, color: Colors.blueGrey);

      case 'Cloudy':
        icon =  Icon(Icons.cloud, color: Colors.grey.shade300);

      case 'Hazy':
      case 'Slightly Hazy':
        icon = const Icon(Icons.deblur, color: Colors.orange);

      case 'Windy':
        icon = const Icon(Icons.air, color: Colors.blue);

      case 'Mist':
      case 'Fog':
        icon =  Icon(Icons.blur_on, color: Colors.grey.shade300);

      case 'Light Rain':
      case 'Moderate Rain':
      case 'Heavy Rain':
        icon = const Icon(Icons.grain, color: Colors.blueAccent);

      case 'Passing Showers':
      case 'Light Showers':
      case 'Showers':
      case 'Heavy Showers':
        icon = const Icon(Icons.grain, color: Colors.blue);

      case 'Thundery Showers':
      case 'Heavy Thundery Showers':
      case 'Heavy Thundery Showers with Gusty Winds':
        icon = const Icon(Icons.flash_on, color: Colors.purple);

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
        _showWeather = false;
        _currentIndex = -1;
        _selectedLocationMarker = null;
      } else {
        _currentIndex = index;
        _showWeather = true;
        _selectedPark = selectedPark;
        _selectedLocationMarker = lat!=null && lon!=null ? Marker(
          markerId: MarkerId('SelectedLocation'),
          position: LatLng(lat, lon),
          infoWindow: InfoWindow(title: 'Selected Location'),
          icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueRed), // Set the color
        ) : null;

        _mapController.animateCamera(
          CameraUpdate.newLatLngZoom(
            LatLng(lat ??_userCurrentLocation.latitude ?? 0, lon ?? _userCurrentLocation.longitude ?? 0),
            12,
          ),
        );
      }
    });
    if (lat == null || lon == null) {
      setState(() {
        _weatherForecast = "Weather Not Available";
        _weatherIcon = const Icon(Icons.error_outline_rounded);
      });
    } else {
      _setWeather(lat, lon);
    }
  }

  void _handleGO(Park zone) {
    final pageNavigatorState =
        context.findAncestorStateOfType<PageNavigatorState>();
    //change here
    if (pageNavigatorState != null) {
      pageNavigatorState.navigateToPage(0,params: {'targetName': zone.name , 'targetLat':zone.closestPoint['Lat'], 'targetLong':zone.closestPoint['Lon']}); // Navigate to AddFriendsPage
    }
  }

  Widget _buildListItem(int index, Park zone) {
    Color tileColor = const Color.fromARGB(10, 0, 0, 0);
    Color selectedColor = const Color.fromARGB(255, 232, 231, 253);
    return Padding(
      padding: const EdgeInsets.only(top: 5, bottom: 5, left: 10, right: 3),
      child: Container(
        height: 60,
        width: 400,
        decoration: BoxDecoration(
          color: index == _currentIndex ? selectedColor : tileColor,
          borderRadius: BorderRadius.circular(10),
          border: const Border(
            bottom: BorderSide(
              color: Colors.grey,
              width: 2,
            ),
          ),
        ),
        child: ListTile(
          title: Text(
            zone.name,
            style:
                GoogleFonts.poppins(fontWeight: FontWeight.bold, fontSize: 15),
          ),
          trailing: SizedBox(
            width: 130,
            child: Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                Text(
                  "${zone.distance.toStringAsFixed(1)} km",
                  style: GoogleFonts.poppins(
                      fontSize: 14, fontWeight: FontWeight.w600),
                ),
                const SizedBox(
                  width: 10,
                ),
                SizedBox(
                  width: 60,
                  height: 30,
                  child: ElevatedButton(
                      onPressed: () => _handleGO(zone),
                      style: ElevatedButton.styleFrom(
                        elevation: 0,
                        backgroundColor: Colors.purple,
                        padding: const EdgeInsets.symmetric(
                            horizontal: 10, vertical: 5),
                        shape: RoundedRectangleBorder(
                          borderRadius:
                              BorderRadius.circular(50), // Rounded corners
                        ),
                      ),
                      child: const Text(
                        "Go",
                        style: TextStyle(fontSize: 12, color: Colors.white),
                      )),
                )
              ],
            ),
          ),
          onTap: () => _onListItemTap(index, zone.closestPoint['Lat'],
              zone.closestPoint['Lon'], zone.name),
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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: PrimaryColors.dullGreen,
      body: Column(
        children: [
          SizedBox(
            height: 400,
            child: Stack(
              children: [
                  GoogleMap(
                      onMapCreated: _onMapCreated,
                      initialCameraPosition: CameraPosition(
                        target: LatLng(_userCurrentLocation.latitude ?? 1.3521, _userCurrentLocation.longitude ?? 103.8198),
                        zoom: 10,
                      ),
                      myLocationButtonEnabled: false,
                      myLocationEnabled: true,
                      liteModeEnabled: false,
                      mapType: MapType.terrain,
                      markers: {
                        if (_currentLocationMarker != null) _currentLocationMarker!,
                        if (_selectedLocationMarker != null) _selectedLocationMarker!,
                      },
                    ),
                if (!_showWeather)
                  Padding(
                    padding:
                        const EdgeInsets.symmetric(vertical: 15, horizontal: 5),
                    child: Align(
                      alignment: Alignment.bottomCenter,
                      child: Container(
                        decoration: BoxDecoration(
                            color: const Color.fromARGB(150, 0, 0, 0),
                            borderRadius: BorderRadius.circular(10)),
                        height: 45,
                        width: 300,
                        child: Column(
                          children: [
                            const Padding(
                              padding:
                                  EdgeInsets.only(left: 20, top: 3, bottom: 0),
                              child: Align(
                                  alignment: Alignment.topLeft,
                                  child: Text(
                                    "Search Radius",
                                    style: TextStyle(color: Colors.white),
                                  )),
                            ),
                            Row(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                SizedBox(
                                  height: 18,
                                  width: 240,
                                  child: SliderTheme(
                                    data: SliderTheme.of(context).copyWith(
                                      thumbShape: const RoundSliderThumbShape(
                                          pressedElevation: 0,
                                          enabledThumbRadius:
                                              7.0), // Change thumb size here
                                      overlayShape:
                                          const RoundSliderOverlayShape(
                                              overlayRadius:
                                                  8.0), // Change overlay size
                                    ),
                                    child: Slider(
                                      activeColor: Colors.black,
                                      overlayColor:
                                          const WidgetStatePropertyAll(
                                              Colors.black),
                                      value: _sliderValue,
                                      min: _sliderMin,
                                      max: _sliderMax,
                                      divisions: 200,
                                      onChanged: (double value) {
                                        _filterWellnessZones(value);
                                      },
                                    ),
                                  ),
                                ),
                                const SizedBox(
                                  width: 7,
                                ),
                                Align(
                                    alignment: Alignment.bottomRight,
                                    child: Text(_sliderValue.toStringAsFixed(1),
                                        style: const TextStyle(
                                            color: Colors.white))),
                              ],
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
                if (_showWeather)
                  Padding(
                    padding:
                        const EdgeInsets.symmetric(vertical: 5, horizontal: 5),
                    child: Align(
                      alignment: Alignment.topCenter,
                      child: Container(
                        height: 65,
                        width: 270,
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(10),
                          color: const Color.fromARGB(150, 0, 0, 0),
                        ),
                        child: Padding(
                          padding: const EdgeInsets.symmetric(
                              vertical: 2, horizontal: 2),
                          child: Row(
                            mainAxisAlignment: MainAxisAlignment.spaceAround,
                            children: [
                              Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                mainAxisAlignment: MainAxisAlignment.center,
                                children: [
                                  Text(
                                    "Next 2-hr forecast",
                                    style: TextStyle(
                                        fontSize: 10,
                                        color: Colors.grey.shade200),
                                  ),
                                  Text(
                                    _weatherForecast,
                                    style: const TextStyle(
                                        color: Colors.white, fontSize: 12),
                                  ),
                                ],
                              ),
                              Column(
                                mainAxisAlignment: MainAxisAlignment.center,
                                crossAxisAlignment: CrossAxisAlignment.center,
                                children: [
                                  SizedBox(
                                    width: 100,
                                    child: Text(
                                      _selectedPark,
                                      textAlign: TextAlign.center,
                                      style: TextStyle(
                                          fontSize: 9,
                                          color: Colors.grey.shade100),
                                    ),
                                  ),
                                  SizedBox(
                                    width: 30,
                                    height: 30,
                                    child: Transform.scale(
                                        scale: 1.5, child: _weatherIcon),
                                  )
                                ],
                              )
                            ],
                          ),
                        ),
                      ),
                    ),
                  )
              ],
            ),
          ),
          Container(
            color: PrimaryColors.grey,
            height: 40,
            child: Stack(
              children: [
                // Use Expanded to take all the available space for the text
                const Center(
                  child: Text(
                    "Wellness Zones",
                    style: TextStyle(
                        color: Colors.black, fontWeight: FontWeight.bold),
                  ),
                ),
                Align(
                  alignment: Alignment.centerRight,
                  child: IconButton(
                    iconSize: 20,
                    onPressed: _setUserLocation,
                    icon: const Icon(
                      Icons.refresh,
                      color: Colors.black,
                    ),
                  ),
                ),
              ],
            ),
          ),
          Expanded(
            child: Stack(
              children: [
                SizedBox.expand(
                  child: Container(
                    color: Colors.white,
                    child: ListView.builder(
                      scrollDirection: Axis.vertical,
                      shrinkWrap: true,
                      itemCount: _filteredZones.length,
                      itemBuilder: (context, index) {
                        Park currentItem = _filteredZones[index];
                        return _buildListItem(index, currentItem);
                      },
                    ),
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
