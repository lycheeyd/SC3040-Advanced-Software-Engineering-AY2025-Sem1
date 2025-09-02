package com.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.DataTransferObject.AccountDTO.ChangePasswordDTO;
import com.DataTransferObject.AccountDTO.DeleteAccountDTO;
import com.DataTransferObject.AccountDTO.EditProfileDTO;
import com.DataTransferObject.AccountDTO.ForgotPasswordDTO;
import com.DataTransferObject.AccountDTO.LoginDTO;
import com.DataTransferObject.AccountDTO.SendOtpDTO;
import com.DataTransferObject.AccountDTO.SignupDTO;
import com.DataTransferObject.AccountDTO.VerifyOtpDTO;

@RestController
@RequestMapping("/central/account")
public class AccountController extends HttpReqController{

    public AccountController(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Value("${account.module.urlPrefix}")
    private String urlPrefix;

    // Implemenet your own mapping below

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupDTO DTO) {
        // Forward signup request to AccountModule
        try {
            String url = urlPrefix + "/account/signup"; // URL of Account Java application
            return restTemplate.postForEntity(url, DTO, Map.class);
        } catch (HttpClientErrorException ex) {
            HttpStatusCode statusCode = ex.getStatusCode();
            if (statusCode == HttpStatus.BAD_REQUEST) {
                return ResponseEntity.status(statusCode).body(ex.getMessage());
            } else {
                return ResponseEntity.status(statusCode).body(statusCode + ex.getMessage());
            }
        } catch (Exception ex) {
            System.out.println(ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO DTO) {
        // Forward login request to AccountModule
        try {
            String url = urlPrefix + "/account/login";
            return restTemplate.postForEntity(url, DTO, Map.class);
        } catch (HttpClientErrorException ex) {
            HttpStatusCode statusCode = ex.getStatusCode();
            if (statusCode == HttpStatus.UNAUTHORIZED) {
                return ResponseEntity.status(statusCode).body("Invalid email or password");
            } else {
                return ResponseEntity.status(statusCode).body(statusCode + ex.getMessage());
            }
        } catch (Exception ex)  {
            System.out.println(ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody SendOtpDTO DTO) {
        try {
           String url = urlPrefix + "/account/send-otp";
            return restTemplate.postForEntity(url, DTO, String.class); 
        } catch (Exception ex)  {
            System.out.println(ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpDTO DTO) {
        try {
            String url = urlPrefix + "/account/verify-otp";
            return restTemplate.postForEntity(url, DTO, String.class);
        } catch (HttpClientErrorException ex) {
            HttpStatusCode statusCode = ex.getStatusCode();
            if (statusCode == HttpStatus.UNAUTHORIZED) {
                return ResponseEntity.status(statusCode).body("Invalid OTP");
            } else {
                return ResponseEntity.status(statusCode).body(statusCode + ex.getMessage());
            }
        } catch (Exception ex) {
            // Handle generic exceptions
            System.out.println(ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO DTO) {
        // Forward change password request to AccountModule
        try {
            String url = urlPrefix + "/account/change-password";
            return restTemplate.postForEntity(url, DTO, String.class);
        } catch (HttpClientErrorException ex) {
            HttpStatusCode statusCode = ex.getStatusCode();
            if (statusCode == HttpStatus.UNAUTHORIZED) {
                return ResponseEntity.status(statusCode).body("Incorrect password.");
            } else {
                return ResponseEntity.status(statusCode).body(ex.getMessage());
            }
        } catch (Exception ex)  {
            System.out.println(ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDTO DTO) {
        // Forward forget password request to AccountModule
        try {
            String url = urlPrefix + "/account/forgot-password";
            return restTemplate.postForEntity(url, DTO, String.class);
        } catch (HttpClientErrorException ex) {
            HttpStatusCode statusCode = ex.getStatusCode();
            if (statusCode == HttpStatus.UNAUTHORIZED) {
                return ResponseEntity.status(statusCode).body("Invalid OTP");
            } else {
                return ResponseEntity.status(statusCode).body(statusCode + ex.getMessage());
            }
        } catch (Exception ex)  {
            System.out.println(ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }

    @PostMapping("/edit-profile")
    public ResponseEntity<?> editProfile(@RequestBody EditProfileDTO DTO) {
        // Forward edit profile request to AccountModule
        try {
            String url = urlPrefix + "/account/edit-profile";
            return restTemplate.postForEntity(url, DTO, Map.class);
        } catch (HttpClientErrorException ex) {
            HttpStatusCode statusCode = ex.getStatusCode();
            if (statusCode == HttpStatus.BAD_REQUEST) {
                return ResponseEntity.status(statusCode).body(ex.getMessage());
            } else {
                return ResponseEntity.status(statusCode).body(statusCode + ex.getMessage());
            }
        } catch (Exception ex)  {
            System.out.println(ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }

    @PostMapping("/delete-account")
    public ResponseEntity<?> deleteAccount(@RequestBody DeleteAccountDTO DTO) {
        // Forward delete account request to AccountModule
        try {
            String url = urlPrefix + "/account/delete-account";
            return restTemplate.postForEntity(url, DTO, String.class);
        } catch (HttpClientErrorException ex) {
            HttpStatusCode statusCode = ex.getStatusCode();
            if (statusCode == HttpStatus.UNAUTHORIZED) {
                return ResponseEntity.status(statusCode).body("Invalid OTP");
            } else {
                return ResponseEntity.status(statusCode).body(ex.getMessage());
            }
        } catch (Exception ex)  {
            System.out.println(ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }

    @GetMapping("/view-profile/{selfID}/{otherID}")
    public ResponseEntity<?> viewProfile(@PathVariable String selfID, @PathVariable String otherID) {
        // Forward view profile request to AccountModule
        try {
            String url = urlPrefix + "/account/view-profile/" + selfID + "/" + otherID;
            return restTemplate.getForEntity(url, Map.class);
        } catch (Exception ex)  {
            System.out.println(ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }

    @GetMapping("/view-profile/{selfID}")
    public ResponseEntity<?> viewProfile(@PathVariable String selfID) {
        // Forward view self profile request to AccountModule
        try {
            String url = urlPrefix + "/account/view-profile/" + selfID;
            return restTemplate.getForEntity(url, Map.class);
        } catch (Exception ex)  {
            System.out.println(ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }
    
}

