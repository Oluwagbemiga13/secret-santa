package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.dto.PersonDTO;
import cz.oluwagbemiga.santa.be.entity.Gift;
import cz.oluwagbemiga.santa.be.entity.Person;
import cz.oluwagbemiga.santa.be.mapper.PersonMapper;
import cz.oluwagbemiga.santa.be.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PersonMapper personMapper;

    @InjectMocks
    private PersonService personService;

    private Person person;
    private PersonDTO personDTO;
    private Gift gift;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        UUID personId = UUID.randomUUID();
        UUID giftId = UUID.randomUUID();

        gift = Gift.builder()
                .id(giftId)
                .name("Gift Name")
                .description("Gift Description")
                .affiliateLink("Affiliate Link")
                .budget(100)
                .expirationDate(null)
                .status(null)
                .build();

        person = Person.builder()
                .id(personId)
                .name("John Doe")
                .email("john.doe@example.com")
                .desiredGift(gift)
                .hasSelectedGift(true)
                .build();

        personDTO = new PersonDTO(
                personId,
                "John Doe",
                "john.doe@example.com",
                new cz.oluwagbemiga.santa.be.dto.GiftDTO(
                        giftId,
                        "Gift Name",
                        "Gift Description",
                        "Affiliate Link",
                        100,
                        null,
                        null
                ),
                null
        );
    }

    @Test
    void testGetBySantasListId() {
        UUID santasListId = UUID.randomUUID();
        when(personRepository.findBySantasListId(santasListId)).thenReturn(List.of(person));
        when(personMapper.toDto(List.of(person))).thenReturn(List.of(personDTO));

        List<PersonDTO> result = personService.getBySantasListId(santasListId);

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).name());
        verify(personRepository, times(1)).findBySantasListId(santasListId);
        verify(personMapper, times(1)).toDto(List.of(person));
    }

    @Test
    void testFindById() {
        when(personRepository.findById(person.getId())).thenReturn(Optional.of(person));

        Person result = personService.findById(person.getId());

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(personRepository, times(1)).findById(person.getId());
    }

    @Test
    void testCreatePerson() {
        when(personMapper.toEntity(personDTO)).thenReturn(person);
        when(personRepository.save(person)).thenReturn(person);
        when(personMapper.toDto(person)).thenReturn(personDTO);

        PersonDTO result = personService.createPerson(personDTO);

        assertNotNull(result);
        assertEquals("John Doe", result.name());
        verify(personMapper, times(1)).toEntity(personDTO);
        verify(personRepository, times(1)).save(person);
        verify(personMapper, times(1)).toDto(person);
    }

    @Test
    void testAssignPersonGift() {
        UUID personId = person.getId();
        when(personRepository.findById(personId)).thenReturn(Optional.of(person));
        when(personRepository.save(person)).thenReturn(person);

        personService.assignPersonGift(personId, gift);

        assertEquals(gift, person.getDesiredGift());
        verify(personRepository, times(1)).findById(personId);
        verify(personRepository, times(1)).save(person);
    }

    @Test
    void testUpdatePerson() {
        when(personRepository.save(person)).thenReturn(person);

        personService.updatePerson(person);

        verify(personRepository, times(1)).save(person);
    }

    @Test
    void testFindByGiftId() {
        UUID giftId = gift.getId();
        when(personRepository.findByDesiredGiftId(giftId)).thenReturn(Optional.of(person));

        Person result = personService.findByGiftId(giftId);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(personRepository, times(1)).findByDesiredGiftId(giftId);
    }
}