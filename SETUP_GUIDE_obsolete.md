# Calowin Application Setup Guide

## Overview
This guide will help you set up the Calowin application with both backend (Spring Boot) and frontend (Flutter) services running locally.

## Prerequisites
- Java 17 or higher
- Maven 3.6+
- Flutter SDK 3.0+
- Node.js (for web development)
- Git

## Architecture
- **Backend**: 6 Spring Boot microservices (all on port 7860)
- **Frontend**: Flutter web application (port 3000)
- **Database**: SQL Server (configured in application.properties)

## Step-by-Step Setup

### 1. Backend Services Setup

#### Port Configuration Issue
**IMPORTANT**: All backend services are configured to use port 7860, which causes conflicts. You need to configure different ports for each service.

#### Configure Individual Service Ports

1. **CalowinAccount** (Port 7861):
```bash
cd CalowinAccount
# Edit src/main/resources/application.properties
# Add: server.port=7861
```

2. **CalowinFriends** (Port 7862):
```bash
cd CalowinFriends
# Edit src/main/resources/application.properties
# Add: server.port=7862
```

3. **CalowinNotification** (Port 7863):
```bash
cd CalowinNotification
# Edit src/main/resources/application.properties
# Add: server.port=7863
```

4. **CalowinSpringNode** (Port 7860 - Main Gateway):
```bash
cd CalowinSpringNode
# Keep as server.port=7860 (main gateway)
```

5. **CalowinTrip** (Port 7864):
```bash
cd CalowinTrip
# Edit src/main/resources/application.properties
# Add: server.port=7864
```

6. **CalowinWellnessZone** (Port 7865):
```bash
cd CalowinWellnessZone
# Edit src/main/resources/application.properties
# Add: server.port=7865
```

#### Start Backend Services
```bash
# Terminal 1 - Account Service
cd CalowinAccount && mvn spring-boot:run

# Terminal 2 - Friends Service  
cd CalowinFriends && mvn spring-boot:run

# Terminal 3 - Notification Service
cd CalowinNotification && mvn spring-boot:run

# Terminal 4 - Main Gateway (SpringNode)
cd CalowinSpringNode && mvn spring-boot:run

# Terminal 5 - Trip Service
cd CalowinTrip && mvn spring-boot:run

# Terminal 6 - WellnessZone Service
cd CalowinWellnessZone && mvn spring-boot:run
```

### 2. Frontend Setup

#### Update API Endpoints
The frontend is currently configured to use the deployed HuggingFace space. You need to update it to use localhost:

1. **Update API Base URL**:
```bash
# Find and replace in all Flutter files:
# FROM: https://sc3040G5-CalowinSpringNode.hf.space
# TO: http://localhost:7860
```

2. **Files to update**:
- `calowin_ui/lib/Pages/sign_up/signup_page.dart`
- `calowin_ui/lib/Pages/sign_up/signup_page2.dart`
- `calowin_ui/lib/Pages/login_page.dart`
- `calowin_ui/lib/Pages/profile/editprofile_page.dart`
- `calowin_ui/lib/Pages/profile/changepassword_page.dart`
- `calowin_ui/lib/control/user_retriever.dart`
- `calowin_ui/lib/control/park_retriever.dart`

#### Start Frontend
```bash
cd calowin_ui
flutter pub get
flutter run -d web-server --web-port 3000
```

### 3. Database Configuration

Ensure your SQL Server database is running and update connection strings in each service's `application.properties`:

```properties
# Example for CalowinAccount
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=CALOWIN_SECURE;encrypt=false;trustServerCertificate=true
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## OTP Issue Analysis

### Invalid OTP Cases

Based on the code analysis, "Invalid OTP" errors occur in these scenarios:

1. **OTP Not Found**: No OTP exists for the email/type combination
2. **OTP Expired**: OTP has passed its expiration time (1 day)
3. **Wrong OTP Code**: The entered code doesn't match the stored code
4. **OTP Already Used**: OTP was already verified and deleted
5. **Database Connection Issues**: Cannot connect to CALOWIN_SECURE database

### OTP Validation Logic
```java
// From OTPService.java
public boolean verifyOTP(String email, String otpCode, ActionType type) {
    Optional<OTPEntry> otpEntityOptional = otpRepository.findByEmailAndOtpType(email, type);
    
    if (otpEntityOptional.isPresent()) {
        OTPEntry otpEntity = otpEntityOptional.get();
        
        // Check if expired
        if (otpEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            otpRepository.deleteByEmailAndOtpType(email, type);
            return false; // EXPIRED
        }
        
        // Check if matches
        if (otpEntity.getOtpCode().equals(otpCode)) {
            otpRepository.deleteByEmailAndOtpType(email, type);
            return true; // VALID
        }
    }
    
    return false; // NOT FOUND or WRONG CODE
}
```

### Debugging OTP Issues

1. **Check Database Connection**: Ensure CALOWIN_SECURE database is accessible
2. **Check OTP Generation**: Look for console logs showing generated OTPs
3. **Check Email Delivery**: Verify OTP emails are being sent
4. **Check Timing**: OTPs expire after 1 day, but check if there are timezone issues

## Quick Setup Script

Use the provided `setup.sh` script for automated setup:

```bash
chmod +x setup.sh
./setup.sh
```

## Verification

1. **Backend Services**: 
   - Main Gateway: http://localhost:7860
   - Account Service: http://localhost:7861
   - Friends Service: http://localhost:7862
   - Notification Service: http://localhost:7863
   - Trip Service: http://localhost:7864
   - WellnessZone Service: http://localhost:7865

2. **Frontend**: http://localhost:3000

3. **Health Checks**:
   ```bash
   curl http://localhost:7860/actuator/health
   curl http://localhost:7861/actuator/health
   # ... for other services
   ```

## Troubleshooting

### Port Conflicts
- Ensure each service uses a unique port
- Check `lsof -i :PORT_NUMBER` to see what's using a port

### Database Issues
- Verify SQL Server is running
- Check connection strings in application.properties
- Ensure database exists: CALOWIN_SECURE

### OTP Issues
- Check console logs for generated OTPs
- Verify email service configuration
- Check database for OTP entries
- Ensure proper timezone settings

## Development Workflow

1. **Backend Changes**: Services auto-restart with Spring Boot DevTools
2. **Frontend Changes**: Flutter hot reload on save
3. **Database Changes**: Restart affected services
4. **API Testing**: Use Postman or curl to test endpoints

## Production Deployment

For production deployment:
1. Configure proper database credentials
2. Set up email service (SMTP configuration)
3. Use environment variables for sensitive data
4. Configure proper logging levels
5. Set up monitoring and health checks
