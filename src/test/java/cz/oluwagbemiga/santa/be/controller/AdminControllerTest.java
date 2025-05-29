package cz.oluwagbemiga.santa.be.controller;

import cz.oluwagbemiga.santa.be.dto.AdminStatistics;
import cz.oluwagbemiga.santa.be.dto.GiftDTO;
import cz.oluwagbemiga.santa.be.entity.GiftStatus;
import cz.oluwagbemiga.santa.be.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getStatistics() {
        AdminStatistics statistics = new AdminStatistics(10L, 5L, 20L, 30L);
        when(adminService.getStatistics()).thenReturn(statistics);

        ResponseEntity<AdminStatistics> response = adminController.getStatistics();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(statistics, response.getBody());
        verify(adminService).getStatistics();
    }

    @Test
    void getAllByStatus() {
        UUID giftId = UUID.randomUUID();
        GiftDTO gift = new GiftDTO(giftId, "Test Gift", "Description", "link", 100, GiftStatus.CREATED, LocalDate.now());
        List<GiftDTO> gifts = List.of(gift);

        when(adminService.getAllGiftsByStatus(GiftStatus.CREATED)).thenReturn(gifts);

        ResponseEntity<List<GiftDTO>> response = adminController.getAllByStatus("CREATED");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(gifts, response.getBody());
        verify(adminService).getAllGiftsByStatus(GiftStatus.CREATED);
    }

    @Test
    void updateGift() {
        UUID giftId = UUID.randomUUID();
        GiftDTO gift = new GiftDTO(giftId, "Test Gift", "Description", "link", 100, GiftStatus.LINKED, LocalDate.now());

        when(adminService.addAffiliateLink(gift)).thenReturn(gift);

        ResponseEntity<GiftDTO> response = adminController.updateGift(gift);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(gift, response.getBody());
        verify(adminService).addAffiliateLink(gift);
    }
}