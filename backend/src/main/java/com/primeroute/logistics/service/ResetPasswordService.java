package com.primeroute.logistics.service;

import com.primeroute.logistics.entity.PasswordResetToken;
import com.primeroute.logistics.entity.User;
import com.primeroute.logistics.repository.PasswordResetTokenRepository;
import com.primeroute.logistics.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ResetPasswordService {

    private static final Logger log = LoggerFactory.getLogger(ResetPasswordService.class);

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResetPasswordService(
            PasswordResetTokenRepository passwordResetTokenRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User resetPassword(String rawToken, String newPassword, String confirmPassword) {
        if (rawToken == null || rawToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid password reset link. Please request a new password reset link.");
        }

        if (newPassword == null || newPassword.trim().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }

        if (confirmPassword != null && !confirmPassword.trim().isEmpty() && !newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        String hashedToken = ForgotPasswordService.hashToken(rawToken.trim());

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(hashedToken)
                .or(() -> passwordResetTokenRepository.findByToken(rawToken.trim()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid password reset link. Please request a new password reset link."));

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("This password reset link is no longer valid. Please request a new password reset link.");
        }

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("This password reset link has expired. Please request a new password reset link.");
        }

        User user = resetToken.getUser();
        if (user == null) {
            throw new IllegalArgumentException("Invalid password reset link. Please request a new password reset link.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setLastPasswordChange(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        log.info("PASSWORD RESET email={} userId={} role={} passwordHashUpdated=true hashPrefix={} hashLength={}",
                savedUser.getEmail(), savedUser.getId(), savedUser.getRole(),
                savedUser.getPassword().substring(0, Math.min(7, savedUser.getPassword().length())),
                savedUser.getPassword().length());

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        return savedUser;
    }
}
