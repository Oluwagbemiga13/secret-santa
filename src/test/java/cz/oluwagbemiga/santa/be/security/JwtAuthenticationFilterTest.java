package cz.oluwagbemiga.santa.be.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilterTest.class);

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        log.info("Security context cleared before test execution");
    }

    @Test
    void shouldAuthenticateWhenValidToken() throws Exception {
        log.info("Testing authentication with valid JWT token");
        String token = "valid-token";
        String userUuid = "test-uuid";
        String role = "ROLE_USER";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.getUuidFromJWT(token)).thenReturn(userUuid);
        when(jwtUtil.getRoleFromJWT(token)).thenReturn(role);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userUuid, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        log.info("Authentication successful with valid token");
    }

    @Test
    void shouldNotAuthenticateWhenNoToken() throws Exception {
        log.info("Testing authentication without Authorization header");
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        log.info("Authentication correctly rejected due to missing token");
    }

    @Test
    void shouldNotAuthenticateWhenInvalidToken() throws Exception {
        log.info("Testing authentication with invalid JWT token");
        String token = "invalid-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.validateToken(token)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        log.info("Authentication correctly rejected due to invalid token");
    }
}