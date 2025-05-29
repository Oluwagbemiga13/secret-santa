package cz.oluwagbemiga.santa.be.security;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class JwtUtilTest {
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        log.info("Setting up JwtUtil test instance");
        jwtUtil = new JwtUtil();
        String TEST_SECRET = "testSecretKeyThatIsLongEnoughForHS256Algorithm";
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", TEST_SECRET);
        int TEST_EXPIRATION = 3600000;
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", TEST_EXPIRATION);
    }

    @Test
    void generateAndValidateToken_Success() {
        UUID userId = UUID.randomUUID();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        log.info("Testing token generation for user ID: {}", userId);

        String token = jwtUtil.generateToken(userId, authority);
        log.debug("Generated token: {}", token);

        assertTrue(jwtUtil.validateToken(token));
        String extractedUuid = jwtUtil.getUuidFromJWT(token);
        String extractedRole = jwtUtil.getRoleFromJWT(token);
        log.info("Token validation successful - UUID: {}, Role: {}", extractedUuid, extractedRole);
    }

    @Test
    void validateToken_InvalidToken() {
        String invalidToken = "invalid.token.string";
        log.info("Testing invalid token validation");

        boolean isValid = jwtUtil.validateToken(invalidToken);
        log.info("Token validation result: {}", isValid);

        assertFalse(isValid);
    }

    @Test
    void generateToken_WithUsername_Success() {
        String username = "testuser";
        log.info("Testing token generation with username: {}", username);

        String token = jwtUtil.generateToken(username);
        log.debug("Generated token: {}", token);

        assertTrue(jwtUtil.validateToken(token));
        log.info("Token validation successful");
    }
}