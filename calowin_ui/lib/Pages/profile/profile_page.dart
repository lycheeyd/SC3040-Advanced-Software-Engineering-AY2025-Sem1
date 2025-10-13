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
  //define retrieve logic here
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
    } // Add listener for profile changes
  }

  @override
  void dispose() {
    _profile.removeListener(getUserProfile);
    super.dispose();
  }

  void getUserProfile() {
    print("GetUserProfile: Profile Page");
    _badges = [];
    for (int i = 0; i < _profile.getBadges().length; i++) {
      if (Words2widgetConverter.convert(_profile.getBadges()[i]) != null) {
        _badges.add(Words2widgetConverter.convert(_profile.getBadges()[i]));
      }
    }
  }

  Future<void> _handleEditProfile() async {
    FocusScope.of(context).unfocus();
    final updatedProfile = await Navigator.push<UserProfile>(context,
        MaterialPageRoute(builder: (context) => EditprofilePage(profile: _profile)));
      
    if (updatedProfile != null) {
      setState(() {
        //_profile = updatedProfile;
        _profile.setName(updatedProfile.getName());
        _profile.setBio(updatedProfile.getBio());
        _profile.setWeight(updatedProfile.getWeight());
        _profile.updateProfile(); //notify other pages about this change
      });
    }
  }

  void _handleLogOut() {
    FocusScope.of(context).unfocus();
    Navigator.of(context).pushNamedAndRemoveUntil('/login', (Route<dynamic> route) => false);
  }

  Widget fieldBuilder(String title, String content) {
    return SizedBox(
      height: 50,
      width: 400,
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 2),
        child: Column(
          children: [
            Row(
              children: [
                SizedBox(
                  width: 100,
                  child: Text(
                    title,
                    style: const TextStyle(
                        fontStyle: FontStyle.italic, color: Colors.black),
                  ),
                ),
                const SizedBox(
                  width: 30,
                ),
                SizedBox(
                    width: 200,
                    child: Text(
                      content,
                      style: const TextStyle(color: Colors.black,fontSize: 12),
                    )),
              ],
            ),
            Divider(
              thickness: 0.3,
              color: Colors.grey.shade600,
            )
          ],
        ),
      ),
    );
  }

  Widget fieldInContainerBuilder(String title, String content, String unit) {
    return SizedBox(
      height: 35,
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 2),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Align(
              alignment: Alignment.centerLeft,
              child: SizedBox(
                child: Text(
                  textAlign: TextAlign.start,
                  title,
                  style: const TextStyle(
                      fontStyle: FontStyle.italic, color: Colors.black),
                ),
              ),
            ),
            Align(
              alignment: Alignment.centerRight,
              child: SizedBox(
                  child: Text(
                textAlign: TextAlign.right,
                "$content $unit",
                style: const TextStyle(color: Colors.black),
              )),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: PrimaryColors.dullGreen,
      body: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(vertical: 20, horizontal: 10),
        child: Column(
          children: [
            fieldBuilder("Name", _profile.getName()),
            fieldBuilder("User ID", _profile.getUserID()),
            fieldBuilder("Email", _profile.getEmail() ?? "Error Retrieving"),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 2),
              child: SizedBox(
                height: 120,
                width: 400,
                child: Column(
                  children: [
                    const Align(
                      alignment: Alignment.topLeft,
                      child: Text(
                        textAlign: TextAlign.left,
                        "Bio",
                        style: TextStyle(color: Colors.black, fontSize: 18),
                      ),
                    ),
                    Container(
                      height: 80,
                      width: 400,
                      decoration: BoxDecoration(
                          color: const Color.fromARGB(255, 233, 243, 233),
                          borderRadius: BorderRadius.circular(10)),
                      child: Padding(
                        padding: const EdgeInsets.symmetric(
                            vertical: 10, horizontal: 20),
                        child: Text(_profile.getBio(),
                            style: PrimaryFonts.systemFont
                                .copyWith(color: Colors.black, fontSize: 14)),
                      ),
                    )
                  ],
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 2),
              child: SizedBox(
                height: 170,
                width: 400,
                child: Column(
                  children: [
                    const Align(
                      alignment: Alignment.topLeft,
                      child: Text(
                        textAlign: TextAlign.left,
                        "Stats",
                        style: TextStyle(color: Colors.black, fontSize: 18),
                      ),
                    ),
                    Container(
                      height: 130,
                      decoration: BoxDecoration(
                          color: const Color.fromARGB(255, 153, 240, 152),
                          borderRadius: BorderRadius.circular(10)),
                      child: Padding(
                          padding: const EdgeInsets.symmetric(
                              vertical: 10, horizontal: 10),
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              fieldInContainerBuilder("Weight",
                                  _profile.getWeight().toString(), "kg"),
                              fieldInContainerBuilder("Total Carbon Saved",
                                  _profile.getCarbonSaved().toString(), "g"),
                              fieldInContainerBuilder("Total Calorie Burned",
                                  _profile.getCalorieBurn().toString(), "kcal")
                            ],
                          )),
                    )
                  ],
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 2),
              child: SizedBox(
                height: 100,
                width: 400,
                child: Column(
                  children: [
                    const Align(
                      alignment: Alignment.topLeft,
                      child: Text(
                        textAlign: TextAlign.left,
                        "Badges",
                        style: TextStyle(color: Colors.black, fontSize: 18),
                      ),
                    ),
                    Container(
                      height: 60,
                      width: 400,
                      decoration: BoxDecoration(
                          color: const Color.fromARGB(255, 153, 240, 152),
                          borderRadius: BorderRadius.circular(10)),
                      child: Padding(
                          padding: const EdgeInsets.symmetric(
                              vertical: 2, horizontal: 2),
                          child: ListView.builder(
                            scrollDirection: Axis.horizontal,
                            shrinkWrap: true,
                            itemCount: _badges.length,
                            itemBuilder: (context, index) {
                              return Padding(
                                  padding: const EdgeInsets.symmetric(
                                      vertical: 4, horizontal: 5),
                                  child: SizedBox(
                                    height: 30,
                                    width: 30,
                                    child: _badges[index],
                                  ));
                            },
                          )),
                    )
                  ],
                ),
              ),
            ),
            Padding(
                  padding:
                      const EdgeInsets.symmetric(horizontal: 10, vertical: 2),
                  child: Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Align(
                        alignment: Alignment.bottomLeft,
                        child: SizedBox(
                          width: 150,
                          height: 40,
                          child: ElevatedButton(
                              onPressed: _handleEditProfile,
                              style: ElevatedButton.styleFrom(
                                elevation: 0,
                                backgroundColor: PrimaryColors.darkGreen,
                                padding: const EdgeInsets.symmetric(
                                    horizontal: 10, vertical: 5),
                                shape: RoundedRectangleBorder(
                                  borderRadius: BorderRadius.circular(
                                      10), // Rounded corners
                                ),
                              ),
                              child: Text(
                                "Edit Profile",
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
                              onPressed: _handleLogOut,
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
                                "Log Out",
                                style: GoogleFonts.roboto(
                                    fontSize: 16, color: Colors.white),
                              )),
                        ),
                      )
                    ],
                  ),
                ),
          ],
        ),
      ),
    );
  }
}
