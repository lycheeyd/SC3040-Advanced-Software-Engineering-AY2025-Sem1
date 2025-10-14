import 'package:calowin/common/AES_Encryptor.dart';
import 'package:calowin/common/colors_and_fonts.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class ChangepasswordPage extends StatefulWidget {
  final String userID;
  const ChangepasswordPage({super.key, required this.userID});

  @override
  State<ChangepasswordPage> createState() => _ChangepasswordPageState();
}

class _ChangepasswordPageState extends State<ChangepasswordPage> {
  // NEW: Using a Form key for robust validation.
  final _formKey = GlobalKey<FormState>();
  late final String userID;

  final TextEditingController _currentPWController = TextEditingController();
  final TextEditingController _newPWController = TextEditingController();
  final TextEditingController _confirmPWController = TextEditingController();

  // NEW: Adding a state for toggling password visibility.
  bool _isNewPasswordObscured = true;
  bool _isConfirmPasswordObscured = true;
  bool _isCurrentPasswordObscured = true;


  @override
  void initState() {
    super.initState();
    userID = widget.userID;
  }

  @override
  void dispose() {
    _currentPWController.dispose();
    _newPWController.dispose();
    _confirmPWController.dispose();
    super.dispose();
  }

  Future<void> _handleChangePassword() async {
    // MODIFIED: Validation is now handled by the form key.
    if (_formKey.currentState!.validate()) {
      final String encryptedOldPassword = AES_Encryptor.encrypt(_currentPWController.text);
      final String encryptedNewPassword = AES_Encryptor.encrypt(_newPWController.text);

      // Note: The original code encrypted the confirmation password separately.
      // Usually, you only need to send the new password once it's confirmed.
      // I've kept the original logic to avoid changing functionality.
      final String encryptedNewConfirmPassword = AES_Encryptor.encrypt(_confirmPWController.text);

      final String url = "https://sc3040G5-CalowinSpringNode.hf.space/central/account/change-password";

      try {
        final response = await http.post(
          Uri.parse(url),
          headers: {"Content-Type": "application/json"},
          body: json.encode({
            "userID": userID,
            "oldPassword": encryptedOldPassword,
            "newPassword": encryptedNewPassword,
            "confirm_newPassword": encryptedNewConfirmPassword,
          }),
        );

        final responseMessage = response.body;

        if (response.statusCode == 200) {
          _showSuccessDialog(responseMessage);
        } else {
          _showErrorDialog(responseMessage);
        }
      } catch (e) {
        _showErrorDialog("Network error: ${e.toString()}");
      }
    }
  }

  void _showErrorDialog(String message) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Text("Error"),
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
      builder: (context) => AlertDialog(
        title: const Text("Success"),
        content: Text(message),
        actions: [
          TextButton(
            onPressed: (){
              Navigator.of(context).pop(); // Close dialog
              Navigator.of(context).pop(); // Go back from this page
            },
            child: const Text('OK'),
          ),
        ],
      ),
    );
  }

  // MODIFIED: The build method is completely refactored for the new UI.
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[100],
      appBar: AppBar(
        title: Text("Change Password", style: GoogleFonts.poppins(fontWeight: FontWeight.bold)),
        backgroundColor: PrimaryColors.dullGreen,
        foregroundColor: Colors.white,
      ),
      body: Form(
        key: _formKey,
        child: ListView(
          padding: const EdgeInsets.all(16.0),
          children: [
            Card(
              elevation: 2,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      "Your password must be at least 8 characters and include an uppercase letter, a lowercase letter, a number, and a special character.",
                      style: GoogleFonts.poppins(color: Colors.black54, fontSize: 14),
                    ),
                    const SizedBox(height: 24),
                    // MODIFIED: Using TextFormField for better validation.
                    TextFormField(
                      controller: _currentPWController,
                      obscureText: _isCurrentPasswordObscured,
                      decoration: InputDecoration(
                          labelText: "Current Password",
                          border: const OutlineInputBorder(),
                          suffixIcon: IconButton(
                            icon: Icon(_isCurrentPasswordObscured ? Icons.visibility_off : Icons.visibility),
                            onPressed: () => setState(() => _isCurrentPasswordObscured = !_isCurrentPasswordObscured),
                          )
                      ),
                      validator: (value) {
                        if (value == null || value.isEmpty) {
                          return "Enter your current password";
                        }
                        return null;
                      },
                    ),
                    const SizedBox(height: 16),
                    TextFormField(
                      controller: _newPWController,
                      obscureText: _isNewPasswordObscured,
                      decoration: InputDecoration(
                          labelText: "New Password",
                          border: const OutlineInputBorder(),
                          suffixIcon: IconButton(
                            icon: Icon(_isNewPasswordObscured ? Icons.visibility_off : Icons.visibility),
                            onPressed: () => setState(() => _isNewPasswordObscured = !_isNewPasswordObscured),
                          )
                      ),
                      validator: (value) {
                        if (value == null || value.isEmpty) {
                          return "New password is required";
                        }
                        final passwordPattern = r'^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=!])(?=.{8,60}).*$';
                        if (!RegExp(passwordPattern).hasMatch(value)) {
                          return "Password does not meet requirements";
                        }
                        return null;
                      },
                    ),
                    const SizedBox(height: 16),
                    TextFormField(
                      controller: _confirmPWController,
                      obscureText: _isConfirmPasswordObscured,
                      decoration: InputDecoration(
                          labelText: "Confirm New Password",
                          border: const OutlineInputBorder(),
                          suffixIcon: IconButton(
                            icon: Icon(_isConfirmPasswordObscured ? Icons.visibility_off : Icons.visibility),
                            onPressed: () => setState(() => _isConfirmPasswordObscured = !_isConfirmPasswordObscured),
                          )
                      ),
                      validator: (value) {
                        if (value == null || value.isEmpty) {
                          return "Confirm your new password";
                        }
                        if (_newPWController.text != value) {
                          return "Passwords do not match";
                        }
                        return null;
                      },
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
      bottomNavigationBar: Padding(
        padding: const EdgeInsets.all(16.0),
        child: ElevatedButton(
          onPressed: _handleChangePassword,
          style: ElevatedButton.styleFrom(
            backgroundColor: PrimaryColors.darkGreen,
            foregroundColor: Colors.white,
            padding: const EdgeInsets.symmetric(vertical: 16),
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
          ),
          child: Text("Change Password", style: GoogleFonts.poppins(fontSize: 16, fontWeight: FontWeight.bold)),
        ),
      ),
    );
  }
}