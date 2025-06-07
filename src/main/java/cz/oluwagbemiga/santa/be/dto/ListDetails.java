package cz.oluwagbemiga.santa.be.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ListDetails(
        @NotNull(message = "List ID is required")
        UUID id,

        @NotBlank(message = "List name is required")
        String name,

        LocalDate creationDate,
        LocalDate dueDate,

        boolean isLocked,

        @Valid
        List<PersonOverview> persons,

        @NotBlank(message = "Status is required")
        String status,

        @Min(value = 0, message = "Budget must be a positive number")
        int budgetPerGift) {
}
