import 'package:calowin/Pages/login_page.dart';
import 'package:calowin/common/user_profile.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

void main() {
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => UserProfile(name: "NA",userID: "NA")),
      ],
      child: MyApp(),
    ),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return  MaterialApp(
      title: 'CaloWin',
      debugShowCheckedModeBanner: false,
      home: Loginpage(),
      initialRoute: '/',
      routes: {
        '/login': (context) => const Loginpage(),}
    );
  }
}
//
