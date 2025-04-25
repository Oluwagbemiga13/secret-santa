package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.dto.AuthResponse;
import cz.oluwagbemiga.santa.be.dto.UserDTO;
import cz.oluwagbemiga.santa.be.entity.User;
import cz.oluwagbemiga.santa.be.exception.UserRegistrationException;
import cz.oluwagbemiga.santa.be.mapper.UserMapper;
import cz.oluwagbemiga.santa.be.repository.UserRepository;
import cz.oluwagbemiga.santa.be.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    public AuthResponse register(UserDTO userDTO) {
        logger.info("Registering new user: {}", userDTO.username());

        try {
            validateRegistrationData(userDTO);

            User user = new User();
            user.setUsername(userDTO.username());
            user.setEmail(userDTO.email());
            user.setPassword(passwordEncoder.encode(userDTO.password()));

            User savedUser = userRepository.save(user);
            String token = jwtUtil.generateToken(savedUser.getUuid(), user.getRole());

            logger.info("Successfully registered user: {}", userDTO.username());
            return new AuthResponse(token, savedUser.getUsername());

        } catch (Exception e) {
            logger.error("Failed to register user: {}", userDTO.username(), e);
            throw new UserRegistrationException("Registration failed: " + e.getMessage());
        }
    }

    private void validateRegistrationData(@NonNull UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.username())) {
            throw new UserRegistrationException("Username already exists");
        }

        if (userRepository.existsByEmail(userDTO.email())) {
            throw new UserRegistrationException("Email already exists");
        }
    }


    public void deleteUser(Long id) {
        throw new NotImplementedException("Delete user functionality is not implemented yet.");
    }

    public User findUserById(String userUuidStr) {
        UUID userUuid = UUID.fromString(userUuidStr);
        return userRepository.findById(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("User not found with UUID: " + userUuidStr));
    }

}
