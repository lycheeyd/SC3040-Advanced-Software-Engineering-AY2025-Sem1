package com.Account.Services;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service
public class PasswordSecurityService {

    public String decrypt(String encrypted, String SECRET_KEY) throws Exception {
        String[] parts = encrypted.split(":");
        byte[] iv = Base64.getDecoder().decode(parts[1]);
        byte[] encryptedBytes = Base64.getDecoder().decode(parts[0]);

        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        keyBytes = sha.digest(keyBytes);
        byte[] truncatedKey = new byte[16];
        System.arraycopy(keyBytes, 0, truncatedKey, 0, 16);

        SecretKeySpec secretKey = new SecretKeySpec(truncatedKey, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

        byte[] original = cipher.doFinal(encryptedBytes);
        return new String(original, StandardCharsets.UTF_8);
    }

    // Utility function to validate the password
    public void isPasswordValid(String password, String confirmPassword) throws Exception {
        // At least 8 characters, 1 digit, 1 uppercase, 1 lowercase, and 1 special character
        String passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=!])(?=.{8,}).*$";
        
        if (!password.equals(confirmPassword)) {
            throw new RuntimeException("Password does not match");
        }

        if (!password.matches(passwordPattern)) {
            throw new RuntimeException("Invalid password. Password must have at least 8 characters, 1 uppercase, and 1 special character");
        }
    }  

    public String generateRandomPassword() {
        // Max length of generated password
        int maxLength = 12; // Max allowed by SQL = 16
        
        // Randomly decide how many special characters to include, ensuring the total length does not exceed maxLength
        int specialCharCount = (int) (Math.random() * (5 - 1)) + 1;  // At most 5 and at least 1 special character
        
        // Generate alphanumeric characters to fill the remaining length
        int alphanumericLength = maxLength - specialCharCount;
        String alphanumeric = RandomStringUtils.randomAlphanumeric(alphanumericLength);
        
        // Generate the specified number of special characters
        String specialChars = RandomStringUtils.random(specialCharCount, "!@#$%^&*()-_=+<>?");
        
        // Combine alphanumeric and special characters
        String combined = alphanumeric + specialChars;

        // Shuffle the combined string to randomize the position of special characters
        List<Character> characters = new ArrayList<>();
        for (char c : combined.toCharArray()) {
            characters.add(c);
        }
        Collections.shuffle(characters);

        // Build the final shuffled string
        StringBuilder finalString = new StringBuilder();
        for (char c : characters) {
            finalString.append(c);
        }

        return finalString.toString();
    }

}
