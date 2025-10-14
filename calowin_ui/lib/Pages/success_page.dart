import 'package:calowin/common/colors_and_fonts.dart';
import 'package:calowin/common/user_profile.dart';
import 'package:calowin/control/words2widget_converter.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../control/apiService.dart';
import 'package:google_fonts/google_fonts.dart';

class SuccessPage extends StatefulWidget {
  final int caloriesBurnt;
  final int carbonSaved;
  final String tripMethod;
  final String currentLocation;
  final String destination;
  final double distance;
  final String userId;

  const SuccessPage({
    super.key,
    required this.caloriesBurnt,
    required this.carbonSaved,
    required this.tripMethod,
    required this.currentLocation,
    required this.destination,
    required this.distance,
    required this.userId,
  });

  @override
  State<SuccessPage> createState() => _SuccessPageState();
}

class _SuccessPageState extends State<SuccessPage> with SingleTickerProviderStateMixin {
  int totalCarbonSavedExp = 0;
  int totalCalorieBurntExp = 0;
  String carbonSavedMedal = "No Medal";
  String calorieBurntMedal = "No Medal";
  late String _userId;
  late UserProfile profileChangeNotify;

  // Thresholds for medal levels
  final int pointsToNextBronze = 1000;
  final int pointsToNextSilver = 5000;
  final int pointsToNextGold = 10000;
  final int pointsToNextPlatinum = 15000;

  int maxCarbon = 0;
  int maxCalorie = 0;

  late AnimationController _controller;
  late Animation<double> _animation;

  @override
  void initState() {
    super.initState();
    _userId = widget.userId;
    _controller = AnimationController(
      duration: const Duration(seconds: 2),
      vsync: this,
    );
    _animation = Tween<double>(begin: 0.0, end: 1.0).animate(_controller);
    fetchAchievements();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    profileChangeNotify = Provider.of<UserProfile>(context,listen: false);
  }

  @override
  void didUpdateWidget(SuccessPage oldWidget) {
    super.didUpdateWidget(oldWidget);
    fetchAchievements();
  }

