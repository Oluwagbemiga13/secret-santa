package cz.oluwagbemiga.santa.be.controller;

import cz.oluwagbemiga.santa.be.dto.GiftDTO;
import cz.oluwagbemiga.santa.be.dto.SantasListDTO;
import cz.oluwagbemiga.santa.be.service.GiftService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gifts")
@Slf4j
public class GiftController {

    private final GiftService giftService;


    @Operation(summary = "Update a gift")
    @PutMapping("/{giftId}")
    public ResponseEntity<GiftDTO> updateGift(@PathVariable UUID giftId, @RequestBody @Valid GiftDTO giftDTO) {
        log.debug("Updating gift: {}", giftDTO);
        return ResponseEntity.ok(giftService.updateGift(giftId, giftDTO));
    }
}
