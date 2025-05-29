package cz.oluwagbemiga.santa.be.dto;

public record AdminStatistics(
        long totalUsers,
        long totalSantasLists,
        long totalPersons,
        long totalGifts
){
}
