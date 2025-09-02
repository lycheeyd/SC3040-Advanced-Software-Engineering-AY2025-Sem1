import 'package:flutter/services.dart';

class MaxLinesInputFormatter extends TextInputFormatter {
  final int maxLines;

  MaxLinesInputFormatter({required this.maxLines});

  @override
  TextEditingValue formatEditUpdate(
      TextEditingValue oldValue, TextEditingValue newValue) {
    // Split the new text into lines
    final lines = newValue.text.split('\n');

    // Initialize a list to accumulate lines
    final modifiedLines = <String>[];

    for (var line in lines) {
      // While the line exceeds the character limit, split it
      while (line.length > 35) {
        modifiedLines.add(line.substring(0, 35));
        line = line.substring(35);
      }
      modifiedLines.add(line);
    }

    // Combine the lines into a single text with newline characters
    String modifiedText = modifiedLines.join('\n');

    // Enforce max lines by trimming the text if needed
    final finalLines = modifiedText.split('\n');

    if (finalLines.length > maxLines) {
      final trimmedText = finalLines.sublist(0, maxLines).join('\n');
      return TextEditingValue(
        text: trimmedText,
        selection: TextSelection.fromPosition(
          TextPosition(offset: trimmedText.length),
        ),
      );
    }

    return TextEditingValue(
      text: modifiedText,
      selection: TextSelection.fromPosition(
        TextPosition(offset: modifiedText.length),
      ),
    );
  }
}