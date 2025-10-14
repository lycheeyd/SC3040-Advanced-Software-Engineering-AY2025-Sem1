import 'package:calowin/common/custom_scaffold.dart';
import 'package:calowin/common/colors_and_fonts.dart';
import 'package:calowin/common/user_profile.dart';
import 'package:calowin/control/page_navigator.dart';
import 'package:calowin/common/AES_Encryptor.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:flutter/services.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:provider/provider.dart';

class SignupPage2 extends StatefulWidget {
  final String email;
  final String password;
  final String confirmPassword;

  const SignupPage2({
    super.key,
    required this.email,
    required this.password,
    required this.confirmPassword,
  });

  @override
  State<SignupPage2> createState() => _SignupPage2State();
}

class _SignupPage2State extends State<SignupPage2> {
  final _formKey = GlobalKey<FormState>();
  final TextEditingController _inputWeight = TextEditingController();
  final TextEditingController _inputName = TextEditingController();
  late final UserProfile profile;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    profile = Provider.of<UserProfile>(context,listen: false);
  }

  @override
  void dispose() {
    _inputName.dispose();
    _inputWeight.dispose();
    super.dispose();
  }

  void _showLoadingDialog() {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return const Dialog(
          child: Padding(
            padding: EdgeInsets.all(20.0),
            child: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                CircularProgressIndicator(),
                SizedBox(width: 20),
                Text("Creating Account..."),
              ],
            ),
          ),
        );
      },
    );
  }

  Future<void> _handleSignup() async {
    if (_formKey.currentState!.validate()) {
      _showLoadingDialog(); // Show spinner

      final String encryptedPassword = AES_Encryptor.encrypt(widget.password);
      final String encryptedConfirmPassword = AES_Encryptor.encrypt(widget.confirmPassword);
      final String url = "https://sc3040G5-CalowinSpringNode.hf.space/central/account/signup";

      try {
        final response = await http.post(
          Uri.parse(url),
          headers: {"Content-Type": "application/json"},
          body: json.encode({
            "email": widget.email,
            "password": encryptedPassword,
            "confirm_password": encryptedConfirmPassword,
            "name": _inputName.text,
            "weight": double.parse(_inputWeight.text),
          }),
        );

        if (mounted) Navigator.of(context).pop(); // Hide spinner

        final responseMessage = response.body;

        if (response.statusCode == 201) {
          final Map<String, dynamic> responseData = jsonDecode(response.body);
          final loginResponse = UserProfile.fromJson(responseData['UserObject']);
          profile.copyProfile(loginResponse);
          _showSuccessDialog("Signup successful! Welcome to CaloWin!");
        } else {
          _showErrorDialog(responseMessage);
        }
      } catch (e) {
        if (mounted) Navigator.of(context).pop(); // Hide spinner on error
        _showErrorDialog("Network error: ${e.toString()}");
      }
    }
  }

  void _showErrorDialog(String message) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Text("Signup Failed"),
          content: Text(message),
          actions: <Widget>[
            TextButton(
              child: const Text('OK'),
              onPressed: () => Navigator.of(context).pop(),
            ),
          ],
        );
      },
    );
  }

  void _showSuccessDialog(String message) {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) => AlertDialog(
        title: const Text("Welcome!"),
        content: Text(message),
        actions: [
          TextButton(
            onPressed: (){
              FocusScope.of(context).unfocus();
              Navigator.of(context).pushAndRemoveUntil(
                MaterialPageRoute(builder: (context) => PageNavigator(profile: profile)),
                    (Route<dynamic> route) => false,
              );
            },
            child: const Text('Get Started'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return CustomScaffold(
      body: Scaffold(
        body: Container(
          decoration: const BoxDecoration(
            gradient: LinearGradient(
              colors: [PrimaryColors.dullGreen, PrimaryColors.darkGreen],
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
            ),
          ),
          child: SafeArea(
            child: Center(
              child: SingleChildScrollView(
                padding: const EdgeInsets.symmetric(horizontal: 24.0),
                child: Form(
                  key: _formKey,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(
                        "Almost there...",
                        style: GoogleFonts.poppins(
                            color: Colors.white,
                            fontSize: 28,
                            fontWeight: FontWeight.bold
                        ),
                      ),
                      Text(
                        "Step 2 of 2",
                        style: GoogleFonts.poppins(color: Colors.white70, fontSize: 16),
                      ),
                      const SizedBox(height: 40),
                      TextFormField(
                        controller: _inputName,
                        style: const TextStyle(color: Colors.white),
                        decoration: _buildInputDecoration("Name", Icons.person_outline),
                        validator: (value) {
                          if (value == null || value.isEmpty) return "Name is required";
                          if (value.length > 16) return "Name cannot exceed 16 characters";
                          return null;
                        },
                      ),
                      const SizedBox(height: 16),
                      TextFormField(
                        controller: _inputWeight,
                        style: const TextStyle(color: Colors.white),
                        decoration: _buildInputDecoration("Weight (kg)", Icons.monitor_weight_outlined),
                        keyboardType: const TextInputType.numberWithOptions(decimal: true),
                        inputFormatters: [FilteringTextInputFormatter.allow(RegExp(r'^\d+\.?\d{0,1}'))],
                        validator: (value) {
                          if (value == null || value.isEmpty) return "Weight is required";
                          final weightPattern = r'^\d+(\.\d{1})?$';
                          if (!RegExp(weightPattern).hasMatch(value)) return "Enter a valid weight (e.g., 70 or 70.5)";
                          return null;
                        },
                      ),
                      const SizedBox(height: 40),
                      SizedBox(
                        width: double.infinity,
                        child: ElevatedButton(
                          onPressed: _handleSignup,
                          style: ElevatedButton.styleFrom(
                            backgroundColor: Colors.white,
                            foregroundColor: PrimaryColors.darkGreen,
                            padding: const EdgeInsets.symmetric(vertical: 16),
                            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                          ),
                          child: Text("Complete Sign Up", style: GoogleFonts.poppins(fontSize: 16, fontWeight: FontWeight.bold)),
                        ),
                      ),
                      const SizedBox(height: 16),
                      Center(
                        child: TextButton(
                          onPressed: () => Navigator.of(context).pop(),
                          child: Text(
                            "← Go Back",
                            style: GoogleFonts.poppins(color: Colors.white70),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }

  InputDecoration _buildInputDecoration(String label, IconData prefixIcon) {
    return InputDecoration(
      labelText: label,
      labelStyle: const TextStyle(color: Colors.white70),
      prefixIcon: Icon(prefixIcon, color: Colors.white70),
      filled: true,
      fillColor: Colors.white.withOpacity(0.1),
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: BorderSide.none,
      ),
      enabledBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: BorderSide.none,
      ),
      focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: Colors.white, width: 2),
      ),
      errorStyle: const TextStyle(color: Colors.yellowAccent),
    );
  }
}