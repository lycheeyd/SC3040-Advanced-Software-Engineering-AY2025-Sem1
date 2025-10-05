package com.Account.Managers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import com.Account.Entities.ActionType;
import com.Account.Entities.UserEntity;
import com.Account.Services.EmailService.EmailService;
import com.Account.Services.OTPService;
import com.Account.Services.PasswordSecurityService;
import com.Database.CalowinSecureDB.SecureInfoDBRepository;

@Service
public class PasswordManagementService {

    @Autowired
    @Qualifier("calowinSecureDBTransactionManager")
    private PlatformTransactionManager calowinSecureDBTransactionManager;

    @Autowired
    private SecureInfoDBRepository secureInfoRepository;

    @Autowired
    private PasswordSecurityService passwordSecurityService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OTPService otpService;

    @Value("${aes.secret-key}")
    private String SECRET_KEY;

    // Change password method
    public void changePassword(String userid, String oldPassword, String newPassword, String confirmNewPassword)
            throws Exception {
        // Check if user exist
        UserEntity user = secureInfoRepository.findByUserID(userid)
                .orElseThrow(() -> new RuntimeException("Invalid user"));

        String decryptedOldPassword = passwordSecurityService.decrypt(oldPassword, SECRET_KEY);
        String decryptedNewPassword = passwordSecurityService.decrypt(newPassword, SECRET_KEY);
        String decryptedConfirmNewPassword = passwordSecurityService.decrypt(confirmNewPassword, SECRET_KEY);

        // Check if new password meet requirements
        passwordSecurityService.isPasswordValid(decryptedNewPassword, decryptedConfirmNewPassword);

        // Authenticate old password
        if (!passwordEncoder.matches(decryptedOldPassword, user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        // Update new password into database (CALOWIN_SECURE)
        user.setPassword(passwordEncoder.encode(decryptedNewPassword));
        secureInfoRepository.save(user);

    }

    // Forgot password method
    public void forgotPassword(String email, String otpCode) throws Exception {
        UserEntity user = secureInfoRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid user"));

        // Authenticate OTP
        if (!otpService.verifyOTP(email, otpCode, ActionType.FORGOT_PASSWORD)) {
            throw new RuntimeException("Invalid OTP");
        }

        // Generate new password and save to database
        String newPassword = passwordSecurityService.generateRandomPassword();

        user.setPassword(passwordEncoder.encode(newPassword));
        secureInfoRepository.save(user);

        // Send new password to email
        ActionType type = ActionType.SEND_NEW_PASSWORD;
        emailService.sendEmail(email, type.getSubject(), type.getMessageBody(newPassword));

    }

}
