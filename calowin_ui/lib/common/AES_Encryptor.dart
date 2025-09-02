import 'dart:convert';
import 'dart:typed_data';
import 'package:encrypt/encrypt.dart';
import 'package:crypto/crypto.dart';

class AES_Encryptor {
  static const String _key = "hd8Hd7K8djHY8dh4"; // Ensure this key is 16 characters for AES-128

  /// Encrypts a given plaintext using AES-CBC with PKCS7 padding.
  static String encrypt(String plainText) {
    final key = _generateKeyFromPassword(_key);
    final iv = IV.fromLength(16); // Using a 16-byte IV

    final encrypter = Encrypter(AES(key, mode: AESMode.cbc, padding: 'PKCS7'));

    final encrypted = encrypter.encrypt(plainText, iv: iv);
    return '${encrypted.base64}:${iv.base64}';
  }

  /// Generates an AES key from a password.
  static Key _generateKeyFromPassword(String password) {
    final keyBytes = utf8.encode(password);
    final hashedBytes = sha256.convert(keyBytes).bytes;
    return Key(Uint8List.fromList(hashedBytes.sublist(0, 16)));
  }
}
