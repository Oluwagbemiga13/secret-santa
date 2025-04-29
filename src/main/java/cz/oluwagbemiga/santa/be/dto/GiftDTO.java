package cz.oluwagbemiga.santa.be.dto;

import java.util.UUID;

public record GiftDTO(
        UUID id,
        String name,
        String description,
        String affiliateLink
) {
}