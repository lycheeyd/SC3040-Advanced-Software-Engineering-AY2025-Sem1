import 'package:flutter/widgets.dart';
import 'package:calowin/common/medals.dart';

class Words2widgetConverter {
  static Image? convert(String medalName) {
    switch (medalName) {
      case "CaloriePlatinum":
        return Medals.caloriePlatinum;
      case "CalorieGold":
        return Medals.calorieGold;
      case "CalorieSilver":
        return Medals.calorieSilver;
      case "CalorieBronze":
        return Medals.calorieBronze;
      case "EcoPlatinum":
        return Medals.ecoPlatinum;
      case "EcoGold":
        return Medals.ecoGold;
      case "EcoSilver":
        return Medals.ecoSilver;
      case "EcoBronze":
        return Medals.ecoBronze;
      default:
        return null; // Return null if the medalName does not match any case
    }
  }
}
