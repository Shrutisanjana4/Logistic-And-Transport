package com.primeroute.logistics.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@primeroute.com}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    @Value("${app.reset-token.expiration-minutes:15}")
    private int expirationMinutes;

    public void sendPasswordResetEmail(String toEmail, String resetToken, String recipientName) {
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

        // Ensure JavaMailSenderImpl has latest credentials if provided in env
        if (mailSender instanceof JavaMailSenderImpl impl) {
            if (mailUsername != null && !mailUsername.isBlank()) {
                impl.setUsername(mailUsername.trim());
            }
            if (mailPassword != null && !mailPassword.isBlank()) {
                impl.setPassword(mailPassword.replaceAll("\\s+", "").trim());
            }
        }

        // Check if SMTP credentials are configured
        boolean isSmtpConfigured = mailSender != null
                && mailUsername != null && !mailUsername.isBlank()
                && mailPassword != null && !mailPassword.isBlank();

        if (!isSmtpConfigured) {
            log.info("[PrimeRoute Email] SMTP credentials not set. Password reset link for {}: {}", toEmail, resetLink);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String senderAddress = (mailUsername != null && !mailUsername.isBlank()) ? mailUsername.trim() : fromEmail;
            
            helper.setFrom(senderAddress, "PrimeRoute Logistics");
            helper.setReplyTo(senderAddress);
            helper.setTo(toEmail);
            helper.setSubject("PrimeRoute Logistics Password Reset Request");

            // Transactional email headers to avoid spam classification
            message.setHeader("X-Mailer", "PrimeRoute Logistics Platform");
            message.setHeader("Auto-Submitted", "auto-generated");
            message.setHeader("X-Priority", "3");

            String plainText = "Hello " + (recipientName != null && !recipientName.isBlank() ? recipientName : "there") + ",\n\n"
                    + "A password reset request was received for your PrimeRoute Logistics account.\n\n"
                    + "Click the link below or copy it into your browser to reset your password:\n"
                    + resetLink + "\n\n"
                    + "This link will expire in " + expirationMinutes + " minutes.\n\n"
                    + "If you did not request this, please disregard this email.\n\n"
                    + "Regards,\n"
                    + "PrimeRoute Logistics Support";

            String htmlContent = "<!DOCTYPE html>"
                    + "<html>"
                    + "<head><meta charset=\"UTF-8\"></head>"
                    + "<body style=\"margin: 0; padding: 24px; font-family: Arial, sans-serif; background-color: #f8fafc; color: #1e293b;\">"
                    + "<div style=\"max-width: 560px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; border: 1px solid #e2e8f0; padding: 32px 28px;\">"
                    + "  <div style=\"margin-bottom: 24px; border-bottom: 2px solid #2563eb; padding-bottom: 16px;\">"
                    + "    <h2 style=\"margin: 0; color: #0f172a; font-size: 20px; font-weight: 700;\">PrimeRoute Logistics</h2>"
                    + "    <p style=\"margin: 4px 0 0; color: #64748b; font-size: 13px;\">Account Security & Verification</p>"
                    + "  </div>"
                    + "  <p style=\"font-size: 15px; margin: 0 0 16px; color: #1e293b;\">"
                    + "    Hello " + (recipientName != null && !recipientName.isBlank() ? recipientName : "") + ","
                    + "  </p>"
                    + "  <p style=\"font-size: 14px; line-height: 1.6; color: #334155; margin: 0 0 24px;\">"
                    + "    We received a request to reset your password for your PrimeRoute Logistics account. You can reset your password by clicking the button below:"
                    + "  </p>"
                    + "  <div style=\"text-align: center; margin: 28px 0;\">"
                    + "    <a href=\"" + resetLink + "\" style=\"display: inline-block; background-color: #2563eb; color: #ffffff; font-weight: 600; font-size: 14px; padding: 12px 28px; border-radius: 6px; text-decoration: none;\">Reset Password</a>"
                    + "  </div>"
                    + "  <p style=\"font-size: 13px; line-height: 1.5; color: #64748b; margin: 0 0 12px;\">"
                    + "    This recovery link will expire after " + expirationMinutes + " minutes."
                    + "  </p>"
                    + "  <p style=\"font-size: 13px; line-height: 1.5; color: #64748b; margin: 0 0 24px;\">"
                    + "    If you did not request this password reset, no action is needed."
                    + "  </p>"
                    + "  <div style=\"border-top: 1px solid #f1f5f9; padding-top: 16px; font-size: 12px; color: #64748b; word-break: break-all;\">"
                    + "    Direct link: <a href=\"" + resetLink + "\" style=\"color: #2563eb;\">" + resetLink + "</a>"
                    + "  </div>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            // Multi-part alternative (plain text + HTML) ensures high inbox deliverability
            helper.setText(plainText, htmlContent);
            mailSender.send(message);
            log.info("Password reset email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to deliver email via SMTP to {}: {}. Reset link available: {}", toEmail, e.getMessage(), resetLink);
        }
    }
}
