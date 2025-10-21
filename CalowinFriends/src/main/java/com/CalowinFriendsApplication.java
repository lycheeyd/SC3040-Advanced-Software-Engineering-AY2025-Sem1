package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//
@SpringBootApplication
public class CalowinFriendsApplication {

    // @Autowired
    // private FriendRelationshipService friendRelationshipService;

    public static void main(String[] args) {
        SpringApplication.run(CalowinFriendsApplication.class, args);
    }

    // @Override
    // public void run(String... args) {
    // testDatabaseConnection();
    // testLeaderboardFunctionality();
    // // testFriendFunctionality();
    // }

    // private void testDatabaseConnection() {
    // try (Connection connection = dataSource.getConnection()) {
    // if (connection != null) {
    // System.out.println("Connection successful.");
    // } else {
    // System.out.println("Connection failed.");
    // }
    // } catch (SQLException e) {
    // System.out.println("Connection error: " + e.getMessage());
    // }
    // }

    // private void testLeaderboardFunctionality() {
    // System.out.println("Carbon Leaderboard:");
    // List<Achievement> carbonLeaderboard =
    // leaderboardService.getCarbonLeaderboard("00000001");
    // carbonLeaderboard.forEach(a -> System.out.println(
    // "User ID: " + a.getUserId() +
    // ", Total Carbon Saved: " + a.getTotalCarbonSaved() +
    // ", Carbon Medal: " + a.getCarbonMedal() +
    // ", Calorie Medal: " + a.getCalorieMedal()
    // ));

    // System.out.println("\nCalories Leaderboard:");
    // List<Achievement> caloriesLeaderboard =
    // leaderboardService.getCaloriesLeaderboard("00000001");
    // caloriesLeaderboard.forEach(a -> System.out.println(
    // "User ID: " + a.getUserId() +
    // ", Total Calories Burnt: " + a.getTotalCalorieBurnt() +
    // ", Carbon Medal: " + a.getCarbonMedal() +
    // ", Calorie Medal: " + a.getCalorieMedal()
    // ));
    // }

    // private void testFriendFunctionality() {
    // // Test sending a friend request
    // try {
    // friendRelationshipService.sendFriendRequest("user4", "user2");
    // System.out.println("Friend request sent from user1 to user2.");
    // } catch (IllegalArgumentException e) {
    // System.out.println("Failed to send friend request: " + e.getMessage());
    // }

    // Test responding to a friend request
    // try {
    // friendRelationshipService.respondToRequest("user1", "user2","ACCEPTED");
    // System.out.println("Friend request accepted.");
    // } catch (Exception e) {
    // System.out.println("Failed to respond to friend request: " + e.getMessage());
    // }

    // // Test fetching pending requests
    // try {
    // List<FriendRelationship> pendingRequests =
    // friendRelationshipService.getPendingRequests("user2");
    // System.out.println("Pending requests for user2: " + pendingRequests.size());
    // } catch (Exception e) {
    // System.out.println("Failed to fetch pending requests: " + e.getMessage());
    // }
    // }
}