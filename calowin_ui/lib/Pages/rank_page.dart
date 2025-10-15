import 'package:calowin/common/colors_and_fonts.dart';
import 'package:calowin/common/user_profile.dart';
import 'package:calowin/control/leaderboard_retriever.dart';
import 'package:calowin/control/page_navigator.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';

class RankPage extends StatefulWidget {
  final String userID;
  const RankPage({super.key,required this.userID});

  @override
  State<RankPage> createState() => _RankPageState();
}

enum LeaderboardType { calories, carbon }

class _RankPageState extends State<RankPage> {
  String userID = "";
  LeaderboardType _selectedType = LeaderboardType.calories;
  LeaderboardRetriever retriever = LeaderboardRetriever();
  List<LeaderboardItem> caloriesleaderBoard = [];
  List<LeaderboardItem> carbonleaderBoard = [];
  late UserProfile _profile;
  bool flag = false;
  // NEW: A state variable to manage the loading spinner.
  bool _isLoading = true;


  // MODIFIED: This function now manages the loading state.
  Future<void> _retrieveLeaderboards() async {
    // Show spinner when this function is called.
    if(mounted) {
      setState(() {
        _isLoading = true;
      });
    }

    try {
      caloriesleaderBoard = await retriever.retrieveCalorieLeaderboard(userID);
      carbonleaderBoard = await retriever.retrieveCarbonLeaderboard(userID);
    } finally {
      // Hide spinner when done, whether it succeeded or failed.
      if(mounted) {
        setState(() {
          _isLoading = false;
        });
      }
    }
  }

  @override
  void initState() {
    super.initState();
    userID = widget.userID;
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    if(flag == false)
    {
      _profile = Provider.of<UserProfile>(context, listen: true);
      _profile.addListener(_retrieveLeaderboards);
      flag = true;
      _retrieveLeaderboards();
    }
  }

  @override
  void dispose() {
    _profile.removeListener(_retrieveLeaderboards);
    super.dispose();
  }

  void _onListItemTap(LeaderboardItem user) {
    final pageNavigatorState =
    context.findAncestorStateOfType<PageNavigatorState>();

    if (pageNavigatorState != null) {
      pageNavigatorState.navigateToPage(5,
          params: {'otherUserID': user.userId,'userID':userID});
    }
  }

