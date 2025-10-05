import 'dart:async';
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:http/http.dart' as http;
import 'package:calowin/common/custom_scaffold.dart';
import 'package:calowin/common/input_field.dart';
import 'package:calowin/Pages/sign_up/signup_page2.dart';
import 'package:calowin/common/colors_and_fonts.dart';
import 'package:calowin/common/ActionType.dart';

class SignupPage extends StatefulWidget {
  const SignupPage({super.key});

  @override
  State<SignupPage> createState() => _SignupPageState();
}

class _SignupPageState extends State<SignupPage> {
  final TextEditingController _inputPassword = TextEditingController();
  final TextEditingController _inputConfirmPassword = TextEditingController();
  final TextEditingController _inputEmail = TextEditingController();
  final TextEditingController _inputOTP = TextEditingController();

  String? _emailError;
  String? _passwordError;
  String? _confirmPasswordError;
  String? _otpError;
  bool _isOTPRequested = false;

  int _otpCountdown = 0;
  Timer? _otpTimer;

  final InputBorder inputBorder = UnderlineInputBorder(
      borderRadius: BorderRadius.circular(8), borderSide: BorderSide.none);

  @override
  void dispose() {
    _otpTimer?.cancel();
    super.dispose();
  }

  void _checkEmail() {
    final emailPattern = r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$';
    setState(() {
      if (_inputEmail.text.isEmpty) {
        _emailError = "Email is required";
      } else if (_inputEmail.text.length > 100) {
        _emailError = "Email cannot exceed 100 characters";
      } else if (!RegExp(emailPattern).hasMatch(_inputEmail.text)) {
        _emailError = "Enter a valid email address";
      } else {
        _emailError = null;
      }
    });
  }

  void _checkPasswordMatch() {     
    setState(() {
      if (_inputConfirmPassword.text.isEmpty) {
        _confirmPasswordError = "Confirm your password";
      } else if (_inputPassword.text != _inputConfirmPassword.text) {
        _confirmPasswordError = "Passwords do not match";
      } else {
        _confirmPasswordError = null;
      }
    });
  }

  void _checkPasswordValid() {
    final passwordPattern = r'^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=!])(?=.{8,60}).*$';        
    setState(() {
      if (_inputPassword.text.isEmpty) {
        _passwordError = "Password is required";
      } else if (_inputPassword.text.length > 60) {
        _passwordError = "Password cannot exceed 60 characters";
      } else if (!RegExp(passwordPattern).hasMatch(_inputPassword.text)) {
        _passwordError =
            "Password must be at least 8 characters long and contain at least 1 digit, 1 uppercase, 1 lowercase, 1 special character.";
      } else {
        _passwordError = null;
      }
    });
  }

  void _startOTPTimer() {
    const oneMinute = 60;
    setState(() {
      _otpCountdown = oneMinute;
    });

    _otpTimer = Timer.periodic(const Duration(seconds: 1), (timer) {
      setState(() {
        if (_otpCountdown > 0) {
          _otpCountdown--;
        } else {
          _otpTimer?.cancel();
        }
      });
    });
  }

