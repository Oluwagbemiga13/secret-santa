package cz.oluwagbemiga.santa.be.controller;

import cz.oluwagbemiga.santa.be.dto.GiftDTO;
import cz.oluwagbemiga.santa.be.dto.PersonDTO;
import cz.oluwagbemiga.santa.be.dto.SantasListDTO;
import cz.oluwagbemiga.santa.be.entity.GiftStatus;
import cz.oluwagbemiga.santa.be.service.EmailService;
import cz.oluwagbemiga.santa.be.service.SantasListService;
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

class SantasListControllerTest {

    @Mock
    private SantasListService santasListService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private SantasListController santasListController;

    private UUID santasListId;
    private UUID personId;
    private SantasListDTO santasListDTO;
    private PersonDTO personDTO;
    private GiftDTO giftDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        santasListId = UUID.randomUUID();
        personId = UUID.randomUUID();
        giftDTO = new GiftDTO(UUID.randomUUID(), "Test Gift", "Description", "link", 100, GiftStatus.CREATED, LocalDate.now());
        personDTO = new PersonDTO(personId, "Test Person", "test@email.com", giftDTO, UUID.randomUUID());
        santasListDTO = new SantasListDTO(
                santasListId,
                "Test List",
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                false,
                List.of(personDTO),
                "CREATED",
                UUID.randomUUID(),
                100
        );
    }

    @Test
    void createSantasList() {
        when(santasListService.createSantasList(santasListDTO)).thenReturn(santasListDTO);

        ResponseEntity<SantasListDTO> response = santasListController.createSantasList(santasListDTO);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(santasListDTO, response.getBody());
        verify(santasListService).createSantasList(santasListDTO);
    }

    @Test
    void getSantasListById() {
        when(santasListService.getSantasListById(santasListId)).thenReturn(santasListDTO);

        ResponseEntity<SantasListDTO> response = santasListController.getSantasListById(santasListId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(santasListDTO, response.getBody());
        verify(santasListService).getSantasListById(santasListId);
    }

    @Test
    void updateSantasList() {
        when(santasListService.updateSantasList(santasListId, santasListDTO)).thenReturn(santasListDTO);

        ResponseEntity<SantasListDTO> response = santasListController.updateSantasList(santasListId, santasListDTO);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(santasListDTO, response.getBody());
        verify(santasListService).updateSantasList(santasListId, santasListDTO);
    }
}