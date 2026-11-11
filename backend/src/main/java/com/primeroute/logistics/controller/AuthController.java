package com.primeroute.logistics.controller;

import com.primeroute.logistics.dto.ForgotPasswordRequest;
import com.primeroute.logistics.dto.RegisterRequest;
import com.primeroute.logistics.dto.LoginRequest;
import com.primeroute.logistics.dto.LoginResponse;
import com.primeroute.logistics.dto.ResetPasswordRequest;
import com.primeroute.logistics.entity.User;
import com.primeroute.logistics.service.AuthService;
import com.primeroute.logistics.service.ForgotPasswordService;
import com.primeroute.logistics.service.ResetPasswordService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://localhost:5174"
})
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final ForgotPasswordService forgotPasswordService;
    private final ResetPasswordService resetPasswordService;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public AuthController(
            AuthService authService,
            ForgotPasswordService forgotPasswordService,
            ResetPasswordService resetPasswordService) {

        this.authService = authService;
        this.forgotPasswordService = forgotPasswordService;
        this.resetPasswordService = resetPasswordService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request) {

        String message = authService.register(request);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        Map<String, String> response = new HashMap<>();
        try {
            String rawToken = forgotPasswordService.createPasswordResetToken(request.getEmail());
            response.put("message", "If an account exists for this email address, a password reset link has been sent.");
            if (rawToken != null) {
                response.put("resetUrl", frontendUrl + "/reset-password?token=" + rawToken);
            }
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Forgot password validation notice: {}", e.getMessage());
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Unexpected error during forgot password processing: {}", e.getMessage());
            response.put("message", "If an account exists for this email address, a password reset link has been sent.");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        try {
            User user = resetPasswordService.resetPassword(
                    request.getToken(),
                    request.getNewPassword(),
                    request.getConfirmPassword()
            );

            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "Password reset successfully.");
            resp.put("email", user.getEmail());
            resp.put("role", user.getRole().name());

            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            log.warn("Password reset rejected: {}", e.getMessage());
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        } catch (Exception e) {
            log.error("Unexpected error during password reset: {}", e.getMessage());
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Unable to reset password. Please request a new reset link.");
            return ResponseEntity.badRequest().body(err);
        }
    }
}
