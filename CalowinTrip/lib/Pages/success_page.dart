import 'package:calowin/common/colors_and_fonts.dart';
import 'package:flutter/material.dart';
import '../control/apiService.dart';
import '../common/medals.dart';
import 'package:google_fonts/google_fonts.dart';

class SuccessPage extends StatefulWidget {
  final int caloriesBurnt;
  final int carbonSaved;
  final String tripMethod;
  final String currentLocation;
  final String destination;
  final double distance;

  SuccessPage({
    required this.caloriesBurnt,
    required this.carbonSaved,
    required this.tripMethod,
    required this.currentLocation,
    required this.destination,
    required this.distance,
  });

  @override
  _SuccessPageState createState() => _SuccessPageState();
}

class _SuccessPageState extends State<SuccessPage> with SingleTickerProviderStateMixin {
  int totalCarbonSavedExp = 0;
  int totalCalorieBurntExp = 0;
  String carbonSavedMedal = "No Medal";
  String calorieBurntMedal = "No Medal";

  // Define thresholds for medals
  final int pointsToNextCarbonBronze = 1000;
  final int pointsToNextCarbonSilver = 5000;
  final int pointsToNextCarbonGold = 10000;
  final int pointsToNextCarbonPlatinum = 15000;

  final int pointsToNextCalorieBronze = 1000;
  final int pointsToNextCalorieSilver = 5000;
  final int pointsToNextCalorieGold = 10000;
  final int pointsToNextCaloriePlatinum = 15000;

