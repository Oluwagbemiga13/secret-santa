package cz.oluwagbemiga.santa.be.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.oluwagbemiga.santa.be.entity.GiftStatus;

import java.time.LocalDate;
import java.util.UUID;

public record GiftDTO(
        UUID id,
        String name,
        String description,
        String affiliateLink,
        @JsonProperty(value = "budgetPerGift", required = true)
        int budgetPerGift,
        GiftStatus status,
        LocalDate expirationDate
) {
}