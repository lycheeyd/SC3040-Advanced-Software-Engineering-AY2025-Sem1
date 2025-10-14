import 'package:calowin/common/colors_and_fonts.dart';
import 'package:calowin/common/custom_scaffold.dart';
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
  bool _searchPerformed = false;
  bool _isSearching = false;

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
    _notifier.removeListener(_getRequesters);
    _searchController.dispose();
    super.dispose();
  }

  @override
  void initState() {
    super.initState();
    _profile = widget.profile;
  }

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
      pageNavigatorState.navigateToPage(3);
    }
  }

  void _handleSearch(String search) async {
    if (search.isEmpty) {
      setState(() {
        _searchList = [];
        _searchPerformed = false;
        _isSearching = false;
      });
      return;
    }

    setState(() {
      _isSearching = true;
      _searchPerformed = true;
    });

    try {
      _searchList = await _friendsController.searchUser(search,_profile.getUserID());
    } finally {
      if(mounted) {
        setState(() {
          _isSearching = false;
        });
      }
    }
  }

  void _onSearchItemTap(String id) {
    final pageNavigatorState =
    context.findAncestorStateOfType<PageNavigatorState>();
    if (pageNavigatorState != null) {
      FocusScope.of(context).unfocus();
      pageNavigatorState.navigateToPage(5,params: {'otherUserID': id,'userID':_profile.getUserID()});
    }
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
                Text("Please wait..."),
              ],
            ),
          ),
        );
      },
    );
  }

  // MODIFIED: _handleAccept now shows a loading spinner.
  void _handleAccept(String id) async {
    _showLoadingDialog(); // Show spinner
    try {
      bool success = await _friendsController.acceptFriend(_profile.getUserID(),id);
      if(mounted) Navigator.of(context).pop(); // Hide spinner

      if(success){
        _getRequesters();
        _profile.updateProfile();
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
    } catch (e) {
      if(mounted) Navigator.of(context).pop(); // Hide spinner on error
    }
  }

  // MODIFIED: _handleReject now shows a loading spinner.
  void _handleReject(String id) async {
    _showLoadingDialog(); // Show spinner
    try {
      bool success = await _friendsController.rejectFriend(_profile.getUserID(),id);
      if(mounted) Navigator.of(context).pop(); // Hide spinner

      if(success){
        _getRequesters();
        _profile.updateProfile();
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
    } catch (e) {
      if(mounted) Navigator.of(context).pop(); // Hide spinner on error
    }
  }

  Widget _buildSearchListItem(UserProfile user) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 0, vertical: 4),
      child: ListTile(
        onTap: () => _onSearchItemTap(user.getUserID()),
        leading: const CircleAvatar(
          backgroundColor: PrimaryColors.dullGreen,
          child: Icon(Icons.person, color: Colors.white),
        ),
        title: Text(user.getName(), style: GoogleFonts.poppins(fontWeight: FontWeight.bold)),
        trailing: const Icon(Icons.arrow_forward_ios, size: 16),
      ),
    );
  }

  Widget _buildFriendRequestItem(UserProfile user) {
    return Card(
      margin: const EdgeInsets.symmetric(vertical: 6),
      child: Padding(
        padding: const EdgeInsets.all(12.0),
        child: Column(
          children: [
            ListTile(
              onTap: () => _onSearchItemTap(user.getUserID()),
              contentPadding: EdgeInsets.zero,
              leading: const CircleAvatar(
                backgroundColor: PrimaryColors.dullGreen,
                child: Icon(Icons.person_add_alt_1, color: Colors.white),
              ),
              title: Text(user.getName(), style: GoogleFonts.poppins(fontWeight: FontWeight.bold)),
              subtitle: const Text("Sent you a friend request"),
            ),
            const SizedBox(height: 10),
            Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                TextButton(
                  onPressed: () => _handleReject(user.getUserID()),
                  child: const Text("Reject", style: TextStyle(color: Colors.red)),
                ),
                const SizedBox(width: 8),
                ElevatedButton(
                  onPressed: () => _handleAccept(user.getUserID()),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: PrimaryColors.brightGreen,
                    foregroundColor: Colors.white,
                  ),
                  child: const Text("Accept"),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildEmptyState({required String title, required String message, required IconData icon}) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 32.0, horizontal: 16.0),
        child: Column(
          children: [
            Icon(icon, size: 50, color: Colors.grey[400]),
            const SizedBox(height: 12),
            Text(title, style: GoogleFonts.poppins(fontSize: 16, fontWeight: FontWeight.bold, color: Colors.grey[600])),
            Text(message, textAlign: TextAlign.center, style: GoogleFonts.poppins(color: Colors.grey[500])),
          ],
        ),
      ),
    );
  }

  final TextEditingController _searchController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return CustomScaffold(
      body: Scaffold(
        backgroundColor: Colors.grey[100],
        appBar: AppBar(
          title: Text("Add Friends", style: GoogleFonts.poppins(fontWeight: FontWeight.bold)),
          backgroundColor: PrimaryColors.dullGreen,
          foregroundColor: Colors.white,
          leading: IconButton(
            icon: const Icon(Icons.arrow_back),
            onPressed: _handleBack,
          ),
        ),
        body: ListView(
          padding: const EdgeInsets.all(16.0),
          children: [
            TextField(
              controller: _searchController,
              onSubmitted: _handleSearch,
              decoration: InputDecoration(
                hintText: "Search for friends by name",
                prefixIcon: const Icon(Icons.search),
                filled: true,
                fillColor: Colors.white,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(30),
                  borderSide: BorderSide.none,
                ),
              ),
            ),
            const SizedBox(height: 16),

            if (_searchPerformed)
              _isSearching
                  ? const Center(child: Padding(padding: EdgeInsets.all(16.0), child: CircularProgressIndicator()))
                  : _searchList.isEmpty
                  ? _buildEmptyState(
                icon: Icons.search_off,
                title: "No Users Found",
                message: "Check the spelling or try a different name.",
              )
                  : Column(
                children: _searchList.map((user) => _buildSearchListItem(user)).toList(),
              ),

            const Padding(
              padding: EdgeInsets.symmetric(vertical: 16.0),
              child: Divider(),
            ),

            Text(
              "Friend Requests",
              style: GoogleFonts.poppins(
                fontSize: 20,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            _friendRequests.isEmpty
                ? _buildEmptyState(
              icon: Icons.notifications_none,
              title: "No Pending Requests",
              message: "You have no new friend requests right now.",
            )
                : Column(
              children: _friendRequests.map((user) => _buildFriendRequestItem(user)).toList(),
            ),
          ],
        ),
      ),
    );
  }
}