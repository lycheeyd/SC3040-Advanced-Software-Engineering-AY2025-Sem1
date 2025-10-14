import 'package:calowin/common/colors_and_fonts.dart';
import 'package:calowin/common/user_profile.dart';
import 'package:calowin/control/words2widget_converter.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../control/apiService.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:confetti/confetti.dart'; // NEW: Import the confetti package
import 'dart:math'; // NEW: Import for the confetti animation

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

// MODIFIED: Removed SingleTickerProviderStateMixin as it's no longer needed for the progress animation.
class _SuccessPageState extends State<SuccessPage> {
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

  // NEW: Controller for the confetti animation.
  late ConfettiController _confettiController;

  @override
  void initState() {
    super.initState();
    _userId = widget.userId;
    // NEW: Initialize the confetti controller.
    _confettiController = ConfettiController(duration: const Duration(seconds: 2));
    fetchAchievements();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    profileChangeNotify = Provider.of<UserProfile>(context,listen: false);
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

      calorieBurntMedal = achievements['calorieBurntMedal'];
      medals.add(calorieBurntMedal);

      profileChangeNotify.setBadges(medals);
      profileChangeNotify.updateProfile();

      maxCarbon = _retrieveThreshold(totalCarbonSavedExp);
      maxCalorie = _retrieveThreshold(totalCalorieBurntExp);
    });

    // NEW: Play the confetti animation after data is loaded.
    _confettiController.play();
  }

  @override
  void dispose() {
    // NEW: Dispose of the confetti controller.
    _confettiController.dispose();
    super.dispose();
  }

  int _retrieveThreshold(int value) {
    if (value >= pointsToNextGold) return pointsToNextPlatinum;
    if (value >= pointsToNextSilver) return pointsToNextGold;
    if (value >= pointsToNextBronze) return pointsToNextSilver;
    return pointsToNextBronze;
  }

  // NEW: Refactored progress section into a more stylish card.
  Widget _buildProgressCard({
    required String title,
    required int currentValue,
    required int gainedExp,
    required int maxValue,
    required String medalType,
    required IconData icon,
    required Color color,
  }) {
    double progress = (currentValue / maxValue).clamp(0.0, 1.0);
    Image? nextMedalImage = _getMedalImage(currentValue, medalType);

    return Card(
      elevation: 4,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            Row(
              children: [
                Icon(icon, color: color, size: 24),
                const SizedBox(width: 8),
                Text(title, style: GoogleFonts.poppins(fontSize: 18, fontWeight: FontWeight.bold)),
                const Spacer(),
                if (nextMedalImage != null)
                  SizedBox(height: 30, width: 30, child: nextMedalImage),
              ],
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: ClipRRect(
                    borderRadius: BorderRadius.circular(10),
                    child: TweenAnimationBuilder<double>(
                      duration: const Duration(milliseconds: 1500),
                      curve: Curves.easeInOut,
                      tween: Tween<double>(begin: 0, end: progress),
                      builder: (context, value, _) => LinearProgressIndicator(
                        value: value,
                        backgroundColor: color.withOpacity(0.2),
                        color: color,
                        minHeight: 12,
                      ),
                    ),
                  ),
                ),
                const SizedBox(width: 12),
                Text(
                  "+$gainedExp EXP",
                  style: GoogleFonts.poppins(fontSize: 14, fontWeight: FontWeight.bold, color: color),
                ),
              ],
            ),
            const SizedBox(height: 8),
            Align(
              alignment: Alignment.centerLeft,
              child: Text(
                "$currentValue / $maxValue",
                style: GoogleFonts.poppins(fontSize: 12, color: Colors.black54),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Image? _getMedalImage(int value, String type) {
    if (value < pointsToNextBronze) return Words2widgetConverter.convert("${type}Bronze");
    if (value < pointsToNextSilver) return Words2widgetConverter.convert("${type}Silver");
    if (value < pointsToNextGold) return Words2widgetConverter.convert("${type}Gold");
    return Words2widgetConverter.convert("${type}Platinum");
  }

  // MODIFIED: The main build method is completely redesigned.
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Stack(
        alignment: Alignment.topCenter,
        children: [
          Container(
            decoration: const BoxDecoration(
              gradient: LinearGradient(
                colors: [PrimaryColors.dullGreen, PrimaryColors.darkGreen],
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
              ),
            ),
          ),
          SafeArea(
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Spacer(),
                  Text(
                    "Trip Complete!",
                    style: GoogleFonts.rammettoOne(
                        fontSize: 32,
                        fontWeight: FontWeight.bold,
                        color: Colors.white),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 12),
                  Text(
                    "You traveled ${widget.distance.toStringAsFixed(2)} km to ${widget.destination}.",
                    style: GoogleFonts.poppins(
                      fontSize: 16,
                      color: Colors.white70,
                    ),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 32),
                  _buildProgressCard(
                    title: "Carbon Saved",
                    currentValue: totalCarbonSavedExp,
                    gainedExp: widget.carbonSaved,
                    maxValue: maxCarbon,
                    medalType: "Eco",
                    icon: Icons.eco,
                    color: Colors.green,
                  ),
                  const SizedBox(height: 16),
                  _buildProgressCard(
                    title: "Calories Burnt",
                    currentValue: totalCalorieBurntExp,
                    gainedExp: widget.caloriesBurnt,
                    maxValue: maxCalorie,
                    medalType: "Calorie",
                    icon: Icons.local_fire_department,
                    color: Colors.orange,
                  ),
                  const Spacer(),
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton(
                      style: ElevatedButton.styleFrom(
                        padding: const EdgeInsets.symmetric(vertical: 16),
                        backgroundColor: Colors.white,
                        foregroundColor: PrimaryColors.darkGreen,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(12),
                        ),
                      ),
                      onPressed: () => Navigator.pop(context),
                      child: Text(
                        "Back to Map",
                        style: GoogleFonts.poppins(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
          // NEW: The confetti widget for the celebration effect.
          Align(
            alignment: Alignment.topCenter,
            child: ConfettiWidget(
              confettiController: _confettiController,
              blastDirection: pi / 2, // Downwards
              particleDrag: 0.05,
              emissionFrequency: 0.05,
              numberOfParticles: 20,
              gravity: 0.1,
              shouldLoop: false,
              colors: const [
                Colors.green, Colors.blue, Colors.pink, Colors.orange, Colors.purple
              ],
            ),
          ),
        ],
      ),
    );
  }
}