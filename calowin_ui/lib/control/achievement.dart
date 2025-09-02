class Achievement {
  int totalCarbonSavedExp;
  int totalCalorieBurntExp;
  String carbonSavedMedal;
  String calorieBurntMedal;

  Achievement({
    required this.totalCarbonSavedExp,
    required this.totalCalorieBurntExp,
    required this.carbonSavedMedal,
    required this.calorieBurntMedal,
  });

  // Factory method to create an Achievement instance from JSON
  factory Achievement.fromJson(Map<String, dynamic> json) {
    return Achievement(
      totalCarbonSavedExp: json['totalCarbonSavedExp'],
      totalCalorieBurntExp: json['totalCalorieBurntExp'],
      carbonSavedMedal: json['carbonSavedMedal'],
      calorieBurntMedal: json['calorieBurntMedal'],
    );
  }
}
