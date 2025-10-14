import 'package:calowin/Pages/profile/changepassword_page.dart';
import 'package:calowin/common/ActionType.dart';
import 'package:calowin/common/custom_scaffold.dart';
import 'package:calowin/common/input_dialog.dart';
import 'package:calowin/common/user_profile.dart';
import 'package:calowin/control/maxline_inputformatter.dart';
import 'package:flutter/material.dart';
import 'package:calowin/common/colors_and_fonts.dart';
import 'package:google_fonts/google_fonts.dart';
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
  final _formKey = GlobalKey<FormState>();

  @override
  void initState() {
    super.initState();
    _profile = widget.profile.copyProfile(widget.profile);
    _nameController.text = _profile.getName();
    _weightController.text = _profile.getWeight().toString();
    _bioController.text = _profile.getBio();
  }

  final TextEditingController _nameController = TextEditingController();
  final TextEditingController _weightController = TextEditingController();
  final TextEditingController _bioController = TextEditingController();

  void _showLoadingDialog({String message = "Please wait..."}) {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (BuildContext context) {
        return Dialog(
          child: Padding(
            padding: const EdgeInsets.all(20.0),
            child: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                const CircularProgressIndicator(),
                const SizedBox(width: 20),
                Text(message),
              ],
            ),
          ),
        );
      },
    );
  }

  Future<void> _handleSaveChanges() async {
    if (_formKey.currentState!.validate()) {
      _formKey.currentState!.save();
      _showLoadingDialog(message: "Saving...");

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

        if (mounted) Navigator.of(context).pop();

        final Map<String, dynamic> responseData = jsonDecode(response.body);

        if (response.statusCode == 200) {
          final responseObject = UserProfile.editfromJson(responseData['UserObject']);
          setState(() {
            _profile.setName(responseObject.getName());
            _profile.setWeight(responseObject.getWeight());
            _profile.setBio(responseObject.getBio());
          });
          _showSuccessDialog(responseData['message']);
        } else {
          _showErrorDialog(responseData['message'] ?? 'An unknown error occurred.');
        }
      } catch (e) {
        if (mounted) Navigator.of(context).pop();
        _showErrorDialog("Network error: ${e.toString()}");
      }
    }
  }

  void _handleChangePassword() {
    FocusScope.of(context).unfocus();
    Navigator.push(context,
        MaterialPageRoute(builder: (context) => ChangepasswordPage(userID: _profile.getUserID())));
  }

  // MODIFIED: This function now returns true on success and false on failure.
  Future<bool> _sendOTP() async {
    _showLoadingDialog(message: "Sending OTP...");
    try {
      final response = await http.post(
        Uri.parse('https://sc3040G5-CalowinSpringNode.hf.space/central/account/send-otp'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'email': _profile.getEmail() ?? "",
          'type': ActionType.DELETE_ACCOUNT.value,
        }),
      );
      if (mounted) Navigator.of(context).pop();

      if (response.statusCode == 200) {
        _showsentSuccessDialog(response.body);
        return true;
      } else {
        _showErrorDialog(response.body);
        return false;
      }
    } catch (e) {
      if (mounted) Navigator.of(context).pop();
      _showErrorDialog('Error: ${e.toString()}');
      return false;
    }
  }

  Future<void> _handleDeleteAccount(String otpCode) async {
    _showLoadingDialog(message: "Deleting Account...");
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

      if (mounted) Navigator.of(context).pop();

      if (response.statusCode == 200) {
        _showDeleteSuccessDialog(response.body);
      } else {
        _showErrorDialog(response.body);
      }
    } catch (e) {
      if (mounted) Navigator.of(context).pop();
      _showErrorDialog('Error: ${e.toString()}');
    }
  }

  void _showErrorDialog(String message) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text("Error"),
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

  void _showsentSuccessDialog(String message) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text("OTP Sent"),
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

  void _showSuccessDialog(String message) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text("Success"),
        content: Text(message),
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
        title: Text("Account Deleted"),
        content: Text(message),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.of(context).pushNamedAndRemoveUntil('/login', (route) => false);
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
        backgroundColor: Colors.grey[100],
        appBar: AppBar(
          title: Text("Edit Profile", style: GoogleFonts.poppins(fontWeight: FontWeight.bold)),
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
                      Text("Personal Information", style: GoogleFonts.poppins(fontSize: 18, fontWeight: FontWeight.bold)),
                      const SizedBox(height: 20),
                      TextFormField(
                        controller: _nameController,
                        decoration: const InputDecoration(labelText: "Name", border: OutlineInputBorder()),
                        validator: (value) {
                          if (value == null || value.isEmpty) return "Name is required";
                          if (value.length > 16) return "Name cannot exceed 16 characters";
                          return null;
                        },
                        onSaved: (value) => _profile.setName(value!),
                      ),
                      const SizedBox(height: 16),
                      TextFormField(
                        controller: _bioController,
                        decoration: const InputDecoration(
                          labelText: "Bio",
                          hintText: "Maximum 100 characters",
                          border: OutlineInputBorder(),
                        ),
                        maxLines: 3,
                        inputFormatters: [LengthLimitingTextInputFormatter(100)],
                        validator: (value) {
                          if (value != null && value.length > 100) return "Bio cannot exceed 100 characters";
                          return null;
                        },
                        onSaved: (value) => _profile.setBio(value!),
                      ),
                      const SizedBox(height: 16),
                      TextFormField(
                        controller: _weightController,
                        decoration: const InputDecoration(labelText: "Weight (kg)", border: OutlineInputBorder()),
                        keyboardType: const TextInputType.numberWithOptions(decimal: true),
                        inputFormatters: [FilteringTextInputFormatter.allow(RegExp(r'^\d+\.?\d{0,1}'))],
                        validator: (value) {
                          if (value == null || value.isEmpty) return "Weight is required";
                          final weightPattern = r'^\d+(\.\d{1})?$';
                          if (!RegExp(weightPattern).hasMatch(value)) return "Enter a valid weight (e.g., 70 or 70.5)";
                          return null;
                        },
                        onSaved: (value) => _profile.setWeight(double.parse(value!)),
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 20),
              Card(
                elevation: 2,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                child: Column(
                  children: [
                    ListTile(
                      title: Text("Change Password", style: GoogleFonts.poppins()),
                      leading: const Icon(Icons.lock_outline),
                      trailing: const Icon(Icons.arrow_forward_ios, size: 16),
                      onTap: _handleChangePassword,
                    ),
                    const Divider(height: 1, indent: 16),
                    // MODIFIED: The onTap logic is now async and awaits the OTP send before showing the next dialog.
                    ListTile(
                      title: Text("Delete Account", style: GoogleFonts.poppins(color: Colors.red)),
                      leading: const Icon(Icons.delete_forever_outlined, color: Colors.red),
                      onTap: () async {
                        // First, await the result of sending the OTP.
                        bool otpWasSent = await _sendOTP();

                        // Only if the OTP was sent successfully, show the dialog to enter it.
                        if (otpWasSent && mounted) {
                          showDialog(
                              context: context,
                              builder: (BuildContext context) {
                                return InputDialog(
                                    confirmButtonColor: Colors.red,
                                    confirmButtonText: "Delete Account",
                                    hintText: "OTP",
                                    title: "Enter OTP",
                                    content: "An OTP has been sent to your email to confirm your identity. This action is permanent.",
                                    onConfirm: (inputText) {
                                      Navigator.of(context).pop();
                                      _handleDeleteAccount(inputText);
                                    },
                                    onCancel: () => Navigator.of(context).pop()
                                );
                              });
                        }
                      },
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
        bottomNavigationBar: Padding(
          padding: const EdgeInsets.all(16.0),
          child: ElevatedButton.icon(
            icon: const Icon(Icons.save),
            label: Text("Save Changes", style: GoogleFonts.poppins(fontSize: 16, fontWeight: FontWeight.bold)),
            onPressed: _handleSaveChanges,
            style: ElevatedButton.styleFrom(
              backgroundColor: PrimaryColors.brightGreen,
              foregroundColor: Colors.white,
              padding: const EdgeInsets.symmetric(vertical: 16),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
            ),
          ),
        ),
      ),
    );
  }
}