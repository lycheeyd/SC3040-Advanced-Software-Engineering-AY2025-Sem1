import 'package:calowin/Pages/sign_up/signup_page.dart';
import 'package:calowin/common/ActionType.dart';
import 'package:calowin/common/colors_and_fonts.dart';
import 'package:calowin/common/custom_scaffold.dart';
import 'package:calowin/common/input_dialog.dart';
import 'package:calowin/common/user_profile.dart';
import 'package:calowin/control/page_navigator.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:calowin/common/AES_Encryptor.dart';
import 'package:provider/provider.dart';

class Loginpage extends StatefulWidget {
  const Loginpage({super.key});

  @override
  State<Loginpage> createState() => _LoginpageState();
}

class _LoginpageState extends State<Loginpage> {
  final _formKey = GlobalKey<FormState>();

  final TextEditingController _inputPassword = TextEditingController();
  final TextEditingController _inputEmail = TextEditingController();
  late UserProfile profile;

  bool _isPasswordObscured = true;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    profile = Provider.of<UserProfile>(context);
  }

  @override
  void dispose() {
    _inputEmail.dispose();
    _inputPassword.dispose();
    super.dispose();
  }

  // NEW: A reusable function to show a loading spinner dialog.
  void _showLoadingDialog() {
    showDialog(
      context: context,
      barrierDismissible: false, // User must not close dialog manually
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

  // MODIFIED: _handleLogin now shows and hides the loading dialog.
  Future<void> _handleLogin() async {
    if (_formKey.currentState!.validate()) {
      _showLoadingDialog(); // Show spinner

      final email = _inputEmail.text;
      final password = _inputPassword.text;
      final String encryptedPassword = AES_Encryptor.encrypt(password);
      final String url = "https://sc3040G5-CalowinSpringNode.hf.space/central/account/login";

      try {
        final response = await http.post(
          Uri.parse(url),
          headers: {"Content-Type": "application/json"},
          body: json.encode({"email": email, "password": encryptedPassword}),
        );

        Navigator.of(context).pop(); // Hide spinner

        if (response.statusCode == 200) {
          final Map<String, dynamic> responseData = jsonDecode(response.body);
          final loginResponse = UserProfile.fromJson(responseData['UserObject']);
          profile.copyProfile(loginResponse);
          profile.updateProfile();

          if (mounted) {
            Navigator.of(context).pushReplacement(
              MaterialPageRoute(builder: (context) => PageNavigator(profile: profile)),
            );
          }
        } else {
          _showErrorDialog(response.body);
        }
      } catch (e) {
        if(mounted) Navigator.of(context).pop(); // Hide spinner on error
        _showErrorDialog("Network error: ${e.toString()}");
      }
    }
  }

  // The rest of your login_page.dart file remains the same...
  // (_handleForgetPW, _showErrorDialog, build method, etc.)
  // ...
  Future<void> _handleForgetPW(String emailText) async {
    final email = emailText.trim();
    if (email.isEmpty || !email.contains('@')) {
      _showErrorDialog("Please enter a valid email address.");
      return;
    }

    try {
      final response = await http.post(
        Uri.parse('https://sc3040G5-CalowinSpringNode.hf.space/central/account/send-otp'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(
            {'email': email, 'type': ActionType.FORGOT_PASSWORD.value}),
      );

      final responseMessage = response.body;
      if (response.statusCode == 200) {
        if (mounted) _handleOTPWindow(email);
      } else {
        _showErrorDialog(responseMessage);
      }
    } catch (e) {
      _showErrorDialog('Error: ${e.toString()}');
    }
  }

  void _handleOTPWindow(String email) {
    showDialog(
        context: context,
        builder: (BuildContext context) {
          return InputDialog(
              hintText: "OTP",
              title: "An OTP has been sent to your email",
              content:
              "A new password will be sent to your email after the OTP is verified.",
              onConfirm: (otpCode) {
                Navigator.of(context).pop();
                _handlePwdSending(email, otpCode);
              },
              onCancel: () => Navigator.of(context).pop()
          );
        });
  }

  Future<void> _handlePwdSending(String email, String otpCode) async {
    final String url = "https://sc3040G5-CalowinSpringNode.hf.space/central/account/forgot-password";
    try {
      final response = await http.post(
        Uri.parse(url),
        headers: {"Content-Type": "application/json"},
        body: json.encode({"email": email, "otpCode": otpCode}),
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

  void _handleSignUp() {
    Navigator.push(
        context, MaterialPageRoute(builder: (context) => const SignupPage()));
  }

  void _showErrorDialog(String message) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Text("Login Failed"),
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
                      Image.asset(
                        'assets/images/CalowinNoBackground.png',
                        width: 120,
                        height: 120,
                      ),
                      Text(
                        "CaloWin",
                        style: PrimaryFonts.logoFont.copyWith(color: Colors.white, fontSize: 50),
                      ),
                      Text(
                        "Crush Your Calories, Conquer Your Goals!",
                        style: GoogleFonts.poppins(
                            color: Colors.white70,
                            fontSize: 14,
                            fontWeight: FontWeight.w500),
                      ),
                      const SizedBox(height: 40),
                      TextFormField(
                        controller: _inputEmail,
                        style: const TextStyle(color: Colors.white),
                        decoration: InputDecoration(
                          labelText: "Email",
                          labelStyle: const TextStyle(color: Colors.white70),
                          prefixIcon: const Icon(Icons.email_outlined, color: Colors.white70),
                          filled: true,
                          fillColor: Colors.white.withOpacity(0.1),
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(12),
                            borderSide: BorderSide.none,
                          ),
                        ),
                        validator: (value) {
                          if (value == null || value.isEmpty) return "Email is required";
                          if (!value.contains('@')) return "Enter a valid email";
                          return null;
                        },
                      ),
                      const SizedBox(height: 16),
                      TextFormField(
                        controller: _inputPassword,
                        obscureText: _isPasswordObscured,
                        style: const TextStyle(color: Colors.white),
                        decoration: InputDecoration(
                          labelText: "Password",
                          labelStyle: const TextStyle(color: Colors.white70),
                          prefixIcon: const Icon(Icons.lock_outline, color: Colors.white70),
                          suffixIcon: IconButton(
                            icon: Icon(_isPasswordObscured ? Icons.visibility_off : Icons.visibility, color: Colors.white70),
                            onPressed: () => setState(() => _isPasswordObscured = !_isPasswordObscured),
                          ),
                          filled: true,
                          fillColor: Colors.white.withOpacity(0.1),
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(12),
                            borderSide: BorderSide.none,
                          ),
                        ),
                        validator: (value) {
                          if (value == null || value.isEmpty) return "Password is required";
                          return null;
                        },
                      ),
                      Align(
                        alignment: Alignment.centerRight,
                        child: TextButton(
                          onPressed: () {
                            showDialog(
                                context: context,
                                builder: (BuildContext context) {
                                  return InputDialog(
                                      hintText: "Email",
                                      title: "Forgot Password",
                                      content: "Please enter your email to receive an OTP.",
                                      onConfirm: (emailText) {
                                        Navigator.of(context).pop();
                                        _handleForgetPW(emailText);
                                      },
                                      onCancel: () => Navigator.of(context).pop());
                                });
                          },
                          child: Text(
                            "Forgot password?",
                            style: GoogleFonts.poppins(color: Colors.white70),
                          ),
                        ),
                      ),
                      const SizedBox(height: 20),
                      SizedBox(
                        width: double.infinity,
                        child: ElevatedButton(
                          onPressed: _handleLogin,
                          style: ElevatedButton.styleFrom(
                            backgroundColor: Colors.white,
                            foregroundColor: PrimaryColors.darkGreen,
                            padding: const EdgeInsets.symmetric(vertical: 16),
                            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                          ),
                          child: Text("Sign In", style: GoogleFonts.poppins(fontSize: 16, fontWeight: FontWeight.bold)),
                        ),
                      ),
                      const SizedBox(height: 20),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Text("Not a member yet?", style: GoogleFonts.poppins(color: Colors.white70)),
                          TextButton(
                            onPressed: _handleSignUp,
                            child: Text(
                              "Sign Up",
                              style: GoogleFonts.poppins(color: Colors.white, fontWeight: FontWeight.bold),
                            ),
                          )
                        ],
                      )
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
}