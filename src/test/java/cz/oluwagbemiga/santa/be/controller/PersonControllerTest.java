package cz.oluwagbemiga.santa.be.controller;

import cz.oluwagbemiga.santa.be.dto.GiftDTO;
import cz.oluwagbemiga.santa.be.dto.PersonDTO;
import cz.oluwagbemiga.santa.be.entity.GiftStatus;
import cz.oluwagbemiga.santa.be.service.PersonService;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PersonControllerTest {

    @Mock
    private PersonService personService;

    @InjectMocks
    private PersonController personController;

    private PersonDTO personDTO;
    private UUID personId;
    private UUID santasListId;
    private GiftDTO giftDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        personId = UUID.randomUUID();
        santasListId = UUID.randomUUID();
        giftDTO = new GiftDTO(UUID.randomUUID(), "Test Gift", "Description", "link", 100, GiftStatus.CREATED, LocalDate.now());
        personDTO = new PersonDTO(personId, "Test Person", "test@email.com", giftDTO, UUID.randomUUID());
    }

    @Test
    void createPerson() {
        when(personService.createPerson(personDTO)).thenReturn(personDTO);

        ResponseEntity<PersonDTO> response = personController.createPerson(personDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(personDTO, response.getBody());
        verify(personService).createPerson(personDTO);
    }

    @Test
    void deletePerson() {
        ResponseEntity<Void> response = personController.deletePerson(personId);

        assertEquals(200, response.getStatusCode().value());
        verify(personService).deletePerson(personId);
    }

    @Test
    void getPersonBySantasListId() {
        List<PersonDTO> persons = List.of(personDTO);
        when(personService.getBySantasListId(santasListId)).thenReturn(persons);

        List<PersonDTO> response = personController.getPersonBySantasListId(santasListId);

        assertEquals(persons, response);
        verify(personService).getBySantasListId(santasListId);
    }
}