  Future<void> fetchAchievements() async {
    ApiService apiService = ApiService();
    var achievements = await apiService.getAchievementProgress(_userId);
  

    setState(() {
      totalCarbonSavedExp = achievements['totalCarbonSavedExp'];
      profileChangeNotify.setCarbonSaved(totalCarbonSavedExp);

      totalCalorieBurntExp = achievements['totalCalorieBurntExp'];
      profileChangeNotify.setCalorieBurn(totalCalorieBurntExp);

      List<String> medals = [];
      carbonSavedMedal = achievements['carbonSavedMedal'];
      medals.add(carbonSavedMedal);
      print(carbonSavedMedal);

      calorieBurntMedal = achievements['calorieBurntMedal'];
      medals.add(calorieBurntMedal);
      print(calorieBurntMedal);

      profileChangeNotify.setBadges(medals);

      profileChangeNotify.updateProfile();

    });
    _controller.forward();
    maxCarbon = _retrieveThreshold(totalCarbonSavedExp);
    maxCalorie = _retrieveThreshold(totalCalorieBurntExp);
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: PrimaryColors.dullGreen,
      body: Center(
        child: SingleChildScrollView(
          child: Card(
            color: PrimaryColors.dullGreen,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(16),
            ),
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                mainAxisSize: MainAxisSize.min,
                children: [
                  Text(
                    "Congratulations! 🎉",
                    style: GoogleFonts.rammettoOne(
                          fontSize: 30,
                          fontWeight: FontWeight.bold,
                          color: Colors.white),
                    textAlign: TextAlign.center,
                  ),
                  SizedBox(height: 10),
                  Text(
                    "You have traveled ${widget.distance.toStringAsFixed(2)} km to ${widget.destination}.",
                    style: GoogleFonts.openSans(
                      fontSize: 16,
                      fontWeight: FontWeight.w600,
                      color: Colors.black54,
                    ),
                    textAlign: TextAlign.center,
                  ),
                  SizedBox(height: 20),
                  _buildProgressSection(
                    title: "Carbon Saved EXP: ${_formatExpDisplay(totalCarbonSavedExp, maxCarbon)}",
                    value: totalCarbonSavedExp,
                    medal: carbonSavedMedal,
                    gainedExp: widget.carbonSaved,
                    threshold: maxCarbon,
                    medalType: "Eco"
                  ),
                  SizedBox(height: 15),
                  _buildProgressSection(
                    title: "Calories Burnt EXP: ${_formatExpDisplay(totalCalorieBurntExp, maxCalorie)}",
                    value: totalCalorieBurntExp,
                    medal: calorieBurntMedal,
                    gainedExp: widget.caloriesBurnt,
                    threshold: maxCalorie,
                    medalType: "Calorie"
                  ),
                  SizedBox(height: 20),
                  ElevatedButton(
                    style: ElevatedButton.styleFrom(
                      padding: EdgeInsets.symmetric(horizontal: 30, vertical: 12),
                      backgroundColor: Colors.green[700],
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(8),
                      ),
                    ),
                    onPressed: () {
                      Navigator.pop(context);
                    },
                    child: Text(
                      "Back to Map",
                      style: GoogleFonts.openSans(
                        fontSize: 18,
                        fontWeight: FontWeight.w600,
                        color: Colors.white,
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  String _formatExpDisplay(int currentExp, int threshold) {
    return currentExp >= threshold ? "$threshold/$threshold" : "$currentExp/$threshold";
  }

  int _retrieveThreshold(int value) {
    if (value >= pointsToNextGold) return pointsToNextPlatinum;
    if (value >= pointsToNextSilver) return pointsToNextGold;
    if (value >= pointsToNextBronze) return pointsToNextSilver;
    return pointsToNextBronze;
  }

  Widget _buildProgressSection({
  required String title,
  required int value,
  required String medal,
  required int gainedExp,
  required int threshold,
  required String medalType,
}) {
  double progress = (value >= threshold) ? 1.0 : value / threshold;
  progress = progress.clamp(0.0, 1.0);

  // Get the next threshold medal if the user is below the current medal

  String bgToDisplay = _getBG(value, threshold);

  // Select the correct medal image based on the threshold medal
  Image? medalImage = _getMedalImage(value,threshold,medal,medalType);

  return Container(
    decoration: _getCardBackgroundImage(bgToDisplay),
    child: Padding(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        children: [
          Text(
            title,
            style: GoogleFonts.openSans(
              fontSize: 20,
              fontWeight: FontWeight.bold,
            ),
            textAlign: TextAlign.center,
          ),
          SizedBox(height: 10),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              // Show the next threshold medal if below the current level
              SizedBox(
                height: 60,
                width: 80,
                child: Column(
                  children: [
                    SizedBox(height: 40,width: 40,child: medalImage),
                    Text("Next Level",style: TextStyle(fontSize: 10),)
                  ],
                ),
              ),
              SizedBox(width: 8),
              Expanded(
                child: AnimatedBuilder(
                  animation: _animation,
                  builder: (context, child) {
                    return LinearProgressIndicator(
                      value: progress * _animation.value,
                      backgroundColor: Colors.grey[300],
                      color: Colors.red,
                      minHeight: 8,
                    );
                  },
                ),
              ),
              SizedBox(width: 10),
              Text(
                value >= threshold ? "MAX" : " +$gainedExp EXP",
                style: GoogleFonts.openSans(
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                ),
                textAlign: TextAlign.center,
              ),
            ],
          ),
        ],
      ),
    ),
  );
}

Image? _getMedalImage(int value, int threshold,String medal,String type) {

  if (value < pointsToNextBronze) {
    return Words2widgetConverter.convert("${type}Bronze");  // Below Bronze level, show Bronze
  } else if (value < pointsToNextSilver) {
    return Words2widgetConverter.convert("${type}Silver"); 
  } else if (value < pointsToNextGold) {
    return Words2widgetConverter.convert("${type}Gold"); 
  } else if (value < pointsToNextPlatinum) {
    return Words2widgetConverter.convert("${type}Platinum"); 
  } else {
    return Words2widgetConverter.convert("${type}Platinum"); 
  }
}
String _getBG(int value, int threshold) {
  if (value >= pointsToNextPlatinum) {
    return "EcoPlatinum";  // Platinum or higher level
  } else if (value >= pointsToNextGold) {
    return "EcoGold";       // Gold level achieved
  } else if (value >= pointsToNextSilver) {
    return "EcoSilver";     // Silver level achieved
  } else if (value >= pointsToNextBronze) {
    return "EcoBronze";     // Bronze level achieved
  } else {
    return "No Medal";      // Below Bronze level
  }
}


  BoxDecoration _getCardBackgroundImage(String medal) {
    switch (medal) {
      case "CaloriePlatinum":
      case "EcoPlatinum":
        return BoxDecoration(
          image: DecorationImage(
            image: AssetImage('assets/images/Platinum.jpg'),
            fit: BoxFit.cover,
          ),
        );
      case "CalorieGold":
      case "EcoGold":
        return BoxDecoration(
          image: DecorationImage(
            image: AssetImage('assets/images/Gold.jpg'),
            fit: BoxFit.cover,
          ),
        );
      case "CalorieSilver":
      case "EcoSilver":
        return BoxDecoration(
          image: DecorationImage(
            image: AssetImage('assets/images/Silver.jpg'),
            fit: BoxFit.cover,
          ),
        );
      case "CalorieBronze":
      case "EcoBronze":
        return BoxDecoration(
          image: DecorationImage(
            image: AssetImage('assets/images/Bronze.jpg'),
            fit: BoxFit.cover,
          ),
        );
      default:
        return BoxDecoration(color: Colors.white);
    }
  }
}
