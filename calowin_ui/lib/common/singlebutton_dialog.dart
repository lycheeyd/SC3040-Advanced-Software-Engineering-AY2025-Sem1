import 'package:flutter/material.dart';
import 'package:calowin/common/colors_and_fonts.dart';

class SinglebuttonDialog extends StatelessWidget {
  final String title;
  final String content;
  final VoidCallback onConfirm;

  const SinglebuttonDialog({
    super.key,
    required this.title,
    required this.content,
    required this.onConfirm,
  });

  @override
  Widget build(BuildContext context) {
    return Dialog(
      backgroundColor: Colors.grey.shade300,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(15),
      ),
      elevation: 16,
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              title,
              style: const TextStyle(fontSize: 20, fontWeight: FontWeight.w600),
            ),
            const SizedBox(height: 15),
            Align(
              alignment: Alignment.centerLeft,
              child: Text(
                content,
                style: const TextStyle(fontSize: 15),
              ),
            ),
            const SizedBox(height: 20),
              Center(
                child: SizedBox(
                    height: 40,
                    width: 130,
                    child: ElevatedButton(
                      style: ElevatedButton.styleFrom(
                        elevation: 0,
                        foregroundColor: Colors.white,
                        backgroundColor: PrimaryColors.darkGreen,
                        shape: RoundedRectangleBorder(
                          borderRadius:
                              BorderRadius.circular(10), // Rounded corners
                        ),
                      ),
                      onPressed: onConfirm,
                      child: const Text("Yes"),
                    ),
                  ),
              ),
          ],
        ),
      ),
    );
  }
}
