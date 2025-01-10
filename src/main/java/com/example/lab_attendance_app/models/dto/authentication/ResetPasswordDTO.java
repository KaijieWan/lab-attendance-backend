package com.example.lab_attendance_app.models.dto.authentication;

import java.io.Serializable;

public class ResetPasswordDTO implements Serializable {

    private String token;
    private String newPassword;

    public ResetPasswordDTO() {
    }

    public ResetPasswordDTO(String token, String newPassword) {
        this.token = token;
        this.newPassword = newPassword;
    }

    public String getToken() { return token;};

    public ResetPasswordDTO setToken(String token) {
        this.token = token;
        return this;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public ResetPasswordDTO setNewPassword(String newPassword) {
        this.newPassword = newPassword;
        return this;
    }
}
