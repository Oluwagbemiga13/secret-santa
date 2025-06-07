package cz.oluwagbemiga.santa.be.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AdminStatistics(
        @NotNull(message = "Total users count is required")
        @Min(value = 0, message = "Total users must be a non-negative number")
        long totalUsers,

        @NotNull(message = "Total santas lists count is required")
        @Min(value = 0, message = "Total santas lists must be a non-negative number")
        long totalSantasLists,

        @NotNull(message = "Total persons count is required")
        @Min(value = 0, message = "Total persons must be a non-negative number")
        long totalPersons,

        @NotNull(message = "Total gifts count is required")
        @Min(value = 0, message = "Total gifts must be a non-negative number")
        long totalGifts
) {
}
