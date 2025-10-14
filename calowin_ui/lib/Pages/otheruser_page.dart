import 'package:calowin/common/colors_and_fonts.dart';
import 'package:calowin/common/dualbutton_dialog.dart';
import 'package:calowin/common/singlebutton_dialog.dart';
import 'package:calowin/control/friends_controller.dart';
import 'package:flutter/foundation.dart';
import 'package:calowin/control/user_retriever.dart';
import 'package:calowin/control/page_navigator.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:calowin/common/user_profile.dart';
import 'package:calowin/control/words2widget_converter.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class OtheruserPage extends StatefulWidget {
  final String? otherUserID;
  final UserProfile profile;

  const OtheruserPage({super.key,required this.profile,this.otherUserID});

  @override
  State<OtheruserPage> createState() => _OtheruserPageState();
}

class _OtheruserPageState extends State<OtheruserPage> {
  late UserProfile _selfProfileNotifier;
  late String? _otherUserID;
  late UserProfile _profile = UserProfile(name: "NA", userID: "NA");
  late List<Image?> _badges = [];
  late UserStatus _userStatus;

  bool? _userFound;
  bool _isLoading = true;

  final UserRetriever _userRetriever = UserRetriever();
  final FriendsController _friendsController = FriendsController();

  @override
  void initState() {
    super.initState();
    _selfProfileNotifier = widget.profile;
    _otherUserID = widget.otherUserID;
    getUserProfile(_selfProfileNotifier.getUserID(),_otherUserID);
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    _selfProfileNotifier = Provider.of<UserProfile>(context,listen: false);
  }

  @override
  void didUpdateWidget(OtheruserPage oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (widget.otherUserID != oldWidget.otherUserID) {
      _otherUserID = widget.otherUserID;
      setState(() {
        _isLoading = true;
        _userFound = null;
      });
      getUserProfile(_selfProfileNotifier.getUserID(),_otherUserID);
    }
  }

  Future<void> getUserProfile(String? user, String? otherUser) async {
    if(user == null  || otherUser == null) {
      if(mounted){
        setState(() {
          _isLoading = false;
          _userFound = false;
        });
      }
      return;
    }

    try {
      _profile = await _userRetriever.retrieveFriend(user, otherUser);
      if (_profile.getUserID() == "Error retrieving user" || _profile.getUserID() == "Unable to connect to server") {
        setState(() {
          _userFound = false;
        });
        return;
      }
      _userStatus = _profile.getStatus() ?? UserStatus.STRANGER;
      _badges = [];
      if(_profile.getBadges().isNotEmpty) {
        for (int i = 0; i < _profile.getBadges().length; i++) {
          if (Words2widgetConverter.convert(_profile.getBadges()[i]) != null) {
            _badges.add(Words2widgetConverter.convert(_profile.getBadges()[i]));
          }
        }
      }
      setState(() {
        _userFound = true;
      });
    } finally {
      if(mounted){
        setState(() {
          _isLoading = false;
        });
      }
    }
  }

  void _handleBack() {
    final pageNavigatorState =
    context.findAncestorStateOfType<PageNavigatorState>();
    if (pageNavigatorState != null) {
      FocusScope.of(context).unfocus();
      pageNavigatorState.navigateToPage(3);
    }
  }

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

  // MODIFIED: Each function now handles its own loading and state update for immediate UI feedback.
  Future<void> _handleRequestFriend() async {
    _showLoadingDialog();
    bool success = await _friendsController.requestFriend(_selfProfileNotifier.getUserID(), _otherUserID!);
    if (mounted) {
      Navigator.of(context).pop(); // Hide spinner
      if (success) {
        setState(() => _userStatus = UserStatus.REQUESTSENT); // Optimistic UI update
        _selfProfileNotifier.updateProfile();
      } else {
        showDialog(context: context, builder: (_) => SinglebuttonDialog(title: "Failed", content: "Failed to send request.", onConfirm: () => Navigator.pop(context)));
      }
    }
  }

  Future<void> _handleUnrequestFriend() async {
    _showLoadingDialog();
    bool success = await _friendsController.cancelRequest(_selfProfileNotifier.getUserID(), _otherUserID!);
    if (mounted) {
      Navigator.of(context).pop();
      if (success) {
        setState(() => _userStatus = UserStatus.STRANGER); // Optimistic UI update
        _selfProfileNotifier.updateProfile();
      } else {
        showDialog(context: context, builder: (_) => SinglebuttonDialog(title: "Failed", content: "Failed to cancel request.", onConfirm: () => Navigator.pop(context)));
      }
    }
  }

