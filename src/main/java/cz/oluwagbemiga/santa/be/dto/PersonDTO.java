package cz.oluwagbemiga.santa.be.dto;

import java.util.UUID;

public record PersonDTO(
        UUID id,
        String name,
        String email,
        String desiredGift,
        UUID recipientId
) {
}