package com.DataTransferObject;

import com.Account.Entities.ActionType;

public class SendOtpDTO {
    private String email;
    private ActionType type;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ActionType getType() {
        return type;
    }

    public void setType(ActionType type) {
        this.type = type;
    }
}
