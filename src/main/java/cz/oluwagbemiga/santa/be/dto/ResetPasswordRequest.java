package cz.oluwagbemiga.santa.be.dto;

public record ResetPasswordRequest(
    String token,
    String newPassword
) {
}
