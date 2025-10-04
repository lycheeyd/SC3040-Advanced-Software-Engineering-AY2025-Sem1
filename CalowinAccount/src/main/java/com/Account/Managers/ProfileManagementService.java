package com.Account.Managers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import com.Account.RESTController.ExternalServiceClient;
import com.Account.Entities.AchievementEntry;
import com.Account.Entities.FriendStatus;
import com.Account.Entities.ProfileEntity;
import com.Account.Entities.UserEntity;
import com.DataTransferObject.LoginResponseDTO;
import com.DataTransferObject.ViewProfileResponseDTO;
import com.Database.CalowinDB.AchievementRepository;
import com.Database.CalowinDB.UserInfoRepository;
import com.Database.CalowinSecureDB.SecureInfoDBRepository;

@Service
public class ProfileManagementService {

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
    private ExternalServiceClient externalServiceController;

    // Edit account method
    public ProfileEntity editProfile(String userID, String name, float weight, String bio) throws Exception {
        ProfileEntity profile = userInfoRepository.findByUserID(userID)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        profile.updateProfile(name, weight, bio);

        userInfoRepository.save(profile);

        return profile;

    }

    // View other's account method
    public ViewProfileResponseDTO viewProfile(String selfID, String otherID) throws Exception {
        ProfileEntity profile = userInfoRepository.findByUserID(otherID)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        AchievementEntry achievement = achievementRepository.findByUserID(profile.getUserID())
                .orElseThrow(() -> new Exception("Failed to retrieve achievement data"));
        
        // Get friend status from external service
        FriendStatus friendStatus = externalServiceController.getFriendStatus(selfID, otherID);

        return new ViewProfileResponseDTO(profile.getUserID(), profile.getName(), profile.getBio(), friendStatus,
                                        achievement.getTotalCarbonSaved(), achievement.getTotalCalorieBurnt(), achievement.getCarbonMedal(), achievement.getCalorieMedal());

    }

    // View self account method
    public LoginResponseDTO viewProfile(String selfID) throws Exception {
        UserEntity user = secureInfoRepository.findByUserID(selfID)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        ProfileEntity profile = userInfoRepository.findByUserID(selfID)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        AchievementEntry achievement = achievementRepository.findByUserID(profile.getUserID())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve achievement data"));
        
        return new LoginResponseDTO(user.getUserID(), user.getEmail(), profile.getName(), profile.getWeight(), profile.getBio(), 
                                achievement.getTotalCarbonSaved(), achievement.getTotalCalorieBurnt(), achievement.getCarbonMedal(), achievement.getCalorieMedal());

    }

}
