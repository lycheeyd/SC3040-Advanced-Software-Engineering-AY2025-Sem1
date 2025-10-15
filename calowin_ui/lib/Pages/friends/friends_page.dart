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
  // NEW: A state variable to manage the loading spinner.
  bool _isLoading = true;

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

  // MODIFIED: This function now manages the loading state.
  Future<void> _getFriends() async {
    if(mounted) {
      setState(() {
        _isLoading = true;
      });
    }

    try {
      final friends = await friendListRetriever.retrieveFriendList(_userID);
      if(mounted) {
        setState(() {
          _friendlist = friends;
        });
      }
    } finally {
      if(mounted) {
        setState(() {
          _isLoading = false;
        });
      }
    }
  }

  void _redirectToAddFriendsPage() {
    final pageNavigatorState =
    context.findAncestorStateOfType<PageNavigatorState>();
    if (pageNavigatorState != null) {
      _notifier.updateProfile();
      pageNavigatorState.navigateToPage(6); // Navigate to AddFriendsPage
    }
  }

  void _onListItemTap(UserProfile friend) {
    final pageNavigatorState =
    context.findAncestorStateOfType<PageNavigatorState>();
    if (pageNavigatorState != null) {
      pageNavigatorState.navigateToPage(5,
          params: {'userID': _userID,'otherUserID': friend.getUserID()}); // Navigate to OtheruserPage
    }
  }

  Widget _buildListItem(int index, UserProfile friend) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
      child: ListTile(
        onTap: () => _onListItemTap(friend),
        leading: const CircleAvatar(
          backgroundColor: PrimaryColors.dullGreen,
          child: Icon(Icons.person, color: Colors.white),
        ),
        title: Text(
          friend.getName(),
          style: GoogleFonts.poppins(fontWeight: FontWeight.bold),
        ),
        trailing: const Icon(Icons.arrow_forward_ios, size: 16, color: Colors.grey),
      ),
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.people_outline, size: 80, color: Colors.grey[400]),
          const SizedBox(height: 16),
          Text(
            "No Friends Yet",
            style: GoogleFonts.poppins(
              fontSize: 18,
              fontWeight: FontWeight.bold,
              color: Colors.grey[600],
            ),
          ),
          const SizedBox(height: 8),
          Text(
            "Tap the button below to add friends!",
            textAlign: TextAlign.center,
            style: GoogleFonts.poppins(fontSize: 14, color: Colors.grey[500]),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[100],
      appBar: AppBar(
        automaticallyImplyLeading: false,
        backgroundColor: PrimaryColors.dullGreen,
        title: Text(
          "Friends",
          style: GoogleFonts.poppins(
              color: Colors.white,
              fontWeight: FontWeight.bold
          ),
        ),
        actions: [
          IconButton(
              onPressed: _isLoading ? null : _getFriends,
              icon: const Icon(Icons.refresh, color: Colors.white,)
          )
        ],
      ),
      body: Column(
        children: [
          // MODIFIED: The body now shows a spinner while loading.
          Expanded(
            child: _isLoading
                ? const Center(child: CircularProgressIndicator())
                : _friendlist.isEmpty
                ? _buildEmptyState()
                : ListView.builder(
              padding: const EdgeInsets.symmetric(vertical: 8),
              itemCount: _friendlist.length,
              itemBuilder: (context, index) {
                return _buildListItem(index, _friendlist[index]);
              },
            ),
          ),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 20),
            color: Colors.grey[100],
            child: SizedBox(
              width: double.infinity,
              child: ElevatedButton.icon(
                icon: const Icon(Icons.person_add),
                onPressed: _redirectToAddFriendsPage,
                style: ElevatedButton.styleFrom(
                  backgroundColor: PrimaryColors.darkGreen,
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 14),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(10),
                  ),
                ),
                label: Text(
                  "Add Friends",
                  style: GoogleFonts.poppins(fontSize: 16, fontWeight: FontWeight.bold),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}