package cz.oluwagbemiga.santa.be.controller;

import cz.oluwagbemiga.santa.be.dto.ResetPasswordRequest;
import cz.oluwagbemiga.santa.be.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/password")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService resetService;

    @Operation(summary = "Send password reset link", description = "Sends a reset link to the user's email if no valid token exists.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reset link sent successfully"),
            @ApiResponse(responseCode = "400", description = "A reset link has already been sent"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) throws MessagingException {
        resetService.sendResetLink(email);
        return ResponseEntity.ok("Reset link sent if email exists");
    }

    @Operation(summary = "Reset password", description = "Resets the password using the provided token and new password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid token or token expired", content = @Content)
    })
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        resetService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok("Password reset successfully");
    }
}