  Future<void> _sendOTP() async {
    _checkEmail(); // Validate email before sending OTP
    
    if (_emailError  != null ) {
      _showErrorDialog(_emailError!);
      return;
    }

    if (_otpCountdown > 0) return;

    final email = _inputEmail.text;

    try {

      final response = await http.post(
        Uri.parse('https://sc3040G5-CalowinSpringNode.hf.space/central/account/send-otp'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'email': email, 'type': ActionType.SIGN_UP.value}),
      );

      final responseMessage = response.body;

      if (response.statusCode == 200) {
        setState(() {
          _isOTPRequested = true;
          _otpError = null;
        });
        _showSuccessDialog(responseMessage);
        _startOTPTimer();
      } else {
        _showErrorDialog(responseMessage);
      }
    } catch (e) {
      setState(() {
        _showErrorDialog('Error: ${e.toString()}');
      });
    }
  }

  Future<void> _verifyOTP() async {
    setState(() {
      _checkEmail();
      _checkPasswordMatch();
      _checkPasswordValid();
    });


    final otp = _inputOTP.text;
    final email = _inputEmail.text;

    if (otp.isEmpty) {
      setState(() {
        _otpError = "OTP is required";
      });
      return;
    }

    try {
      final response = await http.post(
        Uri.parse('https://sc3040G5-CalowinSpringNode.hf.space/central/account/verify-otp'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'email': email, 'otpCode': otp, 'type': ActionType.SIGN_UP.value}),
      );

      final responseMessage = response.body;

      if (response.statusCode == 200) {
        setState(() {
          _otpError = null;
        });
        _handleContinue();
      } else {
        _showErrorDialog(responseMessage);
      }
    } catch (e) {
      setState(() {
        _showErrorDialog('Error validating OTP. Please try again later.');
      });
    }
  }

  void _handleContinue() {
    _checkEmail();
    _checkPasswordMatch();
    _checkPasswordValid();

    if (_emailError == null &&
        _passwordError == null &&
        _confirmPasswordError == null &&
        _otpError == null &&
        _isOTPRequested) {
          FocusScope.of(context).unfocus();
      Navigator.pushReplacement(
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
      body: CustomScaffold(
        body: Scaffold(
          appBar: PreferredSize(
              preferredSize: const Size.fromHeight(50),
              child: AppBar(backgroundColor: PrimaryColors.dullGreen,)
          ),
          backgroundColor: PrimaryColors.dullGreen,
          body: SingleChildScrollView(
            child: Column(
              children: [
                Center(
                  child: Image.asset('assets/images/CalowinNoBackground.png', width: 100, height: 100),
                ),
                Text(
                  "CaloWin",
                  style: PrimaryFonts.logoFont.copyWith(fontSize: 45),
                ),
                Text(
                  "Crush Your Calories, Conquer Your Goals!",
                  style: GoogleFonts.poppins(
                      color: Colors.white,
                      fontSize: 10,
                      fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 18),

                // Input fields
                InputField(
                  obscureText: false,
                  inputController: _inputEmail,
                  title: "Email Address",
                  inputHint: "Enter Your Email",
                  errorText: '',
                  hasError: false,
                ),
                if(_emailError != null)
                  Align(
                    alignment: Alignment.centerLeft,
                    child: Padding(
                      padding: const EdgeInsets.only(left: 20.0), // Adjust padding as needed
                      child: Text(
                        _emailError!,
                        style: GoogleFonts.roboto(
                          fontSize: 11,
                          color: Colors.redAccent.shade400,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                ),
                const SizedBox(height: 18),

                InputField(
                  obscureText: true,
                  inputController: _inputPassword,
                  title: "Password",
                  inputHint: "Enter Your Password",
                  bottomHint:
                      "Password must be at least 8 characters long, have at least 1 digit, 1 uppercase, 1 lowercase, and 1 special character.",
                  errorText: '',
                  hasError: false,
                ),
                if(_passwordError != null)
                  Align(
                    alignment: Alignment.centerLeft,
                    child: Padding(
                      padding: const EdgeInsets.only(left: 20.0),
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
                const SizedBox(height: 18),

                InputField(
                  obscureText: true,
                  inputController: _inputConfirmPassword,
                  title: "Confirm Password",
                  inputHint: "Re-enter Your Password",
                  errorText: '',
                  hasError: false,
                ),
                if(_confirmPasswordError != null)
                  Align(
                    alignment: Alignment.centerLeft,
                    child: Padding(
                      padding: const EdgeInsets.only(left: 20.0),
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
                const SizedBox(height: 20),

                // OTP Field
                Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  children: [
                    Text(
                      "OTP: ",
                      style: GoogleFonts.poppins(
                          fontSize: 14,
                          color: Colors.white,
                          fontWeight: FontWeight.bold),
                    ),
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        SizedBox(
                          height: 50,
                          width: 200,
                          child: TextField(keyboardType: TextInputType.number, // Set the keyboard type to numbers
                              inputFormatters: <TextInputFormatter>[FilteringTextInputFormatter.digitsOnly // Allow only digits
                              ],
                              controller: _inputOTP,
                              textAlign: TextAlign.left,
                              decoration: InputDecoration(
                                filled: true,
                                fillColor: Colors.white,
                                border: inputBorder,
                                enabledBorder: inputBorder,
                                focusedBorder: inputBorder,
                                contentPadding: const EdgeInsets.symmetric(vertical: 2, horizontal: 15),
                                hintText: "Enter OTP sent to your Email",
                                hintStyle: GoogleFonts.roboto(fontSize: 12, color: PrimaryColors.grey),
                              ),
                            ),
                          ),
                          if(_otpError != null)
                            Align(
                              alignment: Alignment.centerLeft,
                              child: Padding(
                                padding: const EdgeInsets.only(left: 5.0),
                                child: Text(
                                  _otpError!,
                                  style: GoogleFonts.roboto(
                                    fontSize: 11,
                                    color: Colors.redAccent.shade400,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ),
                            ),
                      ],
                    ),
                    SizedBox(
                      width: 70,
                      height: 30,
                      child: ElevatedButton(
                          onPressed: _otpCountdown > 0 ? null : _sendOTP,
                          style: ElevatedButton.styleFrom(
                            backgroundColor: _otpCountdown > 0 ? Colors.grey : Colors.blue,
                            padding: const EdgeInsets.symmetric(horizontal: 2, vertical: 2),
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(10), // Rounded corners
                            ),
                          ),
                          child: Text(
                            _otpCountdown > 0 ? 'Wait $_otpCountdown s' : "Send OTP",
                            style: GoogleFonts.roboto(fontSize: 12, color: Colors.white),
                          ),
                      ),
                    ),
                  ],
                ),


                Padding(
                  padding:
                      const EdgeInsets.symmetric(vertical: 5, horizontal: 20),
                  child: Divider(
                    thickness: 1,
                    color: Colors.grey.shade400,
                  ),
                ),

                Padding(
                  padding:
                      const EdgeInsets.symmetric(vertical: 5, horizontal: 20),
                  child: SizedBox(
                    width: 400,
                    height: 45,
                    child: ElevatedButton(
                        onPressed: _verifyOTP,
                        style: ElevatedButton.styleFrom(
                          backgroundColor: const Color.fromARGB(255, 48, 93, 48),
                          padding: const EdgeInsets.symmetric(horizontal: 40, vertical: 12),
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(10), // Rounded corners
                          ),
                        ),
                        child: Text(
                          "Continue",
                          style: GoogleFonts.roboto(fontSize: 16, color: Colors.white),
                        )),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
