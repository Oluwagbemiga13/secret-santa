package cz.oluwagbemiga.santa.be.controller;

import cz.oluwagbemiga.santa.be.dto.AdminStatistics;
import cz.oluwagbemiga.santa.be.dto.GiftDTO;
import cz.oluwagbemiga.santa.be.entity.GiftStatus;
import cz.oluwagbemiga.santa.be.exception.ErrorResponse;
import cz.oluwagbemiga.santa.be.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Admin operations for gift management and statistics")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;

    @Operation(
            summary = "Get Admin Statistics",
            description = "Fetches the total number of registered users and Santa's lists.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved statistics",
                            content = @Content(schema = @Schema(implementation = AdminStatistics.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Admin access required",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Insufficient permissions",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @GetMapping("/statistics")
    public ResponseEntity<AdminStatistics> getStatistics() {
        return ResponseEntity.ok(adminService.getStatistics());
    }

    @Operation(
            summary = "Get All Gifts by Status",
            description = "Fetches all gifts filtered by their status.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved gifts",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = GiftDTO.class)))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid status provided",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Admin access required",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No gifts found with the specified status",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @GetMapping("/gifts/status/{status}")
    public ResponseEntity<List<GiftDTO>> getAllByStatus(
            @Parameter(description = "Gift status to filter by",
                    required = true,
                    example = "CREATED",
                    schema = @Schema(type = "string", allowableValues = {"CREATED", "SELECTED", "LINKED", "SENT"}))
            @PathVariable String status) {
        return ResponseEntity.ok(adminService.getAllGiftsByStatus(GiftStatus.valueOf(status)));
    }

    @Operation(
            summary = "Add Affiliate Link to Gift",
            description = "Updates a gift with an affiliate link and changes status to LINKED.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Gift updated successfully with affiliate link",
                            content = @Content(schema = @Schema(implementation = GiftDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid gift data provided",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Admin access required",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Gift not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @PostMapping("/gifts/update")
    public ResponseEntity<GiftDTO> updateGift(
            @RequestBody GiftDTO giftDTO) {
        return ResponseEntity.ok(adminService.addAffiliateLink(giftDTO));
    }
}