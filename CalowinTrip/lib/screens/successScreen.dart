import 'package:calowin/common/colors_and_fonts.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class SuccessPage extends StatelessWidget {
  final String congratsText;

  const SuccessPage({super.key, required this.congratsText});

  Widget _progressBuilder(Color progressColor, int max, int currentProgress,
      Image badge, int progressIncrement) {
    return SizedBox(
      height: 80,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          SizedBox(
            height: 50,
            child: badge,
          ),
          const SizedBox(
              width: 20), // Add some spacing between the badge and progress
          Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.start, // Align text to start
            children: [
              Text(
                "$currentProgress/$max",
                style: GoogleFonts.averiaSerifLibre(
                    fontSize: 15, color: Colors.black),
              ),
              const SizedBox(height: 5), // Space between text and progress bar
              SizedBox(
                width: 200, // Set a fixed width for the progress bar
                child: LinearProgressIndicator(
                  value: currentProgress /
                      max, // 0.0 to 1.0 for determinate progress; null for indeterminate
                  backgroundColor: const Color.fromARGB(
                      100, 0, 0, 0), // Background color of the progress bar
                  valueColor: AlwaysStoppedAnimation<Color>(
                      progressColor), // Color of the progress indicator
                ),
              ),
            ],
          ),
          const SizedBox(
              width: 20), // Add some spacing before the increment text
          Text(
            "+$progressIncrement",
            style: GoogleFonts.averiaSerifLibre(
                fontSize: 20, color: progressColor),
          )
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: PrimaryColors.brightGreen,
      body: Padding(
        padding: const EdgeInsets.all(10.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            SizedBox(
              height: 300,
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text("Congratulations",
                      style: GoogleFonts.rammettoOne(
                          fontSize: 30,
                          fontWeight: FontWeight.bold,
                          color: Colors.white)),
                  const SizedBox(
                    height: 20,
                  ),
                  Text(congratsText,
                      style: GoogleFonts.rammettoOne(
                          fontSize: 15,
                          fontWeight: FontWeight.bold,
                          color: Colors.black)),
                ],
              ),
            ),
            SizedBox(
              height: 200,
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                 // _progressBuilder(
                     // Colors.yellow, 1000, 800, Medals.calorieBronze, 20),
                 // _progressBuilder(
                    // Colors.blue, 5000, 288, Medals.ecoSilver, 121),
                ],
              ),
            ),
            Flexible(
              child: Padding(
                padding: const EdgeInsets.all(20.0),
                child: SizedBox(
                  child: Align(
                    alignment: Alignment.bottomCenter,
                    child: ElevatedButton(
                        onPressed: () => Navigator.pop(context),
                        style: ElevatedButton.styleFrom(
                          elevation: 0,
                          backgroundColor: PrimaryColors.darkGreen,
                          padding: const EdgeInsets.symmetric(
                              horizontal: 40, vertical: 3),
                          shape: RoundedRectangleBorder(
                            borderRadius:
                                BorderRadius.circular(10), // Rounded corners
                          ),
                        ),
                        child: Text(
                          "Back To Map",
                          style: GoogleFonts.roboto(
                              fontSize: 16, color: Colors.white),
                        )),
                  ),
                ),
              ),
            )
          ],
        ),
      ),
    );
  }
}
