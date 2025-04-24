package cz.oluwagbemiga.santa.be.dto;

import java.util.UUID;

public record PersonDTO(
        Long id,
        String name,
        String email,
        String desiredGift,
        Long recipientId
) {}