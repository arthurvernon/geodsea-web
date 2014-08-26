package com.geodsea.pub.web.rest.dto;

/**
 * Created by Arthur Vernon on 12/08/2014.
 */
public class PasswordChangeDTO {
    private String oldPassword;
    private String newPassword;

    public PasswordChangeDTO(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public PasswordChangeDTO()
    {
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
