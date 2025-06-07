package cz.oluwagbemiga.santa.be.dto;

import cz.oluwagbemiga.santa.be.entity.GiftStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.UUID;

public record GiftDTO(
        UUID id,

        @NotBlank(message = "Gift name is required")
        String name,
        String description,

        String affiliateLink,

        @Min(value = 0, message = "Budget must be a non-negative number")
        int budgetPerGift,

        GiftStatus status,

        LocalDate expirationDate
) {
}