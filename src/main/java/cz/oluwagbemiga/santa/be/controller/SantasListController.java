package cz.oluwagbemiga.santa.be.controller;

import cz.oluwagbemiga.santa.be.dto.ListDetails;
import cz.oluwagbemiga.santa.be.dto.PersonDTO;
import cz.oluwagbemiga.santa.be.dto.SantasListDTO;
import cz.oluwagbemiga.santa.be.dto.SantasListOverview;
import cz.oluwagbemiga.santa.be.exception.ErrorResponse;
import cz.oluwagbemiga.santa.be.service.EmailService;
import cz.oluwagbemiga.santa.be.service.SantasListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/santas-lists")
@Slf4j
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@Tag(name = "Santa's List Management",
        description = "APIs for managing Secret Santa lists including participant management and email notifications")
public class SantasListController {

    private final SantasListService santasListService;
    private final EmailService emailService;

    @Operation(
            summary = "Create a new Santa's list",
            description = "Creates a new Santa's list and returns the created list with a generated ID",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Santa's list successfully created",
                            content = @Content(schema = @Schema(implementation = SantasListDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<SantasListDTO> createSantasList(
            @Parameter(description = "Santa's list data to create", required = true)
            @RequestBody @Valid SantasListDTO santasListDTO) {
        log.debug("Santa's list creation request: {}", santasListDTO);
        SantasListDTO createdList = santasListService.createSantasList(santasListDTO);
        return ResponseEntity.ok(createdList);
    }

    @Operation(
            summary = "Get a Santa's list by ID",
            description = "Retrieves a specific Santa's list by its unique identifier",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Santa's list found",
                            content = @Content(schema = @Schema(implementation = SantasListDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Santa's list not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Insufficient permissions",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<SantasListDTO> getSantasListById(
            @Parameter(description = "UUID of the Santa's list to retrieve", required = true)
            @PathVariable UUID id) {
        SantasListDTO santasList = santasListService.getSantasListById(id);
        return ResponseEntity.ok(santasList);
    }

    @Operation(
            summary = "Update a Santa's list",
            description = "Updates an existing Santa's list with new information",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Santa's list updated successfully",
                            content = @Content(schema = @Schema(implementation = SantasListDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Santa's list not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Insufficient permissions",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<SantasListDTO> updateSantasList(
            @Parameter(description = "UUID of the Santa's list to update", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Updated Santa's list data", required = true)
            @RequestBody @Valid SantasListDTO santasListDTO) {
        log.debug("Updating Santa's list: {}", santasListDTO);
        SantasListDTO updatedList = santasListService.updateSantasList(id, santasListDTO);
        log.debug("Updated Santa's list: {}", updatedList);
        return ResponseEntity.ok(updatedList);
    }

    @Operation(
            summary = "Delete a Santa's list",
            description = "Permanently removes a Santa's list and all its associated data",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Santa's list deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Santa's list not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Insufficient permissions",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSantasList(
            @Parameter(description = "UUID of the Santa's list to delete", required = true)
            @PathVariable UUID id) {
        santasListService.deleteSantasList(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Add a person to a Santa's list",
            description = "Adds a new participant to an existing Santa's list",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Person added successfully",
                            content = @Content(schema = @Schema(implementation = SantasListDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Santa's list not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @PostMapping("/{id}/persons")
    public ResponseEntity<SantasListDTO> addPersonToSantasList(
            @Parameter(description = "UUID of the Santa's list to add person to", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Person data to add to the list", required = true)
            @RequestBody @Valid PersonDTO personDTO) {
        SantasListDTO updatedList = santasListService.addPersonToSantasList(id, personDTO);
        return ResponseEntity.ok(updatedList);
    }

    @Operation(
            summary = "Edit a person in a Santa's list",
            description = "Updates information for an existing participant in a Santa's list",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Person updated successfully",
                            content = @Content(schema = @Schema(implementation = SantasListDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Santa's list or person not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Insufficient permissions",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @PutMapping("/{id}/persons/{personId}")
    public ResponseEntity<SantasListDTO> editPersonInSantasList(
            @Parameter(description = "UUID of the Santa's list containing the person", required = true)
            @PathVariable UUID id,
            @Parameter(description = "UUID of the person to edit", required = true)
            @PathVariable UUID personId,
            @Parameter(description = "Updated person data", required = true)
            @RequestBody @Valid PersonDTO personDTO) {
        SantasListDTO updatedList = santasListService.editPersonInSantasList(id, personId, personDTO);
        return ResponseEntity.ok(updatedList);
    }

    @Operation(
            summary = "Delete a person from a Santa's list",
            description = "Removes a participant from a Santa's list",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Person removed successfully",
                            content = @Content(schema = @Schema(implementation = SantasListDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Santa's list or person not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Insufficient permissions",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @DeleteMapping("/{listId}/persons/{personId}")
    public ResponseEntity<SantasListDTO> deletePersonFromSantasList(
            @Parameter(description = "UUID of the Santa's list containing the person", required = true)
            @PathVariable UUID listId,
            @Parameter(description = "UUID of the person to remove", required = true)
            @PathVariable UUID personId) {
        SantasListDTO updatedList = santasListService.deletePersonFromSantasList(listId, personId);
        return ResponseEntity.ok(updatedList);
    }

    @Operation(
            summary = "Get Santa's list overviews",
            description = "Retrieves summary information for all Santa's lists belonging to the current user",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List overviews retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = SantasListOverview.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @GetMapping("/get-overviews")
    public List<SantasListOverview> getSantasListOverviews() {
        return santasListService.getListsOverviewsByUserId();
    }

    @Operation(
            summary = "Send emails to all participants",
            description = "Sends Secret Santa assignment emails to all participants in the list",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Emails sent successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Santa's list not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @PostMapping("/{id}/send-emails")
    public ResponseEntity<Void> sendEmails(
            @Parameter(description = "UUID of the Santa's list to send emails for", required = true)
            @PathVariable UUID id) {
        emailService.sendRequest(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get detailed information about a Santa's list",
            description = "Retrieves comprehensive details about a Santa's list including participants and assignments",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List details retrieved successfully",
                            content = @Content(schema = @Schema(implementation = ListDetails.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Santa's list not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication required",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @GetMapping("/{id}/details")
    public ListDetails getListDetails(
            @Parameter(description = "UUID of the Santa's list to get details for", required = true)
            @PathVariable UUID id) {
        ListDetails listDetails = santasListService.getListDetails(id);
        log.info("List details : {}", listDetails);
        return listDetails;
    }
}