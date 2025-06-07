package cz.oluwagbemiga.santa.be.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record PersonDTO(
        UUID id,

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        String email,

        @Valid
        GiftDTO desiredGift,

        UUID recipientId
) {
    public boolean hasSelectedGift() {
        return desiredGift.name() != null;
    }
}