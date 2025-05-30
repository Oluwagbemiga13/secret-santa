package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.dto.AuthResponse;
import cz.oluwagbemiga.santa.be.dto.UserDTO;
import cz.oluwagbemiga.santa.be.dto.UserInfo;
import cz.oluwagbemiga.santa.be.entity.User;
import cz.oluwagbemiga.santa.be.exception.ResourceNotFoundException;
import cz.oluwagbemiga.santa.be.exception.UserRegistrationException;
import cz.oluwagbemiga.santa.be.repository.UserRepository;
import cz.oluwagbemiga.santa.be.security.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUuid(UUID.randomUUID());
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        userDTO = new UserDTO("testUser", "test@example.com", "password");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testRegister() {
        when(userRepository.existsByUsername(userDTO.username())).thenReturn(false);
        when(userRepository.existsByEmail(userDTO.email())).thenReturn(false);
        when(passwordEncoder.encode(userDTO.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateToken(user.getUuid(), user.getRole())).thenReturn("jwtToken");

        AuthResponse response = userService.register(userDTO);

        assertEquals("jwtToken", response.token());
        assertEquals("testUser", response.username());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_UsernameExists() {
        when(userRepository.existsByUsername(userDTO.username())).thenReturn(true);

        assertThrows(UserRegistrationException.class, () -> userService.register(userDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegister_EmailExists() {
        when(userRepository.existsByEmail(userDTO.email())).thenReturn(true);

        assertThrows(UserRegistrationException.class, () -> userService.register(userDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testFindUserById() {
        when(userRepository.findById(user.getUuid())).thenReturn(Optional.of(user));

        User result = userService.findUserById(user.getUuid().toString());

        assertEquals(user, result);
        verify(userRepository, times(1)).findById(user.getUuid());
    }

    @Test
    void testFindUserById_NotFound() {
        when(userRepository.findById(user.getUuid())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.findUserById(user.getUuid().toString()));
        verify(userRepository, times(1)).findById(user.getUuid());
    }

    @Test
    void testGetInfoById() {
        UUID testUuid = UUID.randomUUID();
        user.setUuid(testUuid);
        when(userRepository.findByUuid(any())).thenReturn(Optional.of(user));

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUuid.toString());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserInfo result = userService.getInfoById();

        assertEquals(user.getUuid(), result.uuid());
        assertEquals(user.getUsername(), result.username());
        assertEquals(user.getEmail(), result.email());
        verify(userRepository, times(1)).findByUuid(testUuid);
    }

    @Test
    void testGetInfoById_NotFound() {
        when(userRepository.findByUuid(user.getUuid())).thenReturn(Optional.empty());

        org.springframework.security.core.Authentication authentication = mock(org.springframework.security.core.Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user.getUuid().toString());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThrows(ResourceNotFoundException.class, () -> userService.getInfoById());
    }
}