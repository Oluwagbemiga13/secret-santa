package cz.oluwagbemiga.santa.be.controller;

import cz.oluwagbemiga.santa.be.dto.PersonDTO;
import cz.oluwagbemiga.santa.be.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/persons")
@Tag(name = "Person Management", description = "APIs for managing persons in Santa's list")
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
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        personService.deletePerson(id);
        return ResponseEntity.ok().build();
    }
}