package cz.oluwagbemiga.santa.be.controller;

import cz.oluwagbemiga.santa.be.dto.PersonDTO;
import cz.oluwagbemiga.santa.be.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
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
@RequestMapping("/api/persons")
@Tag(name = "Person Management", description = "APIs for managing persons in Santa's list")
@Slf4j
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class PersonController {

    private final PersonService personService;

    @Operation(
            summary = "Create a new person",
            description = "Creates a new person and returns the created person with a generated ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Person successfully created",
                            content = @Content(schema = @Schema(implementation = PersonDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input",
                            content = @Content
                    )
            }
    )
    @PostMapping
    public ResponseEntity<PersonDTO> createPerson(@RequestBody @Valid PersonDTO personDTO) {
        log.debug("Creating person: {}", personDTO);
        PersonDTO createdPerson = personService.createPerson(personDTO);
        return ResponseEntity.ok(createdPerson);
    }

    @Operation(
            summary = "Delete a person",
            description = "Deletes a person by their ID",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Person successfully deleted"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Person not found"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Not authenticated"
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable UUID id) {
        log.debug("Deleting person with ID: {}", id);
        personService.deletePerson(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get persons by Santa's list ID",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/persons/santas-list/{santasListId}")
    public List<PersonDTO> getPersonBySantasListId(@PathVariable UUID santasListId) {
        return personService.getBySantasListId(santasListId);

    }
}