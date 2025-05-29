package cz.oluwagbemiga.santa.be.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record SantasListDTO(
        UUID id,
        String name,
        LocalDate creationDate,
        LocalDate dueDate,
        boolean isLocked,
        List<PersonDTO> persons,
        String status,
        UUID ownerId,
        int budgetPerGift
) {
}