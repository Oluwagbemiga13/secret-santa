package cz.oluwagbemiga.santa.be.controller;

import cz.oluwagbemiga.santa.be.dto.AdminStatistics;
import cz.oluwagbemiga.santa.be.dto.GiftDTO;
import cz.oluwagbemiga.santa.be.entity.GiftStatus;
import cz.oluwagbemiga.santa.be.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
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
                            description = "Unauthorized - Admin access required"
                    )
            }
    )
    @GetMapping("/statistics")
    public ResponseEntity<AdminStatistics> getStatistics() {
        return ResponseEntity.ok(adminService.getStatistics());
    }

    @GetMapping("/gifts/status/{status}")
    public ResponseEntity<List<GiftDTO>> getAllByStatus(@PathVariable String status){
            return ResponseEntity.ok(adminService.getAllGiftsByStatus(GiftStatus.valueOf(status)));
    }

    @PostMapping("/gifts/update")
    public ResponseEntity<GiftDTO> updateGift(@RequestBody GiftDTO giftDTO) {
        return ResponseEntity.ok(adminService.addAffiliateLink(giftDTO));
    }
}