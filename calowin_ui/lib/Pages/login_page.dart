

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
  final TextEditingController _inputPassword = TextEditingController();
  final TextEditingController _inputEmail = TextEditingController();
  UserProfile profile = UserProfile(name: "NA", userID: "NA");

  bool _wrongPassword = false;
  bool _invalidEmail = false;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    profile = Provider.of<UserProfile>(context);
  }


  Future<void> _handleLogin() async {
    setState(() {
      _wrongPassword = false;
      _invalidEmail = false;
    });

    final email = _inputEmail.text;
    final password = _inputPassword.text;

    if (email.isEmpty || !email.contains('@')) {
      setState(() => _invalidEmail = true);
      return;
    }
    if (password.isEmpty) {
      setState(() => _wrongPassword = true);
      return;
    }

    final String encryptedPassword = AES_Encryptor.encrypt(password);

    final String url = "http://172.21.146.188:8080/central/account/login";

    try {
      final response = await http.post(
        Uri.parse(url),
        headers: {"Content-Type": "application/json"},
        body: json.encode({"email": email, "password": encryptedPassword}),
      );

      final responseMessage = response.body;

      if (response.statusCode == 200) {
        // Navigate to the next page
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        final loginResponse = UserProfile.fromJson(responseData['UserObject']);
        profile.copyProfile(loginResponse);
        profile.updateProfile();
        //print(loginResponse.getEmail());
        if(mounted)
        {
          Navigator.of(context).push(
          PageRouteBuilder(
            pageBuilder: (context, animation, secondaryAnimation) =>
                PageNavigator(profile: profile),
            transitionsBuilder: (context, animation, secondaryAnimation, child) {
              return child; // No custom transition
            },
            settings: const RouteSettings(arguments: 'disableSwipe'),
          ),
        );
        }
        else{ print("Login response is null");}
      } else {
        _showErrorDialog(responseMessage);
        setState(() {
          _wrongPassword = false;
          _invalidEmail = false;
          _inputPassword.clear();
          _inputEmail.clear();
          }
        );
      }
    } catch (e) {
      _showErrorDialog("Network error: ${e.toString()}");
    }
  }

  Future<void> _handleForgetPW(String emailText) async{

    final email = emailText.trim();
  
    if (email.isEmpty || !email.contains('@')) {
      Align(
        alignment: Alignment.centerLeft,
        child: Padding(
          padding: const EdgeInsets.only(left: 20.0), // Adjust padding as needed
          child: Text(
            "Invalid email format.",
            style: GoogleFonts.roboto(
              fontSize: 11,
              color: Colors.redAccent.shade400,
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
      );
      return;
    }

    try {
      final response = await http.post(
        Uri.parse('http://172.21.146.188:8080/central/account/send-otp'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'email': email, 'type': ActionType.FORGOT_PASSWORD.value}),
      );

      final responseMessage = response.body;

      if (response.statusCode == 200) {
        _handleOTPWindow(email);
      } else {
        _showErrorDialog(responseMessage);
      }
    } catch (e) {
      setState(() {
        _showErrorDialog('Error: ${e.toString()}');
      });
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
                  "A new password would be sent to your email after the OTP is verified",
              onConfirm: (otpCode) {
                _handlePwdSending(email, otpCode);
                Navigator.of(context).pop();
              },
              onCancel: () {
                Navigator.of(context).pop();
              });
        });
  }

  Future<void> _handlePwdSending(String email, String otpCode) async {
    //verifying otp and send email
    final String url = "http://172.21.146.188:8080/central/account/forgot-password";

    try {
      final response = await http.post(
        Uri.parse(url),
        headers: {"Content-Type": "application/json"},
        body: json.encode({"email": email, "otpCode": otpCode}),
      );

      final responseMessage = response.body;

      if (response.statusCode == 200) {
        // Navigate to the next page
        _showSuccessDialog(responseMessage);

      } else {
        _showErrorDialog(responseMessage);
      }
    } catch (e) {
      _showErrorDialog("Network error: ${e.toString()}");
    }
  }

