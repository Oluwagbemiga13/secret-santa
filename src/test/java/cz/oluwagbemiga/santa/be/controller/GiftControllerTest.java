package cz.oluwagbemiga.santa.be.controller;

import cz.oluwagbemiga.santa.be.dto.GiftDTO;
import cz.oluwagbemiga.santa.be.entity.GiftStatus;
import cz.oluwagbemiga.santa.be.service.GiftService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GiftControllerTest {

    @Mock
    private GiftService giftService;

    @InjectMocks
    private GiftController giftController;

    private GiftDTO giftDTO;
    private UUID giftId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        giftId = UUID.randomUUID();
        giftDTO = new GiftDTO(
                giftId,
                "Test Gift",
                "Test Description",
                "http://test.com",
                100,
                GiftStatus.SELECTED,
                LocalDate.now()
        );
    }

    @Test
    void updateGift() {
        when(giftService.fillDesiredGift(any(UUID.class), any(GiftDTO.class))).thenReturn(giftDTO);

        ResponseEntity<GiftDTO> response = giftController.updateGift(giftId, giftDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(giftDTO, response.getBody());
        verify(giftService).fillDesiredGift(giftId, giftDTO);
    }
}