  Widget _buildPodiumItem(LeaderboardItem user, int rank) {
    final colors = [
      Color(0xFFFFD700), // Gold
      Color(0xFFC0C0C0), // Silver
      Color(0xFFCD7F32), // Bronze
    ];
    final color = colors[rank - 1];
    final double elevation = rank == 1 ? 80.0 : 40.0;
    final Image? medal = _selectedType == LeaderboardType.calories ? user.calorieMedal : user.carbonMedal;
    final int points = _selectedType == LeaderboardType.calories ? user.caloriePoint : user.carbonPoint;


    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        GestureDetector(
          onTap: () => _onListItemTap(user),
          child: Stack(
            alignment: Alignment.center,
            children: [
              Container(
                width: 80,
                height: 80,
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  border: Border.all(color: color, width: 4),
                ),
                child: const CircleAvatar(
                  backgroundColor: Colors.white,
                  child: Icon(Icons.person, size: 40, color: Colors.grey),
                ),
              ),
              Positioned(
                top: 0,
                left: 0,
                child: Container(
                  width: 30,
                  height: 30,
                  decoration: BoxDecoration(
                    color: color,
                    shape: BoxShape.circle,
                    border: Border.all(color: Colors.white, width: 2),
                  ),
                  child: Center(
                    child: Text(
                      '$rank',
                      style: GoogleFonts.poppins(
                          fontWeight: FontWeight.bold,
                          color: Colors.black87,
                          fontSize: 16),
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
        const SizedBox(height: 8),
        Text(user.name, style: GoogleFonts.poppins(fontWeight: FontWeight.bold, fontSize: 16)),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              points.toString(),
              style: GoogleFonts.poppins(color: Colors.black54, fontSize: 14),
            ),
            if (medal != null)
              SizedBox(height: 20, width: 20, child: medal),
          ],
        ),
        const SizedBox(height: 4),
        Container(
          height: elevation,
          width: 90,
          decoration: BoxDecoration(
            color: color,
            borderRadius: const BorderRadius.vertical(top: Radius.circular(8)),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.3),
                blurRadius: 5,
                offset: const Offset(0, 2),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildRankListItem(int index, LeaderboardItem user) {
    final Image? medal = _selectedType == LeaderboardType.calories ? user.calorieMedal : user.carbonMedal;
    final int points = _selectedType == LeaderboardType.calories ? user.caloriePoint : user.carbonPoint;

    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
      child: ListTile(
        onTap: () => _onListItemTap(user),
        leading: Text(
          "${index + 1}",
          style: GoogleFonts.poppins(
              fontWeight: FontWeight.bold,
              fontSize: 16,
              color: Colors.black54),
        ),
        title: Text(
          user.name,
          style: GoogleFonts.poppins(fontWeight: FontWeight.bold),
        ),
        trailing: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              points.toString(),
              style: GoogleFonts.poppins(
                  fontWeight: FontWeight.bold,
                  fontSize: 16,
                  color: PrimaryColors.darkGreen),
            ),
            if (medal != null) ...[
              const SizedBox(width: 8),
              SizedBox(height: 30, width: 30, child: medal),
            ]
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final bool isCalorie = _selectedType == LeaderboardType.calories;
    final List<LeaderboardItem> activeLeaderboard =
    isCalorie ? caloriesleaderBoard : carbonleaderBoard;

    final List<LeaderboardItem> topThree =
    activeLeaderboard.length > 3 ? activeLeaderboard.sublist(0, 3) : activeLeaderboard;
    final List<LeaderboardItem> restOfList =
    activeLeaderboard.length > 3 ? activeLeaderboard.sublist(3) : [];

    return Scaffold(
      backgroundColor: Colors.grey[100],
      appBar: AppBar(
        centerTitle: true,
        backgroundColor: PrimaryColors.darkGreen,
        foregroundColor: Colors.white,
        title: Text(
          "Leaderboard",
          style: GoogleFonts.poppins(fontWeight: FontWeight.bold),
        ),
        elevation: 0,
        // NEW: An action button to refresh the leaderboard.
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            // Disable the button while loading to prevent multiple requests.
            onPressed: _isLoading ? null : _retrieveLeaderboards,
          ),
        ],
      ),
      body: Column(
        children: [
          Container(
            width: double.infinity,
            padding: const EdgeInsets.all(16.0),
            color: PrimaryColors.darkGreen,
            child: SegmentedButton<LeaderboardType>(
              segments: const [
                ButtonSegment(
                    value: LeaderboardType.calories,
                    label: Text('Calories'),
                    icon: Icon(Icons.local_fire_department)),
                ButtonSegment(
                    value: LeaderboardType.carbon,
                    label: Text('Carbon'),
                    icon: Icon(Icons.eco)),
              ],
              selected: {_selectedType},
              onSelectionChanged: (Set<LeaderboardType> newSelection) {
                setState(() {
                  _selectedType = newSelection.first;
                });
              },
              style: SegmentedButton.styleFrom(
                backgroundColor: PrimaryColors.dullGreen,
                foregroundColor: Colors.white,
                selectedForegroundColor: Colors.white,
                selectedBackgroundColor: PrimaryColors.brightGreen,
              ),
            ),
          ),
          // NEW: The body now shows a spinner while loading.
          Expanded(
            child: _isLoading
                ? const Center(child: CircularProgressIndicator())
                : Column(
              children: [
                const SizedBox(height: 20),
                if (topThree.isNotEmpty)
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    crossAxisAlignment: CrossAxisAlignment.end,
                    children: [
                      if (topThree.length > 1) _buildPodiumItem(topThree[1], 2),
                      if (topThree.length > 0) _buildPodiumItem(topThree[0], 1),
                      if (topThree.length > 2) _buildPodiumItem(topThree[2], 3),
                    ],
                  ),
                const SizedBox(height: 20),
                const Divider(indent: 20, endIndent: 20),
                Expanded(
                  child: ListView.builder(
                    padding: const EdgeInsets.only(bottom: 16),
                    itemCount: restOfList.length,
                    itemBuilder: (context, index) {
                      final user = restOfList[index];
                      return _buildRankListItem(index + 3, user);
                    },
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}