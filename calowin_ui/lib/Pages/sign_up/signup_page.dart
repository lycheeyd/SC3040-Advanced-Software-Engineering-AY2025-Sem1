import 'dart:async';
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:http/http.dart' as http;
import 'package:calowin/common/custom_scaffold.dart';
import 'package:calowin/Pages/sign_up/signup_page2.dart';
import 'package:calowin/common/colors_and_fonts.dart';
import 'package:calowin/common/ActionType.dart';

class SignupPage extends StatefulWidget {
  const SignupPage({super.key});

  @override
  State<SignupPage> createState() => _SignupPageState();
}

class _SignupPageState extends State<SignupPage> {
  final _formKey = GlobalKey<FormState>();
  final TextEditingController _inputPassword = TextEditingController();
  final TextEditingController _inputConfirmPassword = TextEditingController();
  final TextEditingController _inputEmail = TextEditingController();
  final TextEditingController _inputOTP = TextEditingController();

  bool _isOTPRequested = false;
  int _otpCountdown = 0;
  Timer? _otpTimer;

  bool _isPasswordObscured = true;
  bool _isConfirmPasswordObscured = true;

  @override
  void dispose() {
    _otpTimer?.cancel();
    _inputEmail.dispose();
    _inputPassword.dispose();
    _inputConfirmPassword.dispose();
    _inputOTP.dispose();
    super.dispose();
  }

