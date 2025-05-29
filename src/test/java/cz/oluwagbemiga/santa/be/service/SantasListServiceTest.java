package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.dto.PersonDTO;
import cz.oluwagbemiga.santa.be.dto.SantasListDTO;
import cz.oluwagbemiga.santa.be.entity.Person;
import cz.oluwagbemiga.santa.be.entity.SantasList;
import cz.oluwagbemiga.santa.be.entity.User;
import cz.oluwagbemiga.santa.be.exception.ResourceNotFoundException;
import cz.oluwagbemiga.santa.be.mapper.PersonMapper;
import cz.oluwagbemiga.santa.be.mapper.SantasListMapper;
import cz.oluwagbemiga.santa.be.repository.SantasListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SantasListServiceTest {

    @Mock
    private SantasListRepository santasListRepository;

    @Mock
    private SantasListMapper santasListMapper;

    @Mock
    private PersonMapper personMapper;

    @Mock
    private PersonService personService;

    @Mock
    private UserService userService;

    @InjectMocks
    private SantasListService santasListService;

    private SantasList santasList;
    private SantasListDTO santasListDTO;
    private Person person;
    private PersonDTO personDTO;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(UUID.randomUUID().toString());
        SecurityContextHolder.setContext(securityContext);

        UUID listId = UUID.randomUUID();
        UUID personId = UUID.randomUUID();

        person = Person.builder()
                .id(personId)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        personDTO = new PersonDTO(
                personId,
                "John Doe",
                "john.doe@example.com",
                null,
                null
        );

        user = new User();
        user.setUuid(UUID.randomUUID());
        user.setUsername("testuser");

        santasList = SantasList.builder()
                .id(listId)
                .name("Test List")
                .creationDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(2))
                .budgetPerGift(100)
                .owner(user)
                .persons(List.of(person))
                .build();

        santasListDTO = new SantasListDTO(
                listId,
                "Test List",
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                false,
                List.of(personDTO),
                "CREATED",
                user.getUuid(),
                100
        );
    }

    @Test
    void testGetSantasListById() {
        when(santasListRepository.findById(santasList.getId())).thenReturn(Optional.of(santasList));
        when(santasListMapper.toDto(santasList)).thenReturn(santasListDTO);

        SantasListDTO result = santasListService.getSantasListById(santasList.getId());

        assertEquals(santasListDTO, result);
        verify(santasListRepository, times(1)).findById(santasList.getId());
    }

    @Test
    void testGetSantasListById_NotFound() {
        when(santasListRepository.findById(santasList.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> santasListService.getSantasListById(santasList.getId()));
        verify(santasListRepository, times(1)).findById(santasList.getId());
    }

    @Test
    void testAddPersonToSantasList() {
        List<Person> mutablePersons = new ArrayList<>(santasList.getPersons());
        santasList.setPersons(mutablePersons);

        when(santasListRepository.findById(santasList.getId())).thenReturn(Optional.of(santasList));
        when(personMapper.toEntity(personDTO)).thenReturn(person);
        santasList.getPersons().add(person); // Simulate adding the person
        when(santasListRepository.save(santasList)).thenReturn(santasList);
        when(santasListMapper.toDto(santasList)).thenReturn(santasListDTO);

        SantasListDTO result = santasListService.addPersonToSantasList(santasList.getId(), personDTO);

        assertEquals(santasListDTO, result);
        verify(santasListRepository, times(1)).findById(santasList.getId());
        verify(santasListRepository, times(1)).save(santasList);
    }

    @Test
    void testDeletePersonFromSantasList() {
        // Create a mutable list and ensure the person is present in the list
        List<Person> mutablePersons = new ArrayList<>();
        mutablePersons.add(person);
        santasList.setPersons(mutablePersons);

        when(santasListRepository.findById(santasList.getId())).thenReturn(Optional.of(santasList));
        when(santasListMapper.toDto(santasList)).thenReturn(santasListDTO);
        doNothing().when(personService).deletePerson(person.getId());
        when(santasListRepository.save(santasList)).thenReturn(santasList);

        // Call the method and assert the result
        SantasListDTO result = santasListService.deletePersonFromSantasList(santasList.getId(), person.getId());

        assertEquals(santasListDTO, result);
        verify(santasListRepository, times(1)).findById(santasList.getId());
        verify(personService, times(1)).deletePerson(person.getId());
        verify(santasListRepository, times(1)).save(santasList);
    }
}