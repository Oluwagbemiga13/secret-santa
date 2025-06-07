package cz.oluwagbemiga.santa.be.controller;

import cz.oluwagbemiga.santa.be.dto.ResetPasswordRequest;
import cz.oluwagbemiga.santa.be.exception.ErrorResponse;
import cz.oluwagbemiga.santa.be.service.EmailService;
import cz.oluwagbemiga.santa.be.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/password")
@RequiredArgsConstructor
@Tag(name = "Reset Password Management", description = "APIs for resetting and changing passwords")
public class PasswordResetController {

    private final PasswordResetService resetService;

    @Operation(
            summary = "Send password reset link",
            description = "Sends a reset link to the user's email if no valid token exists.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Email sent if exists and no token generated in recent time"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid email format",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @Parameter(description = "Email address to send password reset link", required = true, example = "user@example.com")
            @RequestParam String email) {
        if (!EmailService.isValidEmail(email)) return ResponseEntity.status(400).body("Email in wrong format.");
        resetService.sendResetLink(email);
        return ResponseEntity.ok("Reset link sent if email exists");
    }

    @Operation(
            summary = "Reset password",
            description = "Resets the password using the provided token and new password.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Password reset successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid token or token expired",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Token not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @Parameter(description = "Reset password request containing token and new password", required = true)
            @RequestBody ResetPasswordRequest request) {
        resetService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok("Password reset successfully");
    }
}