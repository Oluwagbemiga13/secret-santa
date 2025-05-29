package cz.oluwagbemiga.santa.be.dto;

import java.util.UUID;

public record PersonDTO(
        UUID id,
        String name,
        String email,
        GiftDTO desiredGift,
        UUID recipientId
) {
    public boolean hasSelectedGift() {
        return desiredGift.name() != null;
    }
}