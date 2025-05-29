package cz.oluwagbemiga.santa.be.dto;

import cz.oluwagbemiga.santa.be.entity.SantasList;

import java.util.UUID;

public record SantasListOverview(
        UUID id,
        String name,
        String owner,
        String status,
        String message,
        int budgetPerGift) {
    public SantasListOverview(SantasList santasList) {
        this(santasList.getId(), santasList.getName(), santasList.getOwner().getUsername(), santasList.getStatus().getValue(), santasList.getStatus().getMessage(), santasList.getBudgetPerGift());
    }
}
