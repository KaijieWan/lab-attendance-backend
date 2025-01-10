package com.example.lab_attendance_app.models.dto.authentication;

import com.example.lab_attendance_app.models.dto.UserDTO;

import java.io.Serializable;
import java.util.Date;

public class PasswordResetToken implements Serializable {

    private String email;
    private String username;
    private String token;
    private Date expiration;
    private UserDTO user;

    public PasswordResetToken(String email, String username, String token, Date expiration, UserDTO user) {
        this.email = email;
        this.username = username;
        this.token = token;
        this.expiration = expiration;
        this.user = user;
    }

    public Date getExpiration() {
        return expiration;
    }

    public PasswordResetToken setExpiration(Date expiration) {
        this.expiration = expiration;
        return this;
    }

    public UserDTO getUser() {
        return user;
    }

    public PasswordResetToken setUser(UserDTO user) {
        this.user = user;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public PasswordResetToken setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public PasswordResetToken setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getToken() {
        return token;
    }

    public PasswordResetToken setToken(String token) {
        this.token = token;
        return this;
    }
}

