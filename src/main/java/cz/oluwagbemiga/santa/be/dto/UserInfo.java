package cz.oluwagbemiga.santa.be.dto;

import java.util.UUID;

public record UserInfo(
        UUID uuid,
        String username,
        String email

) {
}
