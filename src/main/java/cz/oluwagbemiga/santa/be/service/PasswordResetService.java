package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.entity.PasswordResetToken;
import cz.oluwagbemiga.santa.be.entity.User;
import cz.oluwagbemiga.santa.be.repository.PasswordResetTokenRepository;
import cz.oluwagbemiga.santa.be.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepo;
    private final UserRepository userRepo;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${secret-santa.fe.base-url}")
    private String baseUrl;

    @Transactional
    public void sendResetLink(String email) {
        Optional<User> opt = userRepo.findByEmail(email);
        if (opt.isEmpty()) return;

        User user = opt.get();
        boolean hasValidToken = tokenRepo.existsByUserAndExpiryDateAfter(user, LocalDateTime.now());
        if (hasValidToken) {
            log.debug("User {} already has a valid password reset token", user.getEmail());
            // Ignored to prevent sending multiple emails and exposing user's email existence
        }
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(30);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);

        resetToken.setExpiryDate(expiry);
        PasswordResetToken saved = tokenRepo.save(resetToken);

        String link = baseUrl + "/reset-password.html?token=" + saved.getToken();
        try {
            emailService.sendEmail(user.getEmail(), "Reset your password",
                    "Click here to reset your password: " + link);
        } catch (MessagingException e) {
            log.error("Something went wrong went sending reset link to {}, message : {}", user.getEmail(), e.getMessage());
            // Ignored to prevent sending multiple emails and exposing user's email existence
        }
        log.debug("Resent password link : {}", link);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepo.findByToken(UUID.fromString(token))
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expired or already used");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        tokenRepo.delete(resetToken);
    }

    @Scheduled(cron = "0 * * * * *")
    public void cleanupExpiredTokens() {
        List<PasswordResetToken> expired = tokenRepo.findAll().stream()
                .filter(t -> t.getExpiryDate().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        tokenRepo.deleteAll(expired);
        log.debug("Cleaned up {} expired password reset tokens", expired.size());
    }
}

