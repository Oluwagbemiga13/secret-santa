package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.entity.PasswordResetToken;
import cz.oluwagbemiga.santa.be.entity.User;
import cz.oluwagbemiga.santa.be.exception.InvalidRequestException;
import cz.oluwagbemiga.santa.be.repository.PasswordResetTokenRepository;
import cz.oluwagbemiga.santa.be.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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

    @Transactional
    public void sendResetLink(String email) throws MessagingException, UsernameNotFoundException, InvalidRequestException {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));

        boolean hasValidToken = tokenRepo.existsByUserAndExpiryDateAfter(user, LocalDateTime.now());
        if (hasValidToken) {
            throw new InvalidRequestException("A reset link has already been sent. Please check your email.");
        }
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(30);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);

        resetToken.setExpiryDate(expiry);
        PasswordResetToken saved = tokenRepo.save(resetToken);

        String link = "http://127.0.0.1:5501/reset-password.html?token=" + saved.getToken();
        emailService.sendEmail(user.getEmail(), "Reset your password",
                "Click here to reset your password: " + link);
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

