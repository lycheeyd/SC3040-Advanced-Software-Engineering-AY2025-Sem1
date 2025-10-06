import 'package:calowin/Pages/profile/changepassword_page.dart';
import 'package:calowin/common/ActionType.dart';
import 'package:calowin/common/custom_scaffold.dart';
import 'package:calowin/common/input_dialog.dart';
import 'package:calowin/common/user_profile.dart';
import 'package:calowin/control/maxline_inputformatter.dart';
import 'package:flutter/material.dart';
import 'package:calowin/common/colors_and_fonts.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:calowin/common/input_field.dart';
import 'package:flutter/services.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class EditprofilePage extends StatefulWidget {
  final UserProfile profile;
  const EditprofilePage({super.key, required this.profile});
  

  @override
  State<EditprofilePage> createState() => _EditprofilePageState();
}

class _EditprofilePageState extends State<EditprofilePage> {
  late UserProfile _profile;

  @override
  void initState() {
    super.initState();
    _profile = widget.profile.copyProfile(widget.profile);
    _nameController.text = _profile.getName();
    _weightController.text = _profile.getWeight().toString();
    _bioController.text = _profile.getBio();
  }

  //logic to be implemented
  final bool _invalidName = false;
  final bool _invalidWeight = false;

  //preset the inputs here
  final TextEditingController _nameController = TextEditingController();
  final TextEditingController _weightController = TextEditingController();
  final TextEditingController _bioController = TextEditingController();

  String? _nameError;
  String? _weightError;
  String? _bioError;
  

  void _checkName() {
    setState(() {
      if (_nameController.text.isEmpty) {
        _nameError = "Name is required";
      } else if (_nameController.text.length > 16) {
        _nameError = "Name cannot exceed 16 characters";
      } else {
        _nameError = null;
      }
    });
  }

  void _checkWeight() {
    final weightPattern = r'^\d+(\.\d{1})?$';
    setState(() {
      if (_weightController.text.isEmpty) {
        _weightError = "Weight is required";
      } else if (!RegExp(weightPattern).hasMatch(_weightController.text)) {
        _weightError = "Enter weight in kg (e.g., 70 or 70.5)";
      } else {
        _weightError = null;
      }
    });
  }

  void _checkBio() {
    setState(() {
      if (_bioController.text.isEmpty) {
        _bioError = "Bio is required";
      } else if (_bioController.text.length > 100) {
        _bioError = "Bio cannot exceed 100 characters";
      } else {
        _bioError = null;
      }
    });
  }

  Future<void> _handleSaveChanges() async {
    _checkName();
    _checkWeight();
    _checkBio();

    if (_nameError == null &&
        _weightError == null &&
        _bioError == null) {

      final String url = "https://sc3040G5-CalowinSpringNode.hf.space/central/account/edit-profile";

      try {
        final response = await http.post(
          Uri.parse(url),
          headers: {"Content-Type": "application/json"},
          body: json.encode({
            "userID": _profile.getUserID(),
            "name": _profile.getName(),
            "weight": _profile.getWeight(),
            "bio": _profile.getBio(),
          }),
        );

        final responseMessage = response.body;
        final Map<String, dynamic> responseData = jsonDecode(response.body);

        if (response.statusCode == 200) {
          _showSuccessDialog(responseData['message']);
          //print(responseData);
          final responseObject = UserProfile.editfromJson(responseData['UserObject']);
          setState(() {
            _profile.setName(responseObject.getName());
            _profile.setWeight(responseObject.getWeight());
            _profile.setBio(responseObject.getBio());
          });
        } else {
          _showErrorDialog(responseMessage);
        }
      } catch (e) {
        _showErrorDialog("Network error: ${e.toString()}");
      }
    }
  }

  void _handleChangePassword() {
    FocusScope.of(context).unfocus();
    Navigator.push(context,
        MaterialPageRoute(builder: (context) => ChangepasswordPage(userID: _profile.getUserID())));
  }
  
