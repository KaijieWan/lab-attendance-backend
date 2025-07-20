package com.example.lab_attendance_app.services;

public interface    EmailService {
    /**
     * Create a new user with the given user object.
     *
     * @param toEmail
     * @param resetLink
     * @return ExecutionStatus (SUCCESS, FAILURE, VALIDATION_ERROR)
     */
    void sendPasswordResetEmail(String toEmail, String resetLink);
}
