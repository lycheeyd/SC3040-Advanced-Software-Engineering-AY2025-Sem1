import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class PrimaryColors {
  static const dullGreen = Color.fromARGB(255, 119, 181, 119);
  static const brightGreen = Color.fromARGB(255, 58, 229, 58);
  static const darkGreen = Color.fromARGB(255, 48, 93, 48);
  static const orange = Color.fromARGB(255, 252, 173, 97);
  static const black = Color.fromARGB(255, 0, 0, 0);
  static final grey = Colors.grey[400];
}

class PrimaryFonts {
  static final logoFont = GoogleFonts.rammettoOne(
      textStyle: const TextStyle(color: PrimaryColors.black, fontSize: 50));
  static final systemFont = GoogleFonts.roboto(color: PrimaryColors.grey);
}
