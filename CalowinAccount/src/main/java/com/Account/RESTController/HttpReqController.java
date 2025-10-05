package com.Account.RESTController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.Account.Entities.ProfileEntity;
import com.Account.Managers.AccountManagementService;
import com.Account.Managers.PasswordManagementService;
import com.Account.Managers.ProfileManagementService;
import com.Account.Services.OTPService;
import com.DataTransferObject.ChangePasswordDTO;
import com.DataTransferObject.DeleteAccountDTO;
import com.DataTransferObject.EditProfileDTO;
import com.DataTransferObject.ForgotPasswordDTO;
import com.DataTransferObject.LoginDTO;
import com.DataTransferObject.LoginResponseDTO;
import com.DataTransferObject.SendOtpDTO;
import com.DataTransferObject.SignupDTO;
import com.DataTransferObject.VerifyOtpDTO;
import com.DataTransferObject.ViewProfileResponseDTO;

@RestController
@RequestMapping("/central/account")
public class HttpReqController {

    protected final RestTemplate restTemplate;

    protected HttpReqController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    private AccountManagementService accountManagementService;

    @Autowired
    private PasswordManagementService passwordManagementService;

    @Autowired
    private ProfileManagementService profileManagementService;

    @Autowired
    private OTPService otpService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupDTO signupDTO) {
        // Signup logic (save user details to DB)
        try {
            LoginResponseDTO responseDTO = accountManagementService.signup(signupDTO.getEmail(),
                    signupDTO.getPassword(), signupDTO.getConfirm_password(), signupDTO.getName(),
                    signupDTO.getWeight());

            // Prepare response after successful signup
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Signup successful");
            response.put("UserObject", responseDTO);

            System.out.println(response);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Signup failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during signup: " + e.getMessage());
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        // Login logic (check username/password)
        try {
            LoginResponseDTO responseDTO = accountManagementService.login(loginDTO.getEmail(), loginDTO.getPassword());

            // Prepare response after successful login
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("UserObject", responseDTO);

            System.out.println(response);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Return unauthorized error for invalid credentials
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during login: " + e.getMessage());
        }

    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody SendOtpDTO sendOtpDTO) {
        try {
            // Send the OTP
            otpService.sendOtpCode(sendOtpDTO.getEmail(), sendOtpDTO.getType());

            return ResponseEntity.ok("OTP sent to email associate with the account");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending OTP: " + e.getMessage());
        }

    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpDTO verifyOtpDTO) {
        try {
            // Verify the OTP
            if (!otpService.verifyOTP(verifyOtpDTO.getEmail(), verifyOtpDTO.getOtpCode(), verifyOtpDTO.getType())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
            }

            return ResponseEntity.ok("OTP valid");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error validating OTP: " + e.getMessage());
        }

    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        // Change password logic (update password in DB)
        try {
            passwordManagementService.changePassword(changePasswordDTO.getUserID(), changePasswordDTO.getOldPassword(),
                    changePasswordDTO.getNewPassword(), changePasswordDTO.getConfirm_newPassword());

            return ResponseEntity.ok("Password changed successfully");

        } catch (RuntimeException e) {
            // Return unauthorized error for invalid credentials
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }

    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        // Forget password logic
        try {
            passwordManagementService.forgotPassword(forgotPasswordDTO.getEmail(), forgotPasswordDTO.getOtpCode());

            return ResponseEntity.ok("New password is sent to your email");

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending new password: " + e.getMessage());
        }

    }

    @PostMapping("/edit-profile")
    public ResponseEntity<?> editProfile(@RequestBody EditProfileDTO editProfileDTO) {
        // Edit account logic
        try {
            System.out.println(editProfileDTO.getUserID());
            ProfileEntity profile = profileManagementService.editProfile(editProfileDTO.getUserID(),
                    editProfileDTO.getName(), editProfileDTO.getWeight(), editProfileDTO.getBio());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Profile updated successfully");
            response.put("UserObject", profile);

            System.out.println(response);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Return unauthorized error for invalid credentials
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }

    }

    @PostMapping("/delete-account")
    public ResponseEntity<?> deleteAccount(@RequestBody DeleteAccountDTO deleteAccountDTO) {
        // Delete account logic
        try {
            accountManagementService.deleteAccount(deleteAccountDTO.getUserID(), deleteAccountDTO.getEmail(),
                    deleteAccountDTO.getOtpCode());

            System.out.println("Account deleted");
            return ResponseEntity.ok("Account deleted");

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting account: " + e.getMessage());
        }

    }

    @GetMapping("/view-profile/{selfID}/{otherID}")
    public ResponseEntity<?> viewProfile(@PathVariable String selfID, @PathVariable String otherID) {
        // View account logic
        try {
            ViewProfileResponseDTO profile = profileManagementService.viewProfile(selfID, otherID);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Profile retrieved successfully");
            response.put("UserObject", profile);

            System.out.println(response);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Return unauthorized error for invalid credentials
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/view-profile/{selfID}")
    public ResponseEntity<?> viewProfile(@PathVariable String selfID) {
        // View account logic
        try {
            LoginResponseDTO profile = profileManagementService.viewProfile(selfID);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Self Profile retrieved successfully");
            response.put("UserObject", profile);

            System.out.println(response);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Return unauthorized error for invalid credentials
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

}