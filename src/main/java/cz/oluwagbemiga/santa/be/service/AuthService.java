package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.dto.AuthResponse;
import cz.oluwagbemiga.santa.be.entity.Role;
import cz.oluwagbemiga.santa.be.entity.User;
import cz.oluwagbemiga.santa.be.exception.UserLoginException;
import cz.oluwagbemiga.santa.be.repository.UserRepository;
import cz.oluwagbemiga.santa.be.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse authenticate(String username, String password) {
        log.debug("Attempting to authenticate user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(AuthService::getException);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw getException();
        }

        String token = jwtUtil.generateToken(user.getUuid(), user.getRole());
        log.debug("Authentication successful for user: {}", user.getUuid());

        return new AuthResponse(token, username);
    }

    public AuthResponse authenticateAdmin(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(AuthService::getException);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw getException();
        }

        if (!user.getRole().equals(Role.ADMIN)) {
            throw getException();
        }

        String token = jwtUtil.generateToken(user.getUuid(), user.getRole());
        log.debug("Authentication successful for admin: {}", user.getUuid());

        return new AuthResponse(token, username);
    }

    private static UserLoginException getException() {
        return new UserLoginException("Invalid username or password");
    }
}