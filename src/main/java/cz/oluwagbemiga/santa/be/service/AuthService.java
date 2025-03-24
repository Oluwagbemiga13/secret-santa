package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.dto.AuthResponse;
import cz.oluwagbemiga.santa.be.dto.RegisterRequest;
import cz.oluwagbemiga.santa.be.entity.User;
import cz.oluwagbemiga.santa.be.mapper.UserMapper;
import cz.oluwagbemiga.santa.be.repository.UserRepository;
import cz.oluwagbemiga.santa.be.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;



    public AuthResponse authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(username);
        return new AuthResponse(token, username);
    }
}