  late AnimationController _controller;
  late Animation<double> _animation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: const Duration(seconds: 2),
      vsync: this,
    );
    _animation = Tween<double>(begin: 0.0, end: 1.0).animate(_controller);

    fetchAchievements();
  }

  Future<void> fetchAchievements() async {
    ApiService apiService = ApiService();
    var achievements = await apiService.getAchievementProgress();
    setState(() {
      totalCarbonSavedExp = achievements['totalCarbonSavedExp'];
      totalCalorieBurntExp = achievements['totalCalorieBurntExp'];
      carbonSavedMedal = achievements['carbonSavedMedal'];
      calorieBurntMedal = achievements['calorieBurntMedal'];
    });
    _controller.forward(); // Start the animation
  }

  @override
  void dispose() {
    _controller.dispose(); // Dispose of the controller
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
                    "You have traveled ${widget.distance} km to ${widget.destination}.",
                    style: GoogleFonts.openSans(
                      fontSize: 16,
                      fontWeight: FontWeight.w600,
                      color: Colors.black54,
                    ),
                    textAlign: TextAlign.center,
                  ),
                  SizedBox(height: 20),
                  _buildProgressSection(
                    title: "Carbon Saved EXP: ${_formatExpDisplay(totalCarbonSavedExp, pointsToNextCarbonPlatinum)}",
                    value: totalCarbonSavedExp,
                    medal: carbonSavedMedal,
                    pointsToNextBronze: pointsToNextCarbonBronze,
                    pointsToNextSilver: pointsToNextCarbonSilver,
                    pointsToNextGold: pointsToNextCarbonGold,
                    pointsToNextPlatinum: pointsToNextCarbonPlatinum,
                    gainedExp: widget.carbonSaved,
                    isCarbon: true,
                  ),
                  SizedBox(height: 15),
                  _buildProgressSection(
                    title: "Calories Burnt EXP: ${_formatExpDisplay(totalCalorieBurntExp, pointsToNextCaloriePlatinum)}",
                    value: totalCalorieBurntExp,
                    medal: calorieBurntMedal,
                    pointsToNextBronze: pointsToNextCalorieBronze,
                    pointsToNextSilver: pointsToNextCalorieSilver,
                    pointsToNextGold: pointsToNextCalorieGold,
                    pointsToNextPlatinum: pointsToNextCaloriePlatinum,
                    gainedExp: widget.caloriesBurnt,
                    isCarbon: false,
                  ),
                  SizedBox(height: 20),
                  ElevatedButton(
                    style: ElevatedButton.styleFrom(
                      padding: EdgeInsets.symmetric(horizontal: 30, vertical: 12), backgroundColor: Colors.green[700],
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

  String _formatExpDisplay(int currentExp, int platinumThreshold) {
    return currentExp >= platinumThreshold ? "$platinumThreshold/$platinumThreshold" : "$currentExp/$platinumThreshold";
  }

  Widget _buildProgressSection({
    required String title,
    required int value,
    required String medal,
    required int pointsToNextBronze,
    required int pointsToNextSilver,
    required int pointsToNextGold,
    required int pointsToNextPlatinum,
    required int gainedExp,
    required bool isCarbon,
  }) {
    double progress = 0.0;
    String nextMedal = "No Medal";

    if (value >= pointsToNextPlatinum) {
      nextMedal = "MAX";
      medal = "Platinum";
      progress = 1.0;
    } else if (value >= pointsToNextGold) {
      nextMedal = "Platinum";
      medal = "Gold";
      progress = (value - pointsToNextGold) / (pointsToNextPlatinum - pointsToNextGold);
    } else if (value >= pointsToNextSilver) {
      nextMedal = "Gold";
      medal = "Silver";
      progress = (value - pointsToNextSilver) / (pointsToNextGold - pointsToNextSilver);
    } else if (value >= pointsToNextBronze) {
      nextMedal = "Silver";
      medal = "Bronze";
      progress = (value - pointsToNextBronze) / (pointsToNextSilver - pointsToNextBronze);
    } else {
      nextMedal = "Bronze";
      progress = value / pointsToNextBronze;
    }

    progress = progress.clamp(0.0, 1.0);

    return Container(
      decoration: _getCardBackgroundImage(medal), // Use image for background
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
              children: [
                if (nextMedal != "No Medal") ...[
                  _getMedalImage(nextMedal, isCarbon: isCarbon, size: 25),
                  SizedBox(width: 8),
                ],
                Expanded(
                  child: AnimatedBuilder(
                    animation: _animation,
                    builder: (context, child) {
                      double animatedProgress = progress * _animation.value;
                      return LinearProgressIndicator(
                        value: animatedProgress,
                        backgroundColor: Colors.grey[300],
                        color: Colors.red,
                        minHeight: 8,
                      );
                    },
                  ),
                ),
                SizedBox(width: 10),
                Text(
                  value >= pointsToNextPlatinum ? "MAX" : "+$gainedExp EXP",
                  style: GoogleFonts.openSans(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                  ),
                  textAlign: TextAlign.center,
                ),
              ],
            ),
            SizedBox(height: 10),
            Text(
              "Current Badge:",
              style: GoogleFonts.openSans(
                fontSize: 18,
                fontWeight: FontWeight.w600,
              ),
              textAlign: TextAlign.center,
            ),
            SizedBox(height: 5),
            _getMedalImage(medal, isCarbon: isCarbon),
            SizedBox(height: 5),
          ],
        ),
      ),
    );
  }

  Widget _getMedalImage(String medal, {required bool isCarbon, double size = 50}) {
    if (isCarbon) {
      switch (medal) {
        case "Gold":
          return SizedBox(width: size, height: size, child: Medals.ecoGold);
        case "Silver":
          return SizedBox(width: size, height: size, child: Medals.ecoSilver);
        case "Bronze":
          return SizedBox(width: size, height: size, child: Medals.ecoBronze);
        case "Platinum":
          return SizedBox(width: size, height: size, child: Medals.ecoPlatinum);
        default:
          return Container();
      }
    } else {
      switch (medal) {
        case "Gold":
          return SizedBox(width: size, height: size, child: Medals.calorieGold);
        case "Silver":
          return SizedBox(width: size, height: size, child: Medals.calorieSilver);
        case "Bronze":
          return SizedBox(width: size, height: size, child: Medals.calorieBronze);
        case "Platinum":
          return SizedBox(width: size, height: size, child: Medals.caloriePlatinum);
        default:
          return Container();
       }
    }
  }

  BoxDecoration _getCardBackgroundImage(String medal) {
    switch (medal) {
      case "Platinum":
        return BoxDecoration(
          image: DecorationImage(
            image: AssetImage('assets/images/Platinum.jpg'), // Platinum image
            fit: BoxFit.cover,
          ),
        );
      case "Gold":
        return BoxDecoration(
          image: DecorationImage(
            image: AssetImage('assets/images/Gold.jpg'), // Gold image
            fit: BoxFit.cover,
          ),
        );
      case "Silver":
        return BoxDecoration(
          image: DecorationImage(
            image: AssetImage('assets/images/Silver.jpg'), // Silver image
            fit: BoxFit.cover,
          ),
        );
      case "Bronze":
        return BoxDecoration(
          image: DecorationImage(
            image: AssetImage('assets/images/Bronze.jpg'), // Bronze image
            fit: BoxFit.cover,
          ),
        );
      default:
        return BoxDecoration(
          color: Colors.white, // Default color if no medal is earned
        );
    }
  }
}
