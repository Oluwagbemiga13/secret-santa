package cz.oluwagbemiga.santa.be.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record SantasListDTO(
        UUID id,

        @NotBlank(message = "List name is required")
        String name,

        LocalDate creationDate,

        @FutureOrPresent(message = "Due date cannot be in the past")
        LocalDate dueDate,

        boolean isLocked,

        @Valid
        List<PersonDTO> persons,

        String status,

        UUID ownerId,

        @Min(value = 0, message = "Budget must be a positive number")
        int budgetPerGift
) {
}