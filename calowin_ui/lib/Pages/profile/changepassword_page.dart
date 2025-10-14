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
  final _formKey = GlobalKey<FormState>();
  late final String userID;

  final TextEditingController _currentPWController = TextEditingController();
  final TextEditingController _newPWController = TextEditingController();
  final TextEditingController _confirmPWController = TextEditingController();

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

  // NEW: A reusable function to show a loading spinner dialog.
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
                Text("Saving..."),
              ],
            ),
          ),
        );
      },
    );
  }

  // MODIFIED: This function now shows and hides the loading spinner.
  Future<void> _handleChangePassword() async {
    if (_formKey.currentState!.validate()) {
      _showLoadingDialog(); // Show spinner

      final String encryptedOldPassword = AES_Encryptor.encrypt(_currentPWController.text);
      final String encryptedNewPassword = AES_Encryptor.encrypt(_newPWController.text);
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

        if (mounted) Navigator.of(context).pop(); // Hide spinner

        final responseMessage = response.body;
        if (response.statusCode == 200) {
          _showSuccessDialog(responseMessage);
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