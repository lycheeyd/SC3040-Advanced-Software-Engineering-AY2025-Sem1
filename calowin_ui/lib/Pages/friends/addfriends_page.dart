import 'package:calowin/common/colors_and_fonts.dart';
import 'package:calowin/common/singlebutton_dialog.dart';
import 'package:calowin/common/user_profile.dart';
import 'package:calowin/control/friends_controller.dart';
import 'package:calowin/control/page_navigator.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';

class AddfriendsPage extends StatefulWidget {
  final UserProfile profile;
  const AddfriendsPage({super.key,required this.profile});

  @override
  State<AddfriendsPage> createState() => _AddfriendsPageState();
}

class _AddfriendsPageState extends State<AddfriendsPage> {
  late UserProfile _profile;
  List<UserProfile> _searchList = [];
  List<UserProfile> _friendRequests = [];
  final FriendsController _friendsController = FriendsController();
  late UserProfile _notifier;
  bool flag = false;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    if(flag == false)
    {
      _notifier = Provider.of<UserProfile>(context,listen: true);
      _notifier.addListener(_getRequesters);
      flag = true;
      _getRequesters();
    }
  }

  @override
  void dispose() {
    super.dispose();
    _notifier.removeListener(_getRequesters);
  }

  @override
  void initState() {
    super.initState();
    _profile = widget.profile;
  }

  // @override  
  // void didUpdateWidget(AddfriendsPage oldWidget){
  //   super.didUpdateWidget(oldWidget);
  //   _getRequesters();
  // }

  Future<void> _getRequesters() async {
    print("getRequester: AddFriendsPage");
    _friendRequests = await _friendsController.retrieveRequesterList(_profile.getUserID());
    if(mounted)
   { setState(() {
      _friendRequests = _friendRequests;
    });}
  }


  void _handleBack() {
    final pageNavigatorState =
        context.findAncestorStateOfType<PageNavigatorState>();
    if (pageNavigatorState != null) {
      FocusScope.of(context).unfocus();
      pageNavigatorState.navigateToPage(3); // Navigate to AddFriendsPage
    }
  }

  void _handleSearch(String search) async {
    _searchList = await _friendsController.searchUser(search,_profile.getUserID());
    setState(() {
      _searchList = _searchList;
    });
  }

  void _onSearchItemTap(String id) {
    final pageNavigatorState =
        context.findAncestorStateOfType<PageNavigatorState>();
    if (pageNavigatorState != null) {
      FocusScope.of(context).unfocus();
      pageNavigatorState.navigateToPage(5,params: {'otherUserID': id,'userID':_profile.getUserID()}); // Navigate to OtheruserPage
    }
  }

  void _handleAccept(String id) async {
    bool success = await _friendsController.acceptFriend(_profile.getUserID(),id);
    setState(() {
      if(success){
        _getRequesters();
        _profile.updateProfile(); //notify other pages that user relationships has been changed
        showDialog(
          context: context, 
          builder: (BuildContext context) {
            return SinglebuttonDialog(title: "Success", content: "Friend added successfully", onConfirm: ()=>Navigator.pop(context));
            }); 
      }
      else{
        showDialog(
          context: context, 
          builder: (BuildContext context) {
            return SinglebuttonDialog(title: "Failed", content: "Failed to add friend, please try again later", onConfirm: ()=>Navigator.pop(context));
            });
      }
    });
  }

  void _handleReject(String id) async {
    bool success = await _friendsController.rejectFriend(_profile.getUserID(),id);
    _profile.updateProfile(); //notify other pages that user relationships has been changed
    setState(() {
      if(success){
        showDialog(
          context: context, 
          builder: (BuildContext context) {
            return SinglebuttonDialog(title: "Success", content: "Request rejected successfully", onConfirm: ()=>Navigator.pop(context));
            }); 
      }
      else{
        showDialog(
          context: context, 
          builder: (BuildContext context) {
            return SinglebuttonDialog(title: "Failed", content: "Failed to reject request, please try again later", onConfirm: ()=>Navigator.pop(context));
            });
      }
    });
  }

  Widget _buildSearchListItem(int index, UserProfile user) {
    Color tileColor = const Color.fromARGB(255, 214, 241, 214);
    TextStyle fontStyle =
        GoogleFonts.aBeeZee(fontSize: 16, fontWeight: FontWeight.bold);
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 5, horizontal: 0),
      child: Container(
        decoration: BoxDecoration(
          color: tileColor,
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.2),
              blurRadius: 6,
              offset: const Offset(0, 4),
            ),
          ],
        ),
        child: ListTile(
          onTap: () => _onSearchItemTap(user.getUserID()),
          leading: const Icon(
            Icons.person,
            size: 25,
          ),
          title: Text(
            user.getName(),
            style: fontStyle,
          ),
        ),
      ),
    );
  }

  Widget _buildFriendRequests(int index, UserProfile user) {
    Color tileColor = const Color.fromARGB(255, 214, 241, 214);
    TextStyle fontStyle =
        GoogleFonts.aBeeZee(fontSize: 16, fontWeight: FontWeight.bold);
    return GestureDetector(
      onTap: ()=>_onSearchItemTap(user.getUserID()),
      child: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Container(
          height: 90,
          width: 400,
          decoration: BoxDecoration(
            color: tileColor,
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.2),
                blurRadius: 6,
                offset: const Offset(0, 4),
              ),
            ],
          ),
          child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 10, horizontal: 15),
            child: Column(
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.center,
                  children: [
                    const Icon(
                      Icons.person_add,
                      size: 25,
                    ),
                    const SizedBox(
                      width: 10,
                    ),
                    Text(
                      user.getName(),
                      style: fontStyle,
                    ),
                  ],
                ),
                const SizedBox(height: 15),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                  crossAxisAlignment: CrossAxisAlignment.center,
                  children: [
                    Align(
                      alignment: Alignment.bottomRight,
                      child: SizedBox(
                        width: 140,
                        height: 27,
                        child: ElevatedButton(
                          onPressed: ()=>_handleAccept(user.getUserID()),
                          style: ElevatedButton.styleFrom(
                            elevation: 0,
                            backgroundColor: PrimaryColors.brightGreen,
                            padding: const EdgeInsets.symmetric(horizontal: 10),
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(10),
                            ),
                          ),
                          child: Text(
                            "Accept",
                            style: GoogleFonts.roboto(
                                fontSize: 13, color: Colors.white),
                          ),
                        ),
                      ),
                    ),
                    Align(
                      alignment: Alignment.bottomLeft,
                      child: SizedBox(
                        width: 140,
                        height: 27,
                        child: ElevatedButton(
                          onPressed: ()=>_handleReject(user.getUserID()),
                          style: ElevatedButton.styleFrom(
                            elevation: 0,
                            backgroundColor: Colors.red,
                            padding: const EdgeInsets.symmetric(horizontal: 10),
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(10),
                            ),
                          ),
                          child: Text(
                            "Reject",
                            style: GoogleFonts.roboto(
                                fontSize: 13, color: Colors.white),
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  final TextEditingController _searchController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      resizeToAvoidBottomInset: false,
      backgroundColor: PrimaryColors.dullGreen,
      appBar: AppBar(
        backgroundColor: PrimaryColors.dullGreen,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: _handleBack,
        ),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
        child: Column(
          children: [
            Align(
              alignment: Alignment.topLeft,
              child: Text(
                "Add Friends!",
                style: GoogleFonts.poppins(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                  fontSize: 22,
                ),
              ),
            ),
            Container(
              height: 45,
              padding:
                  const EdgeInsets.symmetric(horizontal: 10.0, vertical: 0),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(15.0),
              ),
              child: TextField(
                controller: _searchController,
                decoration: InputDecoration(
                  hintText: "Search for your friends!",
                  hintStyle: const TextStyle(fontSize: 13, color: Colors.grey),
                  border: InputBorder.none,
                  suffixIcon: IconButton(
                    icon: const Icon(Icons.search),
                    onPressed: ()=>_handleSearch(_searchController.text),
                  ),
                ),
              ),
            ),
            const SizedBox(height: 30),

            // Search List
            Column(
              children: _searchList.isEmpty
                  ? [
                      const SizedBox()
                    ] // Return an empty widget if no search results
                  : List.generate(
                      _searchList.length,
                      (index) =>
                          _buildSearchListItem(index, _searchList[index]),
                    ),
            ),

            Padding(
              padding: const EdgeInsets.symmetric(vertical: 10),
              child: Divider(
                color: Colors.grey.shade400,
                thickness: 2,
                height: 10,
              ),
            ),

            Align(
              alignment: Alignment.topLeft,
              child: Text(
                "Friend Requests",
                style: GoogleFonts.poppins(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                  fontSize: 22,
                ),
              ),
            ),

            // Friend Requests List
            Column(
              children: _friendRequests.isEmpty
                  ? [
                      const SizedBox()
                    ] // Return an empty widget if no friend requests
                  : List.generate(
                      _friendRequests.length,
                      (index) =>
                          _buildFriendRequests(index, _friendRequests[index]),
                    ),
            ),
          ],
        ),
      ),
    );
  }
}
