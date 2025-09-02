import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:calowin/common/colors_and_fonts.dart';

class InputDialog extends StatelessWidget {
  final String title;
  final String content;
  final ValueChanged<String> onConfirm;
  final VoidCallback onCancel;
  final String? confirmButtonText;
  final String hintText;

  const InputDialog({
    super.key,
    required this.title,
    required this.content,
    required this.onConfirm,
    required this.onCancel,
    required this.hintText,
    this.confirmButtonText,
  });

  @override
  Widget build(BuildContext context) {
    final inputBorder = OutlineInputBorder(
        borderSide: BorderSide.none, borderRadius: BorderRadius.circular(8));

    final TextEditingController inputController = TextEditingController();
    return Dialog(
      backgroundColor: Colors.grey.shade300,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(15),
      ),
      elevation: 16,
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              title,
              style: GoogleFonts.roboto(
                  color: PrimaryColors.black,
                  fontWeight: FontWeight.bold,
                  fontSize: 16),
            ),
            const SizedBox(
              height: 10,
            ),
            Text(
              content,
              style:
                  GoogleFonts.roboto(color: Colors.grey.shade800, fontSize: 14),
            ),
            const SizedBox(
              height: 10,
            ),
            SizedBox(
                height: 40,
                child: TextField(
                  controller: inputController,
                  decoration: InputDecoration(
                    filled: true,
                    fillColor: Colors.white,
                    contentPadding:
                        const EdgeInsets.symmetric(vertical: 5, horizontal: 15),
                    hintText: hintText,
                    hintStyle: GoogleFonts.roboto(
                        fontSize: 16, color: PrimaryColors.grey),
                    enabledBorder: inputBorder,
                    border: inputBorder,
                    focusedBorder: inputBorder,
                  ),
                )),
            const SizedBox(
              height: 15,
            ),
            Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                SizedBox(
                  width: 130,
                  height: 40,
                  child: ElevatedButton(
                      onPressed: onCancel,
                      style: ElevatedButton.styleFrom(
                        elevation: 0,
                        backgroundColor: Colors.white,
                        padding: const EdgeInsets.symmetric(
                            horizontal: 10, vertical: 5),
                        shape: RoundedRectangleBorder(
                          borderRadius:
                              BorderRadius.circular(10), // Rounded corners
                        ),
                      ),
                      child: Text(
                        "Cancel",
                        style: GoogleFonts.roboto(
                            fontSize: 16, color: Colors.black),
                      )),
                ),
                SizedBox(
                  width: 130,
                  height: 40,
                  child: ElevatedButton(
                      onPressed: () {
                        onConfirm(inputController.text);
                      },
                      style: ElevatedButton.styleFrom(
                        elevation: 0,
                        backgroundColor: Colors.black,
                        padding: const EdgeInsets.symmetric(
                            horizontal: 10, vertical: 5),
                        shape: RoundedRectangleBorder(
                          borderRadius:
                              BorderRadius.circular(10), // Rounded corners
                        ),
                      ),
                      child: Text(
                        confirmButtonText ?? "Confirm",
                        style: GoogleFonts.roboto(
                            fontSize: 16, color: Colors.white),
                      )),
                )
              ],
            )
          ],
        ),
      ),
    );
  }
}
