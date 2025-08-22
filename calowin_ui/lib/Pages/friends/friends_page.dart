import 'package:calowin/common/colors_and_fonts.dart';
import 'package:calowin/common/user_profile.dart';
import 'package:calowin/control/friends_controller.dart';
import 'package:calowin/control/page_navigator.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';

class FriendsPage extends StatefulWidget {
  final String userID;
  const FriendsPage({super.key, required this.userID});

  @override
  State<FriendsPage> createState() => _FriendsPageState();
}

class _FriendsPageState extends State<FriendsPage> {
  final FriendsController friendListRetriever = FriendsController();
  late String _userID;
  List<UserProfile> _friendlist = [];
  late UserProfile _notifier;
  bool flag = false;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    if(flag==false)
    {
      _notifier = Provider.of<UserProfile>(context,listen: true);
      _notifier.addListener(_getFriends);
      flag = true;
      _getFriends();
   }
  }

  @override
  void dispose() {
    _notifier.removeListener(_getFriends);
    super.dispose();
  }

  @override
  void initState() {
    super.initState();
    _userID = widget.userID;
  }

  // @override
  // void didUpdateWidget(FriendsPage oldWidget){
  //   super.didUpdateWidget(oldWidget);
  //   _getFriends();
  // }

  //handle the loading of friend list
  Future<void> _getFriends() async {
    print("Get friends: Friends Page");
    _friendlist = await friendListRetriever.retrieveFriendList(_userID);
    if(mounted)
    {setState(() {
      _friendlist = _friendlist;
    });}
  }

  //handle redirection to add friends page
  void _redirectToAddFriendsPage() {
    final pageNavigatorState =
        context.findAncestorStateOfType<PageNavigatorState>();
    //change here
    if (pageNavigatorState != null) {
      _notifier.updateProfile();
      pageNavigatorState.navigateToPage(6); // Navigate to AddFriendsPage
    }
  }

  //handle when friend is tapped
  void _onListItemTap(UserProfile friend) {
    final pageNavigatorState =
        context.findAncestorStateOfType<PageNavigatorState>();
    //change here
    if (pageNavigatorState != null) {
      pageNavigatorState.navigateToPage(5,
          params: {'userID': _userID,'otherUserID': friend.getUserID()}); // Navigate to OtheruserPage
    }
  }

  Widget _buildListItem(int index, UserProfile friend) {
    Color tileColor = const Color.fromARGB(255, 214, 241, 214);
    TextStyle fontStyle =
        GoogleFonts.aBeeZee(fontSize: 16, fontWeight: FontWeight.bold);
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 5, horizontal: 0),
      child: Container(
        decoration: BoxDecoration(
          color: tileColor, // Rounded corners
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.2), // Shadow color with opacity
              blurRadius: 6, // Softness of the shadow
              offset: const Offset(0, 4), // Offset in x and y direction
            ),
          ],
        ),
        child: ListTile(
          onTap: () => _onListItemTap(friend),
          leading: const Icon(
            Icons.person,
            size: 25,
          ),
          title: Text(
            friend.getName(),
            style: fontStyle,
          ),
          trailing: SizedBox(
            width: 80,
            height: 35,
            child: ElevatedButton(
                onPressed: () => _onListItemTap(friend),
                style: ElevatedButton.styleFrom(
                  elevation: 0,
                  backgroundColor: PrimaryColors.darkGreen,
                  padding:
                      const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(10), // Rounded corners
                  ),
                ),
                child: Text(
                  "Profile",
                  style: GoogleFonts.roboto(fontSize: 16, color: Colors.white),
                )),
          ),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: PrimaryColors.dullGreen,
      appBar: AppBar(
        toolbarHeight: 30,
        automaticallyImplyLeading: false,
        backgroundColor: PrimaryColors.dullGreen,
        actions: [IconButton(onPressed: _getFriends, icon: Icon(Icons.refresh))],
      ),
      body: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 10, horizontal: 10),
          child: Column(
            children: [
              Align(
                alignment: Alignment.topLeft,
                child: Text(
                  "Friends",
                  style: GoogleFonts.poppins(
                      color: Colors.white,
                      fontWeight: FontWeight.bold,
                      fontSize: 22),
                ),
              ),
              SizedBox(
                height: 500,
                child: ListView.builder(
                  scrollDirection: Axis.vertical,
                  shrinkWrap: true,
                  itemCount: _friendlist.length,
                  itemBuilder: (context, index) {
                    return _buildListItem(index, _friendlist[index]);
                  },
                ),
              ),
              Divider(
                indent: 20,
                endIndent: 20,
                color: Colors.grey.shade400,
                thickness: 2,
              ),
              const SizedBox(
                height: 30,
              ),
              Center(
                child: SizedBox(
                  width: 150,
                  height: 40,
                  child: ElevatedButton(
                      onPressed: _redirectToAddFriendsPage,
                      style: ElevatedButton.styleFrom(
                        elevation: 0,
                        backgroundColor: PrimaryColors.darkGreen,
                        padding: const EdgeInsets.symmetric(
                            horizontal: 10, vertical: 5),
                        shape: RoundedRectangleBorder(
                          borderRadius:
                              BorderRadius.circular(10), // Rounded corners
                        ),
                      ),
                      child: Text(
                        "Add Friends",
                        style: GoogleFonts.roboto(
                            fontSize: 16, color: Colors.white),
                      )),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
