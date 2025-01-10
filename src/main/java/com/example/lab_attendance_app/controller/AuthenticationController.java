package com.example.lab_attendance_app.controller;

import com.example.lab_attendance_app.enums.ExecutionStatus;
import com.example.lab_attendance_app.models.dto.MessageResponse;
import com.example.lab_attendance_app.models.dto.authentication.LoginDTO;
import com.example.lab_attendance_app.models.dto.authentication.LoginToken;
import com.example.lab_attendance_app.models.dto.authentication.PasswordResetToken;
import com.example.lab_attendance_app.models.dto.authentication.ResetPasswordDTO;
import com.example.lab_attendance_app.models.entities.User;
import com.example.lab_attendance_app.security.JWTService;
import com.example.lab_attendance_app.services.AuthenticationService;
import com.example.lab_attendance_app.services.EmailService;
import com.example.lab_attendance_app.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JWTService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthenticationController(AuthenticationService authenticationService, JWTService jwtService, UserService userService, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    /**
     * ! Handle HttpMessageNotReadableException, which is thrown when the request body is missing
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException() {
        return ResponseEntity.badRequest().body(
                new MessageResponse(
                        "Request body is missing.",
                        ExecutionStatus.INVALID
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO login) {
        try {
            // Authenticate the user through authentication service
            User user = authenticationService.signIn(login);

            // Generate JWT token
            LoginToken loginToken = new LoginToken(
                    user.getUsername(),
                    jwtService.generateToken(user),
                    user.getRole(),
                    new Date(System.currentTimeMillis() + jwtService.getExpirationTime()),
                    user.toDTO()
            );

            // Update user last login time
            ExecutionStatus status = userService.updateUserLastLogin(user.getId(), Instant.now());

            // Check if the last login time was updated successfully and return the token
            if (status == ExecutionStatus.SUCCESS) {
                return ResponseEntity.ok(loginToken);
            }

            // Return error response if the last login time was not updated which should not happen
            return ResponseEntity.badRequest().body(
                    new MessageResponse(
                            "Failed to update last login time.",
                            ExecutionStatus.FAILED
                    )
            );
        } catch (BadCredentialsException e) { // Catch bad credentials exception
            return ResponseEntity.badRequest().body(
                    new MessageResponse(
                            "Invalid username or password.",
                            ExecutionStatus.INVALID
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new MessageResponse(
                            "An error occurred: " + e.getMessage(),
                            ExecutionStatus.FAILED
                    )
            );
        }
    }

    @PostMapping("/is-valid")
    public ResponseEntity<?> isTokenValid(@RequestParam String token, @RequestParam String username) {
        boolean isInvalidated = jwtService.isTokenInvalidated(token);
        if(isInvalidated) {
            return ResponseEntity.badRequest().body(new MessageResponse("Already Invalidated", ExecutionStatus.INVALID));
        }

        boolean isValid = jwtService.isTokenValidAndNotExpired(token, username);
        if (!isValid) {
            return ResponseEntity.badRequest().body(new MessageResponse("INVALID", ExecutionStatus.INVALID));
        }

        return ResponseEntity.ok(new MessageResponse("VALID", ExecutionStatus.VALID));
    }

    @PostMapping("/is-expired")
    public ResponseEntity<?> isTokenExpired(@RequestParam String token) {
        boolean isInvalidated = jwtService.isTokenInvalidated(token);
        if(isInvalidated) {
            return ResponseEntity.badRequest().body(new MessageResponse("Already Invalidated", ExecutionStatus.INVALID));
        }

        boolean isExpired = jwtService.isTokenExpired(token);
        if (isExpired) {
            return ResponseEntity.badRequest().body(new MessageResponse("EXPIRED", ExecutionStatus.INVALID));
        }

        return ResponseEntity.ok(new MessageResponse("NOT EXPIRED", ExecutionStatus.VALID));
    }

    @PostMapping("/password-reset/send-link")
    public ResponseEntity<?> sendPasswordReset(@RequestParam String email) {
        //Step 0: On frontend send an api call to check if the user with that email exists first before continuing
        //Step 1: Search for that user based on the email in the parameters
        User user = userService.getUserByEmail(email);
        //Step 2: Generate token (maybe create a new class called PasswordResetToken)
        PasswordResetToken passwordResetToken = new PasswordResetToken(
                user.getEmail(),
                user.getUsername(),
                jwtService.generateToken(user),
                new Date(System.currentTimeMillis() + jwtService.getExpirationTime()),
                user.toDTO()
        );
        //Step 3: Put the token into the reset link below and send the email
        String resetLink = "http://localhost:4200/resetPass?token=" + passwordResetToken.getToken();
        emailService.sendPasswordResetEmail(email, resetLink);

        //return ResponseEntity.ok(new MessageResponse("Password reset email sent successfully: " + passwordResetToken, ExecutionStatus.VALID));
        return ResponseEntity.ok(passwordResetToken);
    }

    @PostMapping("/password-reset/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ResetPasswordDTO resetPasswordDTO) throws JsonProcessingException {
        log.debug("Received request to change password: {}", resetPasswordDTO);
        log.debug("Received request to change password: {}", resetPasswordDTO.getToken());
        //Step 4: Create a new page for resetting the password on frontend and handle the changing
        //of password through the controller for resetting of passwords at the backend
        //(Create ResetPasswordDTO to handle new password to be updated with)
        String username = jwtService.extractUsername(resetPasswordDTO.getToken());
        log.debug("Received request to change password: {}", username);

        boolean isValid = jwtService.isTokenValidAndNotExpired(resetPasswordDTO.getToken(), username);
        log.debug("Received request to change password: {}", isValid);
        if (!isValid) {
            return ResponseEntity.badRequest().body(new MessageResponse("INVALID", ExecutionStatus.INVALID));
        }

        User user = userService.getUserByUsername(username);
        Integer id = user.getId();
        log.debug("Received request to change password: {}", id);

        // Updates the password
        ExecutionStatus status = userService.updateUserPassword(id, resetPasswordDTO.getNewPassword());

        log.debug(String.valueOf(status));
        //Token is only added to blocklist, remove token on frontend
        if(status.equals(ExecutionStatus.SUCCESS)) {
            jwtService.invalidateToken(resetPasswordDTO.getToken());
        }
        log.debug("Response body: {}", new ObjectMapper().writeValueAsString(new MessageResponse("Password updated successfully", ExecutionStatus.SUCCESS)));

        return switch (status) {
            case NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new MessageResponse(
                            "User does not exist",
                            ExecutionStatus.NOT_FOUND
                    )
            );

            case SUCCESS -> ResponseEntity.ok(
                    new MessageResponse(
                            "Password updated successfully",
                            ExecutionStatus.SUCCESS
                    )
            );

            default -> ResponseEntity.badRequest().body(
                    new MessageResponse(
                            "Failed to update password",
                            status
                    )
            );
        };
    }
}