package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.dto.AuthResponse;
import cz.oluwagbemiga.santa.be.dto.RegisterRequest;
import cz.oluwagbemiga.santa.be.dto.UserDTO;
import cz.oluwagbemiga.santa.be.entity.User;
import cz.oluwagbemiga.santa.be.mapper.UserMapper;
import cz.oluwagbemiga.santa.be.repository.UserRepository;
import cz.oluwagbemiga.santa.be.security.JwtUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        logger.info("Registering new user: {}", userDTO.getUsername());

        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(savedUser.getUsername());

        return new AuthResponse(token, savedUser.getUsername());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserDTO addUser(@Valid UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User savedUser = userRepository.save(user);
        logger.info("User with id {} has been created", savedUser.getId());
        return userMapper.toDto(savedUser);
    }
}