  void _startOTPTimer() {
    const oneMinute = 60;
    setState(() {
      _otpCountdown = oneMinute;
    });

    _otpTimer = Timer.periodic(const Duration(seconds: 1), (timer) {
      if (mounted) {
        setState(() {
          if (_otpCountdown > 0) {
            _otpCountdown--;
          } else {
            _otpTimer?.cancel();
          }
        });
      } else {
        _otpTimer?.cancel();
      }
    });
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
                Text("Loading..."),
              ],
            ),
          ),
        );
      },
    );
  }

  Future<void> _sendOTP() async {
    // A trick to only validate the email field before sending.
    if (!_formKey.currentState!.validate()) {
      _formKey.currentState!.save();
      _formKey.currentState!.validate();
      return;
    }
    if (_otpCountdown > 0) return;

    _showLoadingDialog(); // Show spinner

    final email = _inputEmail.text;
    try {
      final response = await http.post(
        Uri.parse('https://sc3040G5-CalowinSpringNode.hf.space/central/account/send-otp'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'email': email, 'type': ActionType.SIGN_UP.value}),
      );

      if(mounted) Navigator.of(context).pop(); // Hide spinner

      final responseMessage = response.body;
      if (response.statusCode == 200) {
        setState(() {
          _isOTPRequested = true;
        });
        _showSuccessDialog(responseMessage);
        _startOTPTimer();
      } else {
        _showErrorDialog(responseMessage);
      }
    } catch (e) {
      if(mounted) Navigator.of(context).pop(); // Hide spinner on error
      _showErrorDialog('Error: ${e.toString()}');
    }
  }

  Future<void> _verifyAndContinue() async {
    if (_formKey.currentState!.validate()) {
      if (!_isOTPRequested) {
        _showErrorDialog("Please request and enter an OTP first.");
        return;
      }

      _showLoadingDialog(); // Show spinner

      final otp = _inputOTP.text;
      final email = _inputEmail.text;
      try {
        final response = await http.post(
          Uri.parse('https://sc3040G5-CalowinSpringNode.hf.space/central/account/verify-otp'),
          headers: {'Content-Type': 'application/json'},
          body: jsonEncode({'email': email, 'otpCode': otp, 'type': ActionType.SIGN_UP.value}),
        );

        if(mounted) Navigator.of(context).pop(); // Hide spinner

        if (response.statusCode == 200) {
          _handleContinue();
        } else {
          _showErrorDialog(response.body);
        }
      } catch (e) {
        if(mounted) Navigator.of(context).pop(); // Hide spinner on error
        _showErrorDialog('Error validating OTP. Please try again later.');
      }
    }
  }

  void _handleContinue() {
    FocusScope.of(context).unfocus();
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => SignupPage2(
          email: _inputEmail.text,
          password: _inputPassword.text,
          confirmPassword: _inputConfirmPassword.text,
        ),
      ),
    );
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
        title: const Text("OTP Sent"),
        content: Text(message),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('OK'),
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
                        "Create Account",
                        style: GoogleFonts.poppins(
                            color: Colors.white,
                            fontSize: 28,
                            fontWeight: FontWeight.bold
                        ),
                      ),
                      Text(
                        "Step 1 of 2",
                        style: GoogleFonts.poppins(color: Colors.white70, fontSize: 16),
                      ),
                      const SizedBox(height: 30),
                      TextFormField(
                        controller: _inputEmail,
                        style: const TextStyle(color: Colors.white),
                        decoration: _buildInputDecoration("Email", Icons.email_outlined),
                        validator: (value) {
                          if (value == null || value.isEmpty) return "Email is required";
                          final emailPattern = r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$';
                          if (!RegExp(emailPattern).hasMatch(value)) return "Enter a valid email address";
                          return null;
                        },
                      ),
                      const SizedBox(height: 16),
                      TextFormField(
                        controller: _inputPassword,
                        obscureText: _isPasswordObscured,
                        style: const TextStyle(color: Colors.white),
                        decoration: _buildInputDecoration(
                            "Password",
                            Icons.lock_outline,
                            suffixIcon: IconButton(
                              icon: Icon(_isPasswordObscured ? Icons.visibility_off : Icons.visibility, color: Colors.white70),
                              onPressed: () => setState(() => _isPasswordObscured = !_isPasswordObscured),
                            )
                        ),
                        validator: (value) {
                          if (value == null || value.isEmpty) return "Password is required";
                          final passwordPattern = r'^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=!])(?=.{8,60}).*$';
                          if (!RegExp(passwordPattern).hasMatch(value)) return "Password must be 8-60 chars & contain uppercase, lowercase, number, and special char.";
                          return null;
                        },
                      ),
                      const SizedBox(height: 16),
                      TextFormField(
                        controller: _inputConfirmPassword,
                        obscureText: _isConfirmPasswordObscured,
                        style: const TextStyle(color: Colors.white),
                        decoration: _buildInputDecoration(
                            "Confirm Password",
                            Icons.lock_outline,
                            suffixIcon: IconButton(
                              icon: Icon(_isConfirmPasswordObscured ? Icons.visibility_off : Icons.visibility, color: Colors.white70),
                              onPressed: () => setState(() => _isConfirmPasswordObscured = !_isConfirmPasswordObscured),
                            )
                        ),
                        validator: (value) {
                          if (value == null || value.isEmpty) return "Please confirm your password";
                          if (_inputPassword.text != value) return "Passwords do not match";
                          return null;
                        },
                      ),
                      const SizedBox(height: 24),
                      Row(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Expanded(
                            child: TextFormField(
                              controller: _inputOTP,
                              style: const TextStyle(color: Colors.white),
                              keyboardType: TextInputType.number,
                              inputFormatters: [FilteringTextInputFormatter.digitsOnly],
                              decoration: _buildInputDecoration("Enter OTP", Icons.pin_outlined),
                              validator: (value) {
                                if (_isOTPRequested && (value == null || value.isEmpty)) return "OTP is required";
                                return null;
                              },
                            ),
                          ),
                          const SizedBox(width: 12),
                          SizedBox(
                            height: 58,
                            child: ElevatedButton(
                              onPressed: _otpCountdown > 0 ? null : _sendOTP,
                              style: ElevatedButton.styleFrom(
                                backgroundColor: _otpCountdown > 0 ? Colors.grey : Colors.blueAccent,
                                foregroundColor: Colors.white,
                                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                              ),
                              child: Text(_otpCountdown > 0 ? 'Wait $_otpCountdown s' : "Send OTP"),
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 30),
                      SizedBox(
                        width: double.infinity,
                        child: ElevatedButton(
                          onPressed: _verifyAndContinue,
                          style: ElevatedButton.styleFrom(
                            backgroundColor: Colors.white,
                            foregroundColor: PrimaryColors.darkGreen,
                            padding: const EdgeInsets.symmetric(vertical: 16),
                            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                          ),
                          child: Text("Continue", style: GoogleFonts.poppins(fontSize: 16, fontWeight: FontWeight.bold)),
                        ),
                      ),
                      const SizedBox(height: 16),
                      Center(
                        child: TextButton(
                          onPressed: () => Navigator.of(context).pop(),
                          child: Text(
                            "Already have an account? Sign In",
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

  InputDecoration _buildInputDecoration(String label, IconData prefixIcon, {Widget? suffixIcon}) {
    return InputDecoration(
      labelText: label,
      labelStyle: const TextStyle(color: Colors.white70),
      prefixIcon: Icon(prefixIcon, color: Colors.white70),
      suffixIcon: suffixIcon,
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