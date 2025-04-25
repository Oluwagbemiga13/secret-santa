package cz.oluwagbemiga.santa.be.controller;

import cz.oluwagbemiga.santa.be.dto.PersonDTO;
import cz.oluwagbemiga.santa.be.dto.SantasListDTO;
import cz.oluwagbemiga.santa.be.service.SantasListService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/santas-lists")
public class SantasListController {

    private final SantasListService santasListService;

    @Operation(summary = "Create a new Santa's list")
    @PostMapping
    public ResponseEntity<SantasListDTO> createSantasList(@RequestBody @Valid SantasListDTO santasListDTO) {
        SantasListDTO createdList = santasListService.createSantasList(santasListDTO);
        return ResponseEntity.ok(createdList);
    }

    @Operation(summary = "Get a Santa's list by ID")
    @GetMapping("/{id}")
    public ResponseEntity<SantasListDTO> getSantasListById(@PathVariable UUID id) {
        SantasListDTO santasList = santasListService.getSantasListById(id);
        return ResponseEntity.ok(santasList);
    }

    @Operation(summary = "Update a Santa's list")
    @PutMapping("/{id}")
    public ResponseEntity<SantasListDTO> updateSantasList(@PathVariable UUID id, @RequestBody @Valid SantasListDTO santasListDTO) {
        SantasListDTO updatedList = santasListService.updateSantasList(id, santasListDTO);
        return ResponseEntity.ok(updatedList);
    }

    @Operation(summary = "Delete a Santa's list")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSantasList(@PathVariable UUID id) {
        santasListService.deleteSantasList(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add a person to a Santa's list")
    @PostMapping("/{id}/persons")
    public ResponseEntity<SantasListDTO> addPersonToSantasList(@PathVariable UUID id, @RequestBody @Valid PersonDTO personDTO) {
        SantasListDTO updatedList = santasListService.addPersonToSantasList(id, personDTO);
        return ResponseEntity.ok(updatedList);
    }

    @Operation(summary = "Edit a person in a Santa's list")
    @PutMapping("/{id}/persons/{personId}")
    public ResponseEntity<SantasListDTO> editPersonInSantasList(@PathVariable UUID id, @PathVariable Long personId, @RequestBody @Valid PersonDTO personDTO) {
        SantasListDTO updatedList = santasListService.editPersonInSantasList(id, personId, personDTO);
        return ResponseEntity.ok(updatedList);
    }

    @Operation(summary = "Delete a person from a Santa's list")
    @DeleteMapping("/{id}/persons/{personId}")
    public ResponseEntity<SantasListDTO> deletePersonFromSantasList(@PathVariable UUID id, @PathVariable Long personId) {
        SantasListDTO updatedList = santasListService.deletePersonFromSantasList(id, personId);
        return ResponseEntity.ok(updatedList);
    }

}