package cz.oluwagbemiga.santa.be.controller;

import cz.oluwagbemiga.santa.be.dto.AuthResponse;
import cz.oluwagbemiga.santa.be.dto.UserDTO;
import cz.oluwagbemiga.santa.be.dto.UserInfo;
import cz.oluwagbemiga.santa.be.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDTO userDTO;
    private AuthResponse authResponse;
    private UserInfo userInfo;
    private UUID userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        userDTO = new UserDTO("testUser", "test@email.com", "password123");
        authResponse = new AuthResponse("testToken", "testUser");
        userInfo = new UserInfo(userId, "testUser", "test@email.com");
    }

    @Test
    void register() {
        when(userService.register(userDTO)).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = userController.register(userDTO);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(authResponse, response.getBody());
        verify(userService).register(userDTO);
    }

    @Test
    void deleteUser() {
        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertEquals(200, response.getStatusCode().value());
        verify(userService).deleteUser(1L);
    }

    @Test
    void getUserInfo() {
        when(userService.getInfoById()).thenReturn(userInfo);

        ResponseEntity<UserInfo> response = userController.getUserInfo();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(userInfo, response.getBody());
        verify(userService).getInfoById();
    }
}