package cz.oluwagbemiga.santa.be.controller;

import cz.oluwagbemiga.santa.be.dto.AuthResponse;
import cz.oluwagbemiga.santa.be.dto.UserDTO;
import cz.oluwagbemiga.santa.be.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@CrossOrigin(origins = "http://127.0.0.1:5500/register.html")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and returns authentication token",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User successfully registered",
                            content = @Content(schema = @Schema(implementation = AuthResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input or username/email already exists",
                            content = @Content(schema = @Schema(implementation = cz.oluwagbemiga.santa.be.exception.ErrorResponse.class))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid UserDTO userDTO) {
        AuthResponse response = userService.register(userDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete a user account",
            description = "Deletes a user account by ID. Requires authentication. Users can only delete their own account, admins can delete any account.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User successfully deleted"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Not authorized to delete this user"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Not authenticated"
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}