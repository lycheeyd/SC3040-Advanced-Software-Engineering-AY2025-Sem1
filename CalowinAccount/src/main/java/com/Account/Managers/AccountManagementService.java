package com.Account.Managers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import com.Account.Entities.AchievementEntry;
import com.Account.Entities.ActionType;
import com.Account.Entities.ProfileEntity;
import com.Account.Entities.UserEntity;
import com.Account.Services.OTPService;
import com.Account.Services.PasswordSecurityService;
import com.DataTransferObject.LoginResponseDTO;
import com.Database.CalowinDB.AchievementRepository;
import com.Database.CalowinDB.FriendRelationshipRepository;
import com.Database.CalowinDB.TripsRepository;
import com.Database.CalowinDB.UserInfoRepository;
import com.Database.CalowinSecureDB.SecureInfoDBRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.RandomStringUtils;


@Service
public class AccountManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountManagementService.class);
    
    @Autowired
    @Qualifier("calowinSecureDBTransactionManager")
    private PlatformTransactionManager calowinSecureDBTransactionManager;

    @Autowired
    @Qualifier("calowinDBTransactionManager")
    private PlatformTransactionManager calowinDBTransactionManager;

    @Autowired
    private SecureInfoDBRepository secureInfoRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private TripsRepository tripsRepository;

    @Autowired
    private FriendRelationshipRepository friendRelationshipRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordSecurityService passwordSecurityService;

    @Autowired
    private OTPService otpService;

    @Value("${aes.secret-key}")
    private String SECRET_KEY;

    // Signup method
    @Transactional // (transactionManager = "calowinSecureDBTransactionManager")
    public LoginResponseDTO signup(String email, String encryptedPassword, String encryptedConfirmPassword, String name, float weight) throws Exception {
        // Check if user exist
        if (secureInfoRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        String decryptedPassword = passwordSecurityService.decrypt(encryptedPassword, SECRET_KEY);
        String decryptedConfirmPassword = passwordSecurityService.decrypt(encryptedConfirmPassword, SECRET_KEY);

        // Check if password meet requirements
        passwordSecurityService.isPasswordValid(decryptedPassword, decryptedConfirmPassword);
        
        // Generate userID
        String userID = generateUniqueUserId();

        // Create and store user credentials in database (CALOWIN_SECURE)
        UserEntity user = new UserEntity(userID, email, passwordEncoder.encode(decryptedConfirmPassword));
        secureInfoRepository.save(user);

        // Create and store user info in database (CALOWIN)
        ProfileEntity profile = new ProfileEntity(userID, name, weight, "");
        userInfoRepository.save(profile);

        // Create and initialise default empty entry in database (CALOWIN - Achievement Table)
        AchievementEntry achievement = new AchievementEntry(userID, 0, 0, "No Medal", "No Medal");
        achievementRepository.save(achievement);

        // Prepare and returns user data to frontend
        return new LoginResponseDTO(user.getUserID(), user.getEmail(), profile.getName(), profile.getWeight(), profile.getBio(), 
                                    achievement.getTotalCarbonSaved(), achievement.getTotalCalorieBurnt(), achievement.getCarbonMedal(), achievement.getCalorieMedal());

    }

    // Login method
    public LoginResponseDTO login(String email, String encryptedPassword) throws Exception {
        String decryptedPassword = passwordSecurityService.decrypt(encryptedPassword, SECRET_KEY);

        UserEntity user = secureInfoRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(decryptedPassword, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        ProfileEntity profile = userInfoRepository.findByUserID(user.getUserID())
                .orElseThrow(() -> new Exception("Failed to retrieve user data"));

        AchievementEntry achievement = achievementRepository.findByUserID(user.getUserID())
                .orElseThrow(() -> new Exception("Failed to retrieve achievement data"));

        return new LoginResponseDTO(user.getUserID(), user.getEmail(), profile.getName(), profile.getWeight(), profile.getBio(), 
                                    achievement.getTotalCarbonSaved(), achievement.getTotalCalorieBurnt(), achievement.getCarbonMedal(), achievement.getCalorieMedal());
    
    }

    // Delete account method
    @Transactional (transactionManager = "chainedTransactionManager", rollbackFor = Exception.class)
    public void deleteAccount(String userID, String email, String otpCode) throws Exception {
        LOGGER.info("Starting account deletion for userID: {}", userID);
        
        try {
            // Authenticate OTP
            if (!otpService.verifyOTP(email, otpCode, ActionType.DELETE_ACCOUNT)) {
                throw new RuntimeException("Invalid OTP");
            }

            // Delete from CalowinSecureDB
            LOGGER.info("Deleting from SecureInfoRepository...");
            secureInfoRepository.deleteByUserID(userID); //UserEntity

            // Delete from CalowinDB
            LOGGER.info("Deleting from UserInfoRepository...");
            userInfoRepository.deleteByUserID(userID); //ProfileEntity

            LOGGER.info("Deleting from AchievementRepository...");
            achievementRepository.deleteByUserID(userID); // AchievementEntry
            
            LOGGER.info("Deleting from TripsRepository...");
            tripsRepository.deleteByUserID(userID); //TripsEntry

            LOGGER.info("Deleting from FriendRelationshipRepository...");
            friendRelationshipRepository.deleteByUserID(userID); //FriendRelationshipEntry

            // ADD MORE FOR EACH TABLE 
            LOGGER.info("Successfully deleted all associated records for userID: {}", userID);
        } catch (Exception e) {
            LOGGER.error("Error during account deletion for userID: {}, rolling back. Reason: {}", userID, e.getMessage());
            throw e; // Trigger rollback
        }
        
    }

    // Method to generate a unique 8-character userID
    private String generateUniqueUserId() {
        String userID;
        boolean exists;
    
        // Loop until a unique userID is generated
        do {
            // Generate random 8-character alphanumeric string (both letters and numbers)
            userID = RandomStringUtils.randomAlphanumeric(8).toUpperCase();;
            // Check if the generated userID already exists in the database
            exists = secureInfoRepository.existsByUserID(userID);
        } while (exists);
    
        return userID;
    }

}
