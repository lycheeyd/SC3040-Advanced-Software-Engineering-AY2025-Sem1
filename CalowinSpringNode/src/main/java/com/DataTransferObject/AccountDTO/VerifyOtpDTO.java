package com.DataTransferObject.AccountDTO;

public class VerifyOtpDTO {
    private String email;
    private String otpCode;
    private String type;

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtpCode() {
        return this.otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
