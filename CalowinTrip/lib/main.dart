import 'package:calowin/Pages/login_page.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      title: 'CaloWin',
      debugShowCheckedModeBanner: false,
      home: Loginpage(),
      // initialRoute: '/',
      // routes: {
      //   '/login': (context) => const Loginpage(),
      //   '/signup': (context) => const SignupPage(),
      //   '/home': (context) => const PageNavigator(
      //         startPage: 0,
      //       ),
      //   '/profile': (context) => const ProfilePage(),
      //   '/editprofile': (context) => const EditprofilePage()
      //   },
    );
  }
}
