import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:calowin/common/colors_and_fonts.dart';

class InputField extends StatefulWidget {
  final TextEditingController inputController;
  final String title;
  final String inputHint;
  final String? bottomHint;
  final String errorText;
  final bool hasError;
  final TextInputType? keyboardType;
  final List<TextInputFormatter>? inputFormatter;

  const InputField(
      {super.key,
      required this.hasError,
      required this.errorText,
      required this.inputController,
      required this.title,
      required this.inputHint,
      this.bottomHint,
      this.keyboardType,
      this.inputFormatter});

  @override
  State<InputField> createState() => _InputFieldState();
}

class _InputFieldState extends State<InputField> {
  late TextEditingController _inputEmail;
  late String _title;
  late String _inputHint;
  late bool _hasError;
  late String _errorText;
  bool _hasBottomHint = false;
  String? _bottomHint;
  late TextInputType? _keyboardType;
  late List<TextInputFormatter>? _inputFormatter;

  @override
  void initState() {
    super.initState();
    _keyboardType = widget.keyboardType;
    _inputFormatter = widget.inputFormatter;
    _hasError = widget.hasError;
    _errorText = widget.errorText;
    _inputEmail = widget.inputController;
    _title = widget.title;
    _inputHint = widget.inputHint;
    if (widget.bottomHint != null) {
      _bottomHint = widget.bottomHint;
      _hasBottomHint = true;
    }
  }

  @override
  Widget build(BuildContext context) {
    final InputBorder inputBorder = UnderlineInputBorder(
        borderRadius: BorderRadius.circular(8), borderSide: BorderSide.none);

    return SizedBox(
      width: 350,
      child: Column(
        children: [
          Align(
            alignment: Alignment.topLeft,
            child: Text(
              _title,
              style: GoogleFonts.poppins(
                  fontSize: 14,
                  fontWeight: FontWeight.bold,
                  color: Colors.white),
              textAlign: TextAlign.left,
            ),
          ),
          const SizedBox(height: 5),
          SizedBox(
            height: 50,
            child: TextField(
                keyboardType: _keyboardType,
                inputFormatters: _inputFormatter,
                controller: _inputEmail,
                textAlign: TextAlign.left,
                decoration: InputDecoration(
                  filled: true,
                  fillColor: Colors.white,
                  border: inputBorder,
                  enabledBorder: inputBorder,
                  focusedBorder: inputBorder,
                  contentPadding:
                      const EdgeInsets.symmetric(vertical: 5, horizontal: 15),
                  hintText: _inputHint,
                  hintStyle: GoogleFonts.roboto(
                      fontSize: 14, color: PrimaryColors.grey),
                )),
          ),
          const SizedBox(
            height: 5,
          ),
          if (_hasError)
            Align(
              alignment: Alignment.centerLeft,
              child: Text(
                _errorText,
                style: GoogleFonts.roboto(
                  fontSize: 11,
                  color: Colors.redAccent.shade400,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          if (_hasError)
            const SizedBox(
              height: 2,
            ),
          if (_hasBottomHint)
            Align(
              alignment: Alignment.centerLeft,
              child: Text(
                textAlign: TextAlign.left,
                _bottomHint as String,
                style: GoogleFonts.roboto(fontSize: 11, color: Colors.white),
              ),
            )
        ],
      ),
    );
  }
}
