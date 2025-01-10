package com.example.lab_attendance_app.services;

import com.example.lab_attendance_app.models.dto.authentication.LoginDTO;
import com.example.lab_attendance_app.models.entities.User;

public interface AuthenticationService {

    User createUser(User user);

    User signIn(LoginDTO loginDTO);

    User signOut(User user);

    User changePassword(User user, String newPassword);

    User resetPassword(User user);
}
