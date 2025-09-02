package com.DataTransferObject;

public class ChangePasswordDTO {
    private String userID;
    private String oldPassword;
    private String newPassword;
    private String confirm_newPassword;

    public String getUserID() {
        return this.userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getOldPassword() {
        return this.oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return this.newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirm_newPassword() {
        return this.confirm_newPassword;
    }

    public void setConfirm_newPassword(String confirm_newPassword) {
        this.confirm_newPassword = confirm_newPassword;
    }

}
