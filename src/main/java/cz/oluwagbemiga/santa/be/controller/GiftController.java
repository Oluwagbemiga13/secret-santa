package cz.oluwagbemiga.santa.be.controller;

import cz.oluwagbemiga.santa.be.dto.GiftDTO;
import cz.oluwagbemiga.santa.be.exception.ErrorResponse;
import cz.oluwagbemiga.santa.be.service.GiftService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gifts")
@Slf4j
@Tag(name = "Gift Management", description = "Operations related to gifts management")
public class GiftController {

    private final GiftService giftService;


    @Operation(
            summary = "Update a gift",
            description = "Updates the details of an existing gift by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Gift updated successfully",
                    content = @Content(schema = @Schema(implementation = GiftDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Gift not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/{giftId}")
    public ResponseEntity<GiftDTO> updateGift(
            @Parameter(description = "UUID of the gift to update", required = true)
            @PathVariable UUID giftId,
            @RequestBody GiftDTO giftDTO) {
        log.debug("Updating gift: {}", giftDTO);
        return ResponseEntity.ok(giftService.fillDesiredGift(giftId, giftDTO));
    }
}