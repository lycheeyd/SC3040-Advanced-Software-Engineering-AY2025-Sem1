import 'package:calowin/Pages/profile/editprofile_page.dart';
import 'package:calowin/common/colors_and_fonts.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:calowin/common/user_profile.dart';
import 'package:calowin/control/words2widget_converter.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class ProfilePage extends StatefulWidget {
  final UserProfile profile;
  const ProfilePage({super.key,required this.profile});

  @override
  State<ProfilePage> createState() => _ProfilePageState();
}

class _ProfilePageState extends State<ProfilePage> {
  late UserProfile _profile;
  late List<Image?> _badges = [];
  bool flag = false;

  @override
  void initState() {
    super.initState();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    if(flag == false)
    {
      _profile = Provider.of<UserProfile>(context);
      _profile.addListener(getUserProfile);
      flag = true;
      getUserProfile();
    }
  }

  @override
  void dispose() {
    _profile.removeListener(getUserProfile);
    super.dispose();
  }

  void getUserProfile() {
    print("GetUserProfile: Profile Page");
    if(mounted){
      setState(() {
        _badges = [];
        for (int i = 0; i < _profile.getBadges().length; i++) {
          if (Words2widgetConverter.convert(_profile.getBadges()[i]) != null) {
            _badges.add(Words2widgetConverter.convert(_profile.getBadges()[i]));
          }
        }
      });
    }
  }

  Future<void> _handleEditProfile() async {
    FocusScope.of(context).unfocus();
    final updatedProfile = await Navigator.push<UserProfile>(context,
        MaterialPageRoute(builder: (context) => EditprofilePage(profile: _profile)));

    if (updatedProfile != null) {
      setState(() {
        _profile.setName(updatedProfile.getName());
        _profile.setBio(updatedProfile.getBio());
        _profile.setWeight(updatedProfile.getWeight());
        _profile.updateProfile();
      });
    }
  }

  // NEW: A reusable function to show a loading spinner dialog.
  void _showLoadingDialog({String message = "Logging out..."}) {
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

  // MODIFIED: This function now shows a loading spinner before navigating.
  void _handleLogOut() {
    _showLoadingDialog(); // Show spinner
    FocusScope.of(context).unfocus();

    // Adding a small delay to ensure the dialog has time to appear before navigation.
    Future.delayed(const Duration(milliseconds: 500), () {
      if(mounted) {
        Navigator.of(context).pushNamedAndRemoveUntil('/login', (Route<dynamic> route) => false);
      }
    });
  }

  Widget _buildStatTile(String title, String value, String unit) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0, horizontal: 16.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(title, style: GoogleFonts.poppins(fontSize: 15)),
          Text(
            "$value $unit",
            style: GoogleFonts.poppins(fontSize: 15, fontWeight: FontWeight.bold),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[100],
      body: ListView(
        padding: EdgeInsets.zero,
        children: [
          Container(
            padding: const EdgeInsets.only(top: 40, bottom: 20),
            decoration: BoxDecoration(
              color: PrimaryColors.dullGreen,
              borderRadius: const BorderRadius.only(
                bottomLeft: Radius.circular(30),
                bottomRight: Radius.circular(30),
              ),
            ),
            child: Column(
              children: [
                const CircleAvatar(
                  radius: 50,
                  backgroundColor: Colors.white,
                  child: Icon(Icons.person, size: 60, color: PrimaryColors.dullGreen),
                ),
                const SizedBox(height: 12),
                Text(
                  _profile.getName(),
                  style: GoogleFonts.poppins(
                    fontSize: 22,
                    fontWeight: FontWeight.bold,
                    color: Colors.white,
                  ),
                ),
                Text(
                  "User ID: ${_profile.getUserID()}",
                  style: GoogleFonts.poppins(fontSize: 14, color: Colors.white70),
                ),
              ],
            ),
          ),
          const SizedBox(height: 20),

          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16.0),
            child: Card(
              elevation: 2,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text("Bio", style: GoogleFonts.poppins(fontSize: 18, fontWeight: FontWeight.bold)),
                    const SizedBox(height: 8),
                    Text(
                      _profile.getBio().isNotEmpty ? _profile.getBio() : "No bio available.",
                      style: GoogleFonts.poppins(fontSize: 15, color: Colors.black54, height: 1.5),
                    ),
                  ],
                ),
              ),
            ),
          ),
          const SizedBox(height: 16),

          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16.0),
            child: Card(
              elevation: 2,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Padding(
                    padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
                    child: Text("Stats", style: GoogleFonts.poppins(fontSize: 18, fontWeight: FontWeight.bold)),
                  ),
                  _buildStatTile("Weight", _profile.getWeight().toStringAsFixed(1), "kg"),
                  const Divider(indent: 16, endIndent: 16, height: 1),
                  _buildStatTile("Carbon Saved", _profile.getCarbonSaved().toString(), "g"),
                  const Divider(indent: 16, endIndent: 16, height: 1),
                  _buildStatTile("Calories Burned", _profile.getCalorieBurn().toString(), "kcal"),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),

          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16.0),
            child: Card(
              elevation: 2,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text("Badges", style: GoogleFonts.poppins(fontSize: 18, fontWeight: FontWeight.bold)),
                    const SizedBox(height: 12),
                    _badges.isEmpty
                        ? const Text("No badges earned yet.", style: TextStyle(color: Colors.black54))
                        : Wrap(
                      spacing: 16.0,
                      runSpacing: 16.0,
                      children: _badges.map((badge) => SizedBox(
                        height: 50,
                        width: 50,
                        child: badge,
                      )).toList(),
                    ),
                  ],
                ),
              ),
            ),
          ),
          const SizedBox(height: 24),

          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16.0),
            child: Column(
              children: [
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton.icon(
                    icon: const Icon(Icons.edit),
                    onPressed: _handleEditProfile,
                    style: ElevatedButton.styleFrom(
                      backgroundColor: PrimaryColors.darkGreen,
                      foregroundColor: Colors.white,
                      padding: const EdgeInsets.symmetric(vertical: 14),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                    ),
                    label: Text("Edit Profile", style: GoogleFonts.poppins(fontSize: 16, fontWeight: FontWeight.bold)),
                  ),
                ),
                const SizedBox(height: 12),
                SizedBox(
                  width: double.infinity,
                  child: TextButton.icon(
                    icon: const Icon(Icons.logout, color: Colors.red),
                    onPressed: _handleLogOut,
                    style: TextButton.styleFrom(
                      padding: const EdgeInsets.symmetric(vertical: 14),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                    ),
                    label: Text(
                      "Log Out",
                      style: GoogleFonts.poppins(fontSize: 16, fontWeight: FontWeight.bold, color: Colors.red),
                    ),
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 24),
        ],
      ),
    );
  }
}