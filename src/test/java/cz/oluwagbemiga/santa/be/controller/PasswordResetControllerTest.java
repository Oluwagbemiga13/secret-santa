package cz.oluwagbemiga.santa.be.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.oluwagbemiga.santa.be.dto.ResetPasswordRequest;
import cz.oluwagbemiga.santa.be.exception.GlobalExceptionHandler;
import cz.oluwagbemiga.santa.be.service.PasswordResetService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PasswordResetControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PasswordResetService resetService;

    @InjectMocks
    private PasswordResetController passwordResetController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(passwordResetController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testForgotPasswordSuccess() throws Exception {
        String email = "user@example.com";

        mockMvc.perform(post("/auth/password/forgot-password")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(content().string("Reset link sent if email exists"));

        verify(resetService).sendResetLink(email);
    }

    @Test
    void testForgotPasswordFailure() throws Exception {
        String email = "invalid@example.com";
        doThrow(new MessagingException("Error sending email"))
                .when(resetService).sendResetLink(email);

        mockMvc.perform(post("/auth/password/forgot-password")
                        .param("email", email))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testResetPasswordSuccess() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest("some-token", "newPassword");

        mockMvc.perform(post("/auth/password/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset successfully"));

        verify(resetService).resetPassword(request.token(), request.newPassword());
    }
}