package com.Account.Services;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Account.Entities.OTPEntry;
import com.Account.Entities.ActionType;
import com.Database.CalowinSecureDB.OTPRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional("calowinSecureDBTransactionManager")
public class OTPService {

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private EmailService emailService;

    // Generate and store OTP with 1-day expiry
    private String generateAndSaveOTP(String email, ActionType type) {
        // Generate 6-digit OTP
        String otpCode = RandomStringUtils.randomNumeric(6);

        // Set the expiration time (1 day from now)
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);

        // Check if the OTP of action type already exists for user, then delete
        otpRepository.findByEmailAndOtpType(email, type)
                        .ifPresent(existingOtp -> otpRepository.delete(existingOtp));

        // Save new OTP to database (CALOWIN_SECURE)
        OTPEntry otpEntity = new OTPEntry(email, otpCode, expiresAt, type);
        otpRepository.save(otpEntity);

        return otpCode;
    }

    // Send otpCode to user
    public void sendOtpCode(String email, ActionType type) throws Exception {
        // Generate OTP
        String otpCode = generateAndSaveOTP(email, type);

        // Send OTP
        emailService.sendEmail(email, type.getSubject(), type.getMessageBody(otpCode));
    }

    // Verify if the OTP is valid (not expired and matches)
    public boolean verifyOTP(String email, String otpCode, ActionType type) {
        Optional<OTPEntry> otpEntityOptional = otpRepository.findByEmailAndOtpType(email, type);

        if (otpEntityOptional.isPresent()) {
            OTPEntry otpEntity = otpEntityOptional.get();

            // Check if the OTP has expired
            if (otpEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
                // Clean up expired OTP
                otpRepository.deleteByEmailAndOtpType(email, type); 
                return false;
            }

            // Check if OTP matches
            if (otpEntity.getOtpCode().equals(otpCode)) {
                // Delete OTP entry after verification
                otpRepository.deleteByEmailAndOtpType(email, type);
                return true;
            }
        }

        // No OTP found for the user
        return false;
    }

    // Runs every day at midnight to delete expired OTPs
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanUpExpiredOTPs() {
        otpRepository.findAll().forEach(otp -> {
            if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
                otpRepository.delete(otp);
            }
        });
    }

}

