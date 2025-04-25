package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.dto.AuthResponse;
import cz.oluwagbemiga.santa.be.entity.Role;
import cz.oluwagbemiga.santa.be.entity.User;
import cz.oluwagbemiga.santa.be.repository.UserRepository;
import cz.oluwagbemiga.santa.be.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private final String TEST_USERNAME = "testuser";
    private final String TEST_PASSWORD = "password123";

    @BeforeEach
    void setUp() {
        log.info("Setting up test user data");
        testUser = new User();
        testUser.setUuid(UUID.randomUUID());
        testUser.setUsername(TEST_USERNAME);
        testUser.setPassword("encoded_password");
        testUser.setRole(Role.USER);
        log.debug("Test user created with UUID: {}", testUser.getUuid());
    }

    @Test
    void authenticate_Success() {
        log.info("Testing successful authentication for user: {}", TEST_USERNAME);
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(TEST_PASSWORD, testUser.getPassword())).thenReturn(true);
        String TEST_TOKEN = "test.jwt.token";
        when(jwtUtil.generateToken(any(UUID.class), any())).thenReturn(TEST_TOKEN);

        AuthResponse response = authService.authenticate(TEST_USERNAME, TEST_PASSWORD);

        log.info("Authentication successful, token generated");
        log.debug("Generated token: {}", response.token());
        assertNotNull(response);
        assertEquals(TEST_TOKEN, response.token());
        assertEquals(TEST_USERNAME, response.username());
    }

    @Test
    void authenticate_UserNotFound() {
        log.info("Testing authentication with non-existent user: {}", TEST_USERNAME);
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.authenticate(TEST_USERNAME, TEST_PASSWORD));

        log.info("Expected exception thrown: {}", exception.getMessage());
    }

    @Test
    void authenticate_InvalidPassword() {
        log.info("Testing authentication with invalid password for user: {}", TEST_USERNAME);
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(TEST_PASSWORD, testUser.getPassword())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.authenticate(TEST_USERNAME, TEST_PASSWORD));

        log.info("Expected exception thrown: {}", exception.getMessage());
    }
}