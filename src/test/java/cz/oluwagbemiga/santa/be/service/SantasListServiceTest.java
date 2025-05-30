package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.dto.*;
import cz.oluwagbemiga.santa.be.entity.*;
import cz.oluwagbemiga.santa.be.exception.InvalidRequestException;
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

import static org.junit.jupiter.api.Assertions.*;
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

        GiftDTO giftDTO = new GiftDTO(
                UUID.randomUUID(),
                "Test Gift",
                "Test Description",
                "https://test.url",
                50,
                GiftStatus.CREATED,
                LocalDate.now().plusDays(2));

        personDTO = new PersonDTO(
                personId,
                "John Doe",
                "john.doe@example.com",
                giftDTO,  // Add the gift
                null
        );

        user = new User();
        user.setUuid(UUID.randomUUID());
        user.setUsername("testuser");

        List<Person> persons = new ArrayList<>();
        persons.add(person);

        santasList = SantasList.builder()
                .id(listId)
                .name("Test List")
                .creationDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(2))
                .budgetPerGift(100)
                .owner(user)
                .persons(persons)  // Use mutable list
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

    @Test
    void testCreateSantasList() {
        // Prepare test data
        PersonDTO newPersonDTO = new PersonDTO(null, "Jane Doe", "jane@example.com", null, null);
        SantasListDTO inputDto = new SantasListDTO(
                null,
                "New List",
                null,
                LocalDate.now().plusDays(7),
                false,
                List.of(newPersonDTO),
                null,
                user.getUuid(),
                200
        );

        when(userService.findUserById(anyString())).thenReturn(user);
        when(santasListRepository.save(any(SantasList.class))).thenReturn(santasList);
        when(santasListMapper.toDto(any(SantasList.class))).thenReturn(santasListDTO);

        SantasListDTO result = santasListService.createSantasList(inputDto);

        assertEquals(santasListDTO, result);
        verify(santasListRepository).save(any(SantasList.class));
    }

    @Test
    void testEditPersonInSantasList() {
        PersonDTO updatedPersonDTO = new PersonDTO(
                person.getId(),
                "Updated Name",
                "updated@example.com",
                null,
                null
        );

        when(santasListRepository.findById(santasList.getId())).thenReturn(Optional.of(santasList));
        when(santasListRepository.save(santasList)).thenReturn(santasList);
        when(santasListMapper.toDto(santasList)).thenReturn(santasListDTO);

        SantasListDTO result = santasListService.editPersonInSantasList(
                santasList.getId(),
                person.getId(),
                updatedPersonDTO
        );

        assertEquals(santasListDTO, result);
        verify(santasListRepository).save(santasList);
    }

    @Test
    void testGetListsOverviewsByUserId() {
        String userUuid = UUID.randomUUID().toString();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        when(auth.getPrincipal()).thenReturn(userUuid);

        List<SantasList> lists = List.of(santasList);
        when(santasListRepository.findAllByOwnerUuid(UUID.fromString(userUuid))).thenReturn(lists);

        List<SantasListOverview> result = santasListService.getListsOverviewsByUserId();

        assertEquals(1, result.size());
        verify(santasListRepository).findAllByOwnerUuid(UUID.fromString(userUuid));
    }

    @Test
    void testDeleteSantasList() {
        String userUuid = user.getUuid().toString();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        when(auth.getPrincipal()).thenReturn(userUuid);

        when(santasListRepository.findById(santasList.getId())).thenReturn(Optional.of(santasList));
        doNothing().when(santasListRepository).deleteById(santasList.getId());

        santasListService.deleteSantasList(santasList.getId());

        verify(santasListRepository).deleteById(santasList.getId());
    }

    @Test
    void testUpdateSantasList() {
        SantasListDTO updateDto = new SantasListDTO(
                santasList.getId(),
                "Updated List",
                santasList.getCreationDate(),
                LocalDate.now().plusDays(14),
                false,
                List.of(personDTO),
                "CREATED",
                user.getUuid(),
                150
        );

        when(santasListRepository.findById(santasList.getId())).thenReturn(Optional.of(santasList));
        // Add this line to mock person lookup
        when(personService.findById(personDTO.id())).thenReturn(person);
        when(santasListRepository.save(any(SantasList.class))).thenReturn(santasList);
        when(santasListMapper.toDto(santasList)).thenReturn(updateDto);

        SantasListDTO result = santasListService.updateSantasList(santasList.getId(), updateDto);

        assertEquals(updateDto, result);
        verify(santasListRepository).save(any(SantasList.class));
        // Add verification for person lookup
        verify(personService).findById(personDTO.id());
    }

    @Test
    void testUpdateSantasList_InvalidName() {
        SantasListDTO updateDto = new SantasListDTO(
                santasList.getId(),
                "",  // Invalid blank name
                santasList.getCreationDate(),
                LocalDate.now().plusDays(14),
                false,
                List.of(personDTO),
                "CREATED",
                user.getUuid(),
                150
        );

        assertThrows(InvalidRequestException.class,
                () -> santasListService.updateSantasList(santasList.getId(), updateDto));
    }

    @Test
    void testUpdateSantasList_InvalidBudget() {
        SantasListDTO updateDto = new SantasListDTO(
                santasList.getId(),
                "Valid Name",
                santasList.getCreationDate(),
                LocalDate.now().plusDays(14),
                false,
                List.of(personDTO),
                "CREATED",
                user.getUuid(),
                0  // Invalid budget
        );

        assertThrows(InvalidRequestException.class,
                () -> santasListService.updateSantasList(santasList.getId(), updateDto));
    }

    @Test
    void testUpdateSantasList_RemovePerson() {
        List<Person> mutablePersons = new ArrayList<>();
        mutablePersons.add(person);
        santasList.setPersons(mutablePersons);

        SantasListDTO updateDto = new SantasListDTO(
                santasList.getId(),
                "Updated List",
                santasList.getCreationDate(),
                LocalDate.now().plusDays(14),
                false,
                List.of(),
                "CREATED",
                user.getUuid(),
                150
        );

        when(santasListRepository.findById(santasList.getId())).thenReturn(Optional.of(santasList));
        doNothing().when(personService).deletePerson(person.getId());
        when(santasListRepository.save(any(SantasList.class))).thenReturn(santasList);
        when(santasListMapper.toDto(santasList)).thenReturn(updateDto);

        SantasListDTO result = santasListService.updateSantasList(santasList.getId(), updateDto);

        assertEquals(updateDto, result);
        verify(personService).deletePerson(person.getId());
    }

    @Test
    void testUpdateSantasList_AddNewPerson() {
        when(personService.findById(person.getId())).thenReturn(person);

        PersonDTO newPersonDTO = new PersonDTO(
                null,
                "New Person",
                "new@example.com",
                null,
                null
        );

        SantasListDTO updateDto = new SantasListDTO(
                santasList.getId(),
                "Updated List",
                santasList.getCreationDate(),
                LocalDate.now().plusDays(14),
                false,
                List.of(personDTO, newPersonDTO),
                "CREATED",
                user.getUuid(),
                150
        );

        Person newPerson = Person.builder()
                .name(newPersonDTO.name())
                .email(newPersonDTO.email())
                .build();


        when(santasListRepository.findById(santasList.getId())).thenReturn(Optional.of(santasList));
        when(personMapper.toEntity(newPersonDTO)).thenReturn(newPerson);
        when(personService.findById(personDTO.id())).thenReturn(person);
        when(santasListRepository.save(any(SantasList.class))).thenReturn(santasList);
        when(santasListMapper.toDto(santasList)).thenReturn(updateDto);

        SantasListDTO result = santasListService.updateSantasList(santasList.getId(), updateDto);

        assertEquals(updateDto, result);
        verify(personMapper).toEntity(newPersonDTO);
        verify(personService).findById(personDTO.id());
    }

    @Test
    void testUpdateSantasList_UpdateExistingPerson() {
        PersonDTO updatedPersonDTO = new PersonDTO(
                person.getId(),
                "Updated Name",
                "updated@example.com",
                null,
                null
        );

        SantasListDTO updateDto = new SantasListDTO(
                santasList.getId(),
                "Updated List",
                santasList.getCreationDate(),
                LocalDate.now().plusDays(14),
                false,
                List.of(updatedPersonDTO),
                "CREATED",
                user.getUuid(),
                150
        );

        when(santasListRepository.findById(santasList.getId())).thenReturn(Optional.of(santasList));
        when(personService.findById(person.getId())).thenReturn(person);
        when(santasListRepository.save(any(SantasList.class))).thenReturn(santasList);
        when(santasListMapper.toDto(santasList)).thenReturn(updateDto);

        SantasListDTO result = santasListService.updateSantasList(santasList.getId(), updateDto);

        assertEquals(updateDto, result);
        verify(personService).findById(person.getId());
    }

    @Test
    void testUpdateSantasList_InvalidPersonData() {
        PersonDTO invalidPersonDTO = new PersonDTO(
                null,
                "",  // Invalid blank name
                "email@example.com",
                null,
                null
        );

        SantasListDTO updateDto = new SantasListDTO(
                santasList.getId(),
                "Updated List",
                santasList.getCreationDate(),
                LocalDate.now().plusDays(14),
                false,
                List.of(invalidPersonDTO),
                "CREATED",
                user.getUuid(),
                150
        );

        assertThrows(InvalidRequestException.class,
                () -> santasListService.updateSantasList(santasList.getId(), updateDto));
    }

    @Test
    void testUpdateStatus() {
        UUID listId = santasList.getId();
        ListStatus newStatus = ListStatus.GIFTS_SELECTED;

        when(santasListRepository.findById(listId)).thenReturn(Optional.of(santasList));
        when(santasListRepository.save(santasList)).thenReturn(santasList);
        when(santasListMapper.toDto(santasList)).thenReturn(santasListDTO);

        SantasListDTO result = santasListService.updateStatus(listId, newStatus);

        assertEquals(santasListDTO, result);
        verify(santasListRepository).findById(listId);
        verify(santasListRepository).save(santasList);
    }

    @Test
    void testUpdateStatus_NotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(santasListRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> santasListService.updateStatus(nonExistentId, ListStatus.GIFTS_SELECTED));
    }

    @Test
    void testGetListDetails() {
        UUID listId = santasList.getId();
        List<PersonOverview> personOverviews = List.of(
                new PersonOverview(person)
        );

        when(santasListRepository.findById(listId)).thenReturn(Optional.of(santasList));
        when(personService.getBySantasListId(listId)).thenReturn(List.of(personDTO));

        ListDetails result = santasListService.getListDetails(listId);

        assertEquals(listId, result.id());
        assertEquals(santasList.getName(), result.name());
        assertEquals(santasList.getCreationDate(), result.creationDate());
        assertEquals(santasList.getDueDate(), result.dueDate());
        assertEquals(santasList.isLocked(), result.isLocked());
        assertEquals(santasList.getStatus().name(), result.status());
        assertEquals(santasList.getBudgetPerGift(), result.budgetPerGift());
        assertEquals(1, result.persons().size());
    }

    @Test
    void testGetListDetails_NotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(santasListRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> santasListService.getListDetails(nonExistentId));
    }

    @Test
    void testGetAllByStatus() {
        ListStatus status = ListStatus.CREATED;
        List<SantasList> expectedLists = List.of(santasList);

        when(santasListRepository.findByStatus(status)).thenReturn(expectedLists);

        List<SantasList> result = santasListService.getAllByStatus(status);

        assertEquals(expectedLists, result);
        verify(santasListRepository).findByStatus(status);
    }

    @Test
    void testGetAllByStatus_EmptyList() {
        ListStatus status = ListStatus.CREATED;
        when(santasListRepository.findByStatus(status)).thenReturn(List.of());

        List<SantasList> result = santasListService.getAllByStatus(status);

        assertTrue(result.isEmpty());
        verify(santasListRepository).findByStatus(status);
    }
}