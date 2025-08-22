package com.DataTransferObject;

import com.Account.Entities.ActionType;

public class VerifyOtpDTO {
    private String email;
    private String otpCode;
    private ActionType type;

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

    public ActionType getType() {
        return this.type;
    }

    public void setType(ActionType type) {
        this.type = type;
    }

}