  Future<void> _sendOTP() async {
    try {
      final response = await http.post(
        Uri.parse('https://sc3040G5-CalowinSpringNode.hf.space/central/account/send-otp'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'email': _profile.getEmail() ?? "",
          'type': ActionType.DELETE_ACCOUNT.value,
        }),
      );

      final responseMessage = response.body;

      if (response.statusCode == 200) {
        _showsentSuccessDialog(responseMessage);
      } else {
        _showErrorDialog(responseMessage);
      }
    } catch (e) {
        _showErrorDialog('Error: ${e.toString()}');
    }
  }

  Future<void> _handleDeleteAccount(String otpCode) async {
    
    final String url = "https://sc3040G5-CalowinSpringNode.hf.space/central/account/delete-account";

    try {
      final response = await http.post(
        Uri.parse(url),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'userID': _profile.getUserID(), 
          'email': _profile.getEmail() ?? "", 
          'otpCode': otpCode,
        }),
      );

      final responseMessage = response.body;

      if (response.statusCode == 200) {
        _showDeleteSuccessDialog(responseMessage);
      
      } else {
        _showErrorDialog(responseMessage);
      }
    } catch (e) {
        _showErrorDialog('Error: ${e.toString()}');
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

  void _showsentSuccessDialog(String message) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(message),
        //content: Text(message),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.pop(context);
            }, 
            child: const Text('OK'),
          ),
        ],
      ),
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
            onPressed: () {
              Navigator.pop(context);
              Navigator.pop(context,_profile);
            }, 
            child: const Text('OK'),
          ),
        ],
      ),
    );
  }

  void _showDeleteSuccessDialog(String message) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(message),
        //content: Text(message),
        actions: [
          TextButton(
            onPressed: () {
            Navigator.pop(context); //pop this dialog
            Navigator.pop(context); //pop to profile
            Navigator.pop(context); //pop to login
            }, 
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
        resizeToAvoidBottomInset: false,
        appBar: AppBar(
          backgroundColor: PrimaryColors.dullGreen,
        ),
        backgroundColor: PrimaryColors.dullGreen,
        body: Padding(
          padding: const EdgeInsets.symmetric(vertical: 15, horizontal: 20),
          child: Column(
            children: [
              Align(
                alignment: Alignment.topLeft,
                child: Text(
                  "Edit Profile",
                  style: GoogleFonts.poppins(
                      color: Colors.white,
                      fontWeight: FontWeight.bold,
                      fontSize: 30),
                ),
              ),
              const SizedBox(
                height: 20,
              ),
              InputField(
                  onChange: _profile.setName,
                  obscureText: false,
                  hasError: _invalidName,
                  errorText: "Invalid Name",
                  inputController: _nameController,
                  title: "Name",
                  inputHint: "Enter your name here"),
              const SizedBox(
                height: 10,
              ),
              SizedBox(
                height: 140,
                width: 400,
                child: Column(
                  children: [
                    Align(
                      alignment: Alignment.topLeft,
                      child: Padding(
                        padding: const EdgeInsets.symmetric(horizontal: 15),
                        child: Text(
                          textAlign: TextAlign.left,
                          "Bio",
                          style: GoogleFonts.poppins(
                              fontSize: 14,
                              fontWeight: FontWeight.bold,
                              color: Colors.white),
                        ),
                      ),
                    ),
                    const SizedBox(
                      height: 5,
                    ),
                    Container(
                      height: 100,
                      width: 350,
                      decoration: BoxDecoration(
                          color: const Color.fromARGB(255, 233, 243, 233),
                          borderRadius: BorderRadius.circular(10)),
                      child: Padding(
                        padding: const EdgeInsets.symmetric(
                            vertical: 5, horizontal: 10),
                        child: TextField(
                          onChanged:_profile.setBio,
                          inputFormatters: [
                            MaxLinesInputFormatter(maxLines: 3),
                            LengthLimitingTextInputFormatter(100)
                          ],
                          style: const TextStyle(fontSize: 14),
                          controller: _bioController,
                          maxLines: 3, // Set the maximum number of lines
                          minLines: 1, // Set the minimum number of lines
                          decoration: const InputDecoration(
                            labelText: "Maximum 100 characters",
                            labelStyle: TextStyle(fontSize: 12),
                            border: InputBorder.none,
                          ),
                        ),
                      ),
                    )
                  ],
                ),
              ),
              InputField(
                onChange: (value){_profile.setWeight(double.parse(value));},
                  obscureText: false,
                  keyboardType:
                      const TextInputType.numberWithOptions(decimal: true),
                  inputFormatter: <TextInputFormatter>[
                    FilteringTextInputFormatter.allow(RegExp(r'^\d+\.?\d{0,1}'))
                  ],
                  hasError: _invalidWeight,
                  errorText: "Invalid Weight",
                  inputController: _weightController,
                  title: "Weight",
                  inputHint: "Enter your current weight"),
              const Spacer(),
              Padding(
                padding:
                    const EdgeInsets.symmetric(vertical: 10, horizontal: 5),
                child: Column(
                  children: [
                    Align(
                      alignment: Alignment.bottomCenter,
                      child: SizedBox(
                        width: 400,
                        height: 45,
                        child: ElevatedButton(
                            onPressed: (){_handleSaveChanges();},
                            style: ElevatedButton.styleFrom(
                              elevation: 0,
                              backgroundColor: PrimaryColors.brightGreen,
                              padding: const EdgeInsets.symmetric(
                                  horizontal: 40, vertical: 12),
                              shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(
                                    10), // Rounded corners
                              ),
                            ),
                            child: Text(
                              "Save Changes",
                              style: GoogleFonts.roboto(
                                  fontSize: 16, color: Colors.white),
                            )),
                      ),
                    ),
                    const SizedBox(
                      height: 10,
                    ),
                    Row(
                      crossAxisAlignment: CrossAxisAlignment.center,
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Align(
                          alignment: Alignment.bottomLeft,
                          child: SizedBox(
                            width: 150,
                            height: 40,
                            child: ElevatedButton(
                                onPressed: _handleChangePassword,
                                style: ElevatedButton.styleFrom(
                                  elevation: 0,
                                  backgroundColor: PrimaryColors.darkGreen,
                                  padding: const EdgeInsets.symmetric(
                                      horizontal: 0, vertical: 5),
                                  shape: RoundedRectangleBorder(
                                    borderRadius: BorderRadius.circular(
                                        10), // Rounded corners
                                  ),
                                ),
                                child: Text(
                                  "Change Password",
                                  style: GoogleFonts.roboto(
                                      fontSize: 16, color: Colors.white),
                                )),
                          ),
                        ),
                        Align(
                          alignment: Alignment.bottomRight,
                          child: SizedBox(
                            width: 150,
                            height: 40,
                            child: ElevatedButton(
                                onPressed: () {
                                  _sendOTP(); 
                                  showDialog(
                                    context: context,
                                    builder: (BuildContext context) {
                                      return InputDialog(
                                          confirmButtonColor: Colors.red,
                                          confirmButtonText: "Delete Account",
                                          hintText: "OTP",
                                          title: "Delete Account",
                                          content:
                                              "An OTP has been sent to your email to confirm your identity",
                                          onConfirm: (inputText) {
                                            Navigator.of(context).pop();
                                            _handleDeleteAccount(inputText);
                                          },
                                          onCancel: () {
                                            Navigator.of(context).pop();
                                          });
                                    });
                                  },
                                style: ElevatedButton.styleFrom(
                                  elevation: 0,
                                  backgroundColor: Colors.red,
                                  padding: const EdgeInsets.symmetric(
                                      horizontal: 10, vertical: 5),
                                  shape: RoundedRectangleBorder(
                                    borderRadius: BorderRadius.circular(
                                        10), // Rounded corners
                                  ),
                                ),
                                child: Text(
                                  "Delete Account",
                                  style: GoogleFonts.roboto(
                                      fontSize: 16, color: Colors.white),
                                )),
                          ),
                        )
                      ],
                    ),
                    const SizedBox(
                      height: 15,
                    )
                  ],
                ),
              )
            ],
          ),
        ),
      ),
    );
  }
}
