import 'package:flutter/material.dart';

class CustomScaffold extends StatefulWidget {
  final Widget body;
  const CustomScaffold({super.key, required this.body});

  @override
  State<CustomScaffold> createState() => _CustomScaffoldState();
}

class _CustomScaffoldState extends State<CustomScaffold> {
  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () {
        // Dismiss the keyboard when tapping outside
        FocusScope.of(context).unfocus();
      },
      child: widget.body,
    );
  }
}