//hello
  void _handleSignUp() {
    Navigator.push(
        context, MaterialPageRoute(builder: (context) => const SignupPage()));
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
    final inputBorder = OutlineInputBorder(
        borderSide: BorderSide(color: Colors.grey.shade400),
        borderRadius: BorderRadius.circular(8));
    return CustomScaffold(
      body: Scaffold(
        appBar: AppBar(
          backgroundColor: PrimaryColors.dullGreen,
          foregroundColor: PrimaryColors.dullGreen,
        ),
        backgroundColor: PrimaryColors.dullGreen,
        body: SingleChildScrollView(
          child: Stack(
            children: [
              Column(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Center(
                    child: Image.asset(
                      'assets/images/CalowinNoBackground.png',
                      width: 150,
                      height: 150,
                    ),
                  ),
                  Text(
                    "CaloWin",
                    style: PrimaryFonts.logoFont,
                  ),
                  Text(
                    "Crush Your Calories, Conquer Your Goals!",
                    style: GoogleFonts.poppins(
                        color: Colors.white,
                        fontSize: 12,
                        fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(
                    height: 30,
                  ),
                  Container(
                    width: 320,
                    height: 400,
                    decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(8),
                        border:
                            Border.all(color: Colors.grey.shade200, width: 1)),
                    child: Padding(
                      padding: const EdgeInsets.only(
                          top: 15, right: 15, left: 15, bottom: 10),
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          Column(
                            children: [
                              Align(
                                alignment: Alignment.topLeft,
                                child: Text(
                                  "Email",
                                  style: GoogleFonts.roboto(fontSize: 16),
                                  textAlign: TextAlign.left,
                                ),
                              ),
                              const SizedBox(height: 5),
                              SizedBox(
                                height: 40,
                                child: TextField(
                                    controller: _inputEmail,
                                    textAlign: TextAlign.left,
                                    decoration: InputDecoration(
                                      contentPadding:
                                          const EdgeInsets.symmetric(
                                              vertical: 5, horizontal: 15),
                                      hintText: "Enter Your Email",
                                      hintStyle: GoogleFonts.roboto(
                                          fontSize: 16,
                                          color: PrimaryColors.grey),
                                      enabledBorder: inputBorder,
                                      border: inputBorder,
                                      focusedBorder: inputBorder,
                                    )),
                              ),
                              if (_invalidEmail)
                                Align(
                                  alignment: Alignment.topLeft,
                                  child: Text(
                                    "Invalid Email!",
                                    style: GoogleFonts.roboto(
                                        fontSize: 14, color: Colors.red),
                                  ),
                                )
                            ],
                          ),
                          const SizedBox(
                            height: 15,
                          ),
                          Column(
                            children: [
                              Align(
                                alignment: Alignment.topLeft,
                                child: Text(
                                  "Password",
                                  style: GoogleFonts.roboto(fontSize: 16),
                                  textAlign: TextAlign.left,
                                ),
                              ),
                              const SizedBox(height: 5),
                              SizedBox(
                                height: 40,
                                child: TextField(
                                    controller: _inputPassword,
                                    obscureText: true,
                                    textAlign: TextAlign.left,
                                    decoration: InputDecoration(
                                      contentPadding:
                                          const EdgeInsets.symmetric(
                                              vertical: 5, horizontal: 15),
                                      hintText: "Enter Your Password",
                                      hintStyle: GoogleFonts.roboto(
                                          fontSize: 16,
                                          color: PrimaryColors.grey),
                                      enabledBorder: inputBorder,
                                      border: inputBorder,
                                      focusedBorder: inputBorder,
                                    )),
                              ),
                              if (_wrongPassword)
                                Align(
                                  alignment: Alignment.topLeft,
                                  child: Text(
                                    "Wrong password!",
                                    style: GoogleFonts.roboto(
                                        fontSize: 14, color: Colors.red),
                                  ),
                                )
                            ],
                          ),
                          const SizedBox(height: 15),
                          SizedBox(
                            width: 272,
                            height: 45,
                            child: ElevatedButton(
                                onPressed: ()=>_handleLogin(),
                                style: ElevatedButton.styleFrom(
                                  backgroundColor:
                                      const Color.fromARGB(255, 48, 93, 48),
                                  padding: const EdgeInsets.symmetric(
                                      horizontal: 40, vertical: 12),
                                  shape: RoundedRectangleBorder(
                                    borderRadius: BorderRadius.circular(
                                        10), // Rounded corners
                                  ),
                                ),
                                child: Text(
                                  "Sign In",
                                  style: GoogleFonts.roboto(
                                      fontSize: 16, color: Colors.white),
                                )),
                          ),
                          // const SizedBox(
                          //   height: 20,
                          // ),
                          Align(
                            alignment: Alignment.centerLeft,
                            child: TextButton(
                                onPressed: () {
                                  setState(() {
                                    _wrongPassword = false;
                                  });
                                  showDialog(
                                      context: context,
                                      builder: (BuildContext context) {
                                        return InputDialog(
                                            hintText: "Email",
                                            title: "Please key in your email",
                                            content:
                                                "A temporary password would be sent to your email",
                                            onConfirm: (emailText) {
                                              Navigator.of(context).pop();
                                              _handleForgetPW(emailText);
                                            },
                                            onCancel: () {
                                              Navigator.of(context).pop();
                                            });
                                      });
                                },
                                child: Text(
                                  "Forgot password?",
                                  style: GoogleFonts.roboto(
                                      decoration: TextDecoration.underline,
                                      fontSize: 14,
                                      color: Colors.black),
                                )),
                          ),
                          Divider(
                            height: 15,
                            thickness: 1,
                            color: PrimaryColors.grey,
                          ),
                          Row(
                            children: [
                              const Align(
                                  alignment: Alignment.centerLeft,
                                  child: Text("Not a member yet?")),
                              TextButton(
                                  onPressed: _handleSignUp,
                                  child: const Text(
                                    "Sign Up",
                                    style: TextStyle(color: Colors.orange),
                                  ))
                            ],
                          )
                        ],
                      ),
                    ),
                  )
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}
