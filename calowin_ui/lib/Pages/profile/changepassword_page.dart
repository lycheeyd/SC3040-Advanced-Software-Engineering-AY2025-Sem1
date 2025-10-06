import 'package:calowin/common/AES_Encryptor.dart';
import 'package:calowin/common/colors_and_fonts.dart';
import 'package:calowin/common/custom_scaffold.dart';
import 'package:calowin/common/input_field.dart';
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
  late final String userID;

  bool _wrongPW = false;
  bool _wrongNewPW = false;
  bool _newPWNotSame = false;

  final TextEditingController _currentPWController = TextEditingController();
  final TextEditingController _newPWController = TextEditingController();
  final TextEditingController _confirmPWController = TextEditingController();

  String? _currentPasswordError;
  String? _passwordError;
  String? _confirmPasswordError;

  @override
  void initState() {
    super.initState();
    userID = widget.userID;
  }

  void _checkCurrentPassword() {     
    setState(() {
      if (_currentPWController.text.isEmpty) {
        _currentPasswordError = "Enter current password";
      } else {
        _currentPasswordError = null;
      }
    });
  }

  void _checkPasswordMatch() {     
    setState(() {
      if (_confirmPWController.text.isEmpty) {
        _confirmPasswordError = "Confirm your new password";
      } else if (_newPWController.text != _confirmPWController.text) {
        _confirmPasswordError = "Passwords do not match";
      } else {
        _confirmPasswordError = null;
      }
    });
  }

  void _checkPasswordValid() {
    final passwordPattern = r'^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=!])(?=.{8,60}).*$';    
    setState(() {
      if (_newPWController.text.isEmpty) {
        _passwordError = "New password is required";
      } else if (_newPWController.text.length > 60) {
        _passwordError = "Password cannot exceed 60 characters";
      } else if (!RegExp(passwordPattern).hasMatch(_newPWController.text)) {
        _passwordError =
            "Password must be at least 8 characters long and contain at least 1 digit, 1 uppercase, 1 lowercase, 1 special character.";
      } else {
        _passwordError = null;
      }
    });
  }

  Future<void> _handleChangePassword() async {
    // Handle change password logic
    _checkPasswordValid();
    _checkPasswordMatch();
    _checkCurrentPassword();

    if (_confirmPasswordError == null &&
        _currentPasswordError == null &&
        _passwordError == null) {
      
      final String encryptedOldPassword = AES_Encryptor.encrypt(_currentPWController.text);
      final String encryptedNewPassword = AES_Encryptor.encrypt(_newPWController.text);
      final String encryptedNewConfirmPassword = AES_Encryptor.encrypt(_confirmPWController.text);

      final String url = "https://sc3040G5-CalowinSpringNode.hf.space/central/account/change-password";

      try {
        print("called");
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
          // Signup successful
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
          title: Text(message),
          //content: Text(message),
          actions: <Widget>[
            TextButton(
              child: const Text('OK'),
              onPressed: () {
                Navigator.of(context).pop();
              },
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
        title: Text(message),
        //content: Text(message),
        actions: [
          TextButton(
            onPressed: (){Navigator.pop(context);Navigator.pop(context);},
            child: const Text('OK'),
          ),
        ],
      ),
    );
    if(mounted)
    {
      Navigator.pop(context);
      Navigator.pop(context);
    }
  }

  @override
  Widget build(BuildContext context) {
    return CustomScaffold(
      body: Scaffold(
        backgroundColor: PrimaryColors.dullGreen,
        appBar: AppBar(
          backgroundColor: PrimaryColors.dullGreen,
        ),
        resizeToAvoidBottomInset:
            false, // Prevent resizing when keyboard pops up
        body: Padding(
          padding: const EdgeInsets.symmetric(vertical: 10, horizontal: 20),
          child: Column(
            children: [
              Expanded(
                child: SingleChildScrollView(
                  child: Column(
                    children: [
                      Align(
                        alignment: Alignment.topLeft,
                        child: Text(
                          "Change Password",
                          style: GoogleFonts.poppins(
                              color: Colors.white,
                              fontWeight: FontWeight.bold,
                              fontSize: 30),
                        ),
                      ),
                      const SizedBox(height: 10),
                      Align(
                        alignment: Alignment.topLeft,
                        child: Text(
                          "Your password must:\n  * Be at least 8 characters long\n  * Contain at least 1 special character\n  * Contain at least 1 upper case letter",
                          style: GoogleFonts.poppins(
                              fontSize: 15, color: Colors.white),
                        ),
                      ),
                      const SizedBox(height: 20),

                      InputField(
                        obscureText: true,
                        inputController: _currentPWController,
                        title: "Current Password",
                        inputHint: "Enter Your Current Password",
                        errorText: "Wrong Password!",
                        hasError: _wrongPW,
                      ),
                      if(_currentPasswordError != null)
                        Align(
                          alignment: Alignment.centerLeft,
                          child: Padding(
                            padding: const EdgeInsets.only(left: 20.0), // Adjust padding as needed
                            child: Text(
                              _currentPasswordError!,
                              style: GoogleFonts.roboto(
                                fontSize: 11,
                                color: Colors.redAccent.shade400,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ),
                      ),
                      const SizedBox(height: 10),

                      InputField(
                        obscureText: true,
                        inputController: _newPWController,
                        title: "New Password",
                        inputHint: "Enter Your New Password",
                        errorText: "Invalid Password!",
                        hasError: _wrongNewPW,
                      ),
                      if(_passwordError != null)
                        Align(
                          alignment: Alignment.centerLeft,
                          child: Padding(
                            padding: const EdgeInsets.only(left: 20.0), // Adjust padding as needed
                            child: Text(
                              _passwordError!,
                              style: GoogleFonts.roboto(
                                fontSize: 11,
                                color: Colors.redAccent.shade400,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ),
                      ),
                      const SizedBox(height: 10),

                      InputField(
                        obscureText: true,
                        inputController: _confirmPWController,
                        title: "Confirm Password",
                        inputHint: "Re-enter New Password",
                        errorText:
                            "Please make sure you entered the same as above!",
                        hasError: _newPWNotSame,
                      ),
                      if(_confirmPasswordError != null)
                        Align(
                          alignment: Alignment.centerLeft,
                          child: Padding(
                            padding: const EdgeInsets.only(left: 20.0), // Adjust padding as needed
                            child: Text(
                              _confirmPasswordError!,
                              style: GoogleFonts.roboto(
                                fontSize: 11,
                                color: Colors.redAccent.shade400,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ),
                      ),
                      const SizedBox(height: 10),
                    ],
                  ),
                ),
              ),

              SizedBox(
                width: double.infinity,
                height: 45,
                child: ElevatedButton(
                  onPressed: _handleChangePassword,
                  style: ElevatedButton.styleFrom(
                    elevation: 0,
                    backgroundColor: PrimaryColors.darkGreen,
                    padding: const EdgeInsets.symmetric(
                        horizontal: 40, vertical: 12),
                    shape: RoundedRectangleBorder(
                      borderRadius:
                          BorderRadius.circular(10), // Rounded corners
                    ),
                  ),
                  child: Text(
                    "Change Password",
                    style:
                        GoogleFonts.roboto(fontSize: 16, color: Colors.white),
                  ),
                ),
              ),
              const SizedBox(
                height: 40,
              )
            ],
          ),
        ),
      ),
    );
  }
}
