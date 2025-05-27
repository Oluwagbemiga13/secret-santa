package cz.oluwagbemiga.santa.be.dto;

import cz.oluwagbemiga.santa.be.entity.SantasList;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ListDetails(
        UUID id,
        String name,
        LocalDate creationDate,
        LocalDate dueDate,
        boolean isLocked,
        List<PersonOverview> persons,
        String status,
        int budgetPerGift) {
    public ListDetails(SantasList santasList) {
        this(
                santasList.getId(),
                santasList.getName(),
                santasList.getCreationDate(),
                santasList.getDueDate(),
                santasList.isLocked(),
                santasList.getPersons().stream()
                        .map(PersonOverview::new)
                        .toList(),
                santasList.getStatus().name(),
                santasList.getBudgetPerGift()
        );
    }

}