  Future<void> _handleRemoveFriend() async {
    _showLoadingDialog();
    bool success = await _friendsController.removeFriend(_selfProfileNotifier.getUserID(), _otherUserID!);
    if (mounted) {
      Navigator.of(context).pop();
      if (success) {
        setState(() => _userStatus = UserStatus.STRANGER); // Optimistic UI update
        _selfProfileNotifier.updateProfile();
      } else {
        showDialog(context: context, builder: (_) => SinglebuttonDialog(title: "Failed", content: "Failed to remove friend.", onConfirm: () => Navigator.pop(context)));
      }
    }
  }

  Future<void> _handleAccept() async {
    _showLoadingDialog();
    bool success = await _friendsController.acceptFriend(_selfProfileNotifier.getUserID(), _otherUserID!);
    if (mounted) {
      Navigator.of(context).pop();
      if (success) {
        setState(() => _userStatus = UserStatus.FRIEND); // Optimistic UI update
        _selfProfileNotifier.updateProfile();
      } else {
        showDialog(context: context, builder: (_) => SinglebuttonDialog(title: "Failed", content: "Failed to accept request.", onConfirm: () => Navigator.pop(context)));
      }
    }
  }

  Future<void> _handleReject() async {
    _showLoadingDialog();
    bool success = await _friendsController.rejectFriend(_selfProfileNotifier.getUserID(), _otherUserID!);
    if (mounted) {
      Navigator.of(context).pop();
      if (success) {
        setState(() => _userStatus = UserStatus.STRANGER); // Optimistic UI update
        _selfProfileNotifier.updateProfile();
      } else {
        showDialog(context: context, builder: (_) => SinglebuttonDialog(title: "Failed", content: "Failed to reject request.", onConfirm: () => Navigator.pop(context)));
      }
    }
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

  Widget _buildActionButtons() {
    switch (_userStatus) {
      case UserStatus.FRIEND:
        return SizedBox(
          width: double.infinity,
          child: ElevatedButton.icon(
            icon: const Icon(Icons.person_remove),
            onPressed: () => showDialog(
                context: context,
                builder: (BuildContext context) => DualbuttonDialog(
                    title: "Remove Friend?",
                    content: "You will not see them on your leaderboard anymore.",
                    onConfirm: () {
                      Navigator.of(context).pop();
                      _handleRemoveFriend();
                    },
                    onCancel: () => Navigator.of(context).pop)),
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.red,
              foregroundColor: Colors.white,
              padding: const EdgeInsets.symmetric(vertical: 12),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
            ),
            label: const Text("Remove Friend", style: TextStyle(fontWeight: FontWeight.bold)),
          ),
        );

      case UserStatus.STRANGER:
        return SizedBox(
          width: double.infinity,
          child: ElevatedButton.icon(
              icon: const Icon(Icons.person_add),
              onPressed: _handleRequestFriend,
              style: ElevatedButton.styleFrom(
                backgroundColor: PrimaryColors.darkGreen,
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(vertical: 12),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              ),
              label: const Text("Send Friend Request", style: TextStyle(fontWeight: FontWeight.bold))),
        );

      case UserStatus.REQUESTRECIEVED:
        return Row(
          children: [
            Expanded(
              child: ElevatedButton(
                onPressed: _handleReject,
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.red,
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 12),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                ),
                child: const Text("Reject", style: TextStyle(fontWeight: FontWeight.bold)),
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: ElevatedButton(
                onPressed: _handleAccept,
                style: ElevatedButton.styleFrom(
                  backgroundColor: PrimaryColors.brightGreen,
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 12),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                ),
                child: const Text("Accept", style: TextStyle(fontWeight: FontWeight.bold)),
              ),
            ),
          ],
        );

      case UserStatus.REQUESTSENT:
        return SizedBox(
          width: double.infinity,
          child: ElevatedButton.icon(
              icon: const Icon(Icons.undo),
              onPressed: _handleUnrequestFriend,
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.grey,
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(vertical: 12),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              ),
              label: const Text("Cancel Request", style: TextStyle(fontWeight: FontWeight.bold))),
        );
      default:
        return const SizedBox.shrink();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[100],
      appBar: AppBar(
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: _handleBack,
        ),
        backgroundColor: PrimaryColors.dullGreen,
        foregroundColor: Colors.white,
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _userFound == false
          ? Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.person_off, size: 80, color: Colors.grey[400]),
            const SizedBox(height: 16),
            Text(
              "User Not Found",
              style: GoogleFonts.poppins(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.grey[600]),
            ),
          ],
        ),
      )
          : Column(
        children: [
          Expanded(
            child: ListView(
              padding: EdgeInsets.zero,
              children: [
                Container(
                  padding: const EdgeInsets.only(top: 20, bottom: 20),
                  decoration: const BoxDecoration(
                    color: PrimaryColors.dullGreen,
                    borderRadius: BorderRadius.only(
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
                        style: GoogleFonts.poppins(fontSize: 22, fontWeight: FontWeight.bold, color: Colors.white),
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
              ],
            ),
          ),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 20),
            color: Colors.white,
            child: _buildActionButtons(),
          ),
        ],
      ),
    );
  }
}