package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.entity.PasswordResetToken;
import cz.oluwagbemiga.santa.be.entity.User;
import cz.oluwagbemiga.santa.be.exception.InvalidRequestException;
import cz.oluwagbemiga.santa.be.repository.PasswordResetTokenRepository;
import cz.oluwagbemiga.santa.be.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordResetServiceTest {

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Mock
    private PasswordResetTokenRepository tokenRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendResetLink_Success() throws MessagingException {
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);

        // Simulate user lookup
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        // Simulate no valid token exists
        when(tokenRepo.existsByUserAndExpiryDateAfter(eq(user), any(LocalDateTime.class))).thenReturn(false);

        // When the token is saved, simulate setting a UUID as token
        PasswordResetToken savedToken = new PasswordResetToken();
        savedToken.setToken(UUID.randomUUID());
        when(tokenRepo.save(any(PasswordResetToken.class))).thenReturn(savedToken);

        passwordResetService.sendResetLink(email);

        // Capture the token passed to save
        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepo).save(tokenCaptor.capture());
        assertEquals(user, tokenCaptor.getValue().getUser());

        // Verify email sent
        verify(emailService).sendEmail(eq(email), anyString(), contains(savedToken.getToken().toString()));
    }

    @Test
    void testSendResetLink_UserNotFound() {
        String email = "nonexistent@example.com";
        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> passwordResetService.sendResetLink(email));
    }

    @Test
    void testSendResetLink_HasValidToken() {
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(tokenRepo.existsByUserAndExpiryDateAfter(eq(user), any(LocalDateTime.class))).thenReturn(true);

        InvalidRequestException thrownEx = assertThrows(InvalidRequestException.class,
                () -> passwordResetService.sendResetLink(email));
        assertTrue(thrownEx.getMessage().contains("reset link has already been sent"));
    }

    @Test
    void testResetPassword_Success() {
        UUID uuid = UUID.randomUUID();
        String rawPassword = "newPassword";
        String encodedPassword = "encodedPassword";
        User user = new User();
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(uuid);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        token.setUser(user);

        when(tokenRepo.findByToken(uuid)).thenReturn(Optional.of(token));
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        passwordResetService.resetPassword(String.valueOf(uuid), rawPassword);

        // Verify that the user's password is updated and token is deleted
        assertEquals(encodedPassword, user.getPassword());
        verify(userRepo).save(user);
        verify(tokenRepo).delete(token);
    }

    @Test
    void testResetPassword_ExpiredToken() {
        String tokenString = UUID.randomUUID().toString();
        String rawPassword = "newPassword";
        User user = new User();
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.fromString(tokenString));
        token.setExpiryDate(LocalDateTime.now().minusMinutes(10));
        token.setUser(user);

        when(tokenRepo.findByToken(UUID.fromString(tokenString))).thenReturn(Optional.of(token));

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> passwordResetService.resetPassword(tokenString, rawPassword));
        assertTrue(ex.getMessage().contains("Token expired"));
    }

    @Test
    void testCleanupExpiredTokens() {
        PasswordResetToken validToken = new PasswordResetToken();
        validToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        PasswordResetToken expiredToken = new PasswordResetToken();
        expiredToken.setExpiryDate(LocalDateTime.now().minusMinutes(10));

        when(tokenRepo.findAll()).thenReturn(
                java.util.Arrays.asList(validToken, expiredToken)
        );

        passwordResetService.cleanupExpiredTokens();

        // Verify deleteAll is called with the expired token only
        verify(tokenRepo).deleteAll(Collections.singletonList(expiredToken));
    }
}