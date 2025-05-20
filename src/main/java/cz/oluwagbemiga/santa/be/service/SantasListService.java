package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.dto.*;
import cz.oluwagbemiga.santa.be.entity.ListStatus;
import cz.oluwagbemiga.santa.be.entity.Person;
import cz.oluwagbemiga.santa.be.entity.SantasList;
import cz.oluwagbemiga.santa.be.exception.ResourceNotFoundException;
import cz.oluwagbemiga.santa.be.exception.UnauthorizedAccessException;
import cz.oluwagbemiga.santa.be.mapper.PersonMapper;
import cz.oluwagbemiga.santa.be.mapper.SantasListMapper;
import cz.oluwagbemiga.santa.be.repository.SantasListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SantasListService {

    private final SantasListRepository santasListRepository;
    private final SantasListMapper santasListMapper;
    private final PersonMapper personMapper;
    private final PersonService personService;
    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_USER')")
    public SantasListDTO createSantasList(SantasListDTO santasListDTO) {
        String userUuid = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        SantasList newList = SantasList.builder()
                .creationDate(LocalDate.now())
                .dueDate(santasListDTO.dueDate())
                .name(santasListDTO.name())
                .owner(userService.findUserById(userUuid))
                .build();

        // Use the proper bidirectional relationship management
        santasListDTO.persons().forEach(personDTO -> {
            Person person = Person.builder()
                    .name(personDTO.name())
                    .email(personDTO.email())
                    .desiredGift(null)
                    .build();
            newList.addPerson(person); // This method handles both sides of the relationship
        });

        SantasList savedList = santasListRepository.save(newList);
        return santasListMapper.toDto(savedList);
    }

    public SantasListDTO editPersonInSantasList(UUID santasListId, Long personId, PersonDTO updatedPersonDTO) {
        SantasList santasList = santasListRepository.findById(santasListId)
                .orElseThrow(() -> new ResourceNotFoundException("Santa's list not found with ID: " + santasListId));

        Person person = santasList.getPersons().stream()
                .filter(p -> p.getId().equals(personId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Person not found with ID: " + personId));

        person.setName(updatedPersonDTO.name());
        person.setEmail(updatedPersonDTO.email());

        SantasList updatedSantasList = santasListRepository.save(santasList);
        return santasListMapper.toDto(updatedSantasList);
    }

    public SantasListDTO deletePersonFromSantasList(UUID santasListId, UUID personId) {
        SantasList santasList = santasListRepository.findById(santasListId)
                .orElseThrow(() -> new ResourceNotFoundException("Santa's list not found with ID: " + santasListId));

        Person person = santasList.getPersons().stream()
                .filter(p -> p.getId().equals(personId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with ID: " + personId));

        // Remove bidirectional relationship
        person.setSantasList(null);
        santasList.getPersons().remove(person);

        // Save the updated list
        santasList = santasListRepository.save(santasList);

        // Delete the person
        personService.deletePerson(personId);

        return santasListMapper.toDto(santasList);
    }

    public SantasListDTO addPersonToSantasList(UUID santasListId, PersonDTO newPersonDTO) {
        SantasList santasList = santasListRepository.findById(santasListId)
                .orElseThrow(() -> new ResourceNotFoundException("Santa's list not found with ID: " + santasListId));

        Person newPerson = personMapper.toEntity(newPersonDTO);
        newPerson.setSantasList(santasList);

        santasList.getPersons().add(newPerson);

        SantasList updatedSantasList = santasListRepository.save(santasList);
        return santasListMapper.toDto(updatedSantasList);
    }

    public SantasListDTO updateSantasList(SantasListDTO santasListDTO){
        return santasListMapper.toDto(santasListRepository.save(santasListMapper.toEntity(santasListDTO)));
    }

    /**
     * Updates only approved attributes. Excluding isLocked and creationDate.
     *
     * @param id
     * @param santasListDTO
     * @return
     */
    public SantasListDTO updateSantasList(UUID id, SantasListDTO santasListDTO) {
        log.info("Starting update for SantasList ID: {}", id);

        SantasList santasList = santasListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Santa's list not found with ID: " + id));
        log.debug("Found existing SantasList: {}, with {} persons", santasList.getName(), santasList.getPersons().size());

        // Update basic list properties
        santasList.setName(santasListDTO.name());
        santasList.setDueDate(santasListDTO.dueDate());

        // Get IDs of persons in the DTO
        List<UUID> updatedPersonIds = santasListDTO.persons().stream()
                .map(PersonDTO::id)
                .filter(Objects::nonNull)
                .toList();

        // Find and remove persons that are no longer in the list
        List<Person> personsToRemove = santasList.getPersons().stream()
                .filter(person -> !updatedPersonIds.contains(person.getId()))
                .toList();

        // Remove the persons and delete them from database
        personsToRemove.forEach(person -> {
            santasList.getPersons().remove(person);
            personService.deletePerson(person.getId());
            log.debug("Removed and deleted person: {} (ID: {})", person.getName(), person.getId());
        });

        // Update existing persons and add new ones
        santasListDTO.persons().forEach(personDTO -> {
            if (personDTO.id() != null) {
                try {
                    Person existingPerson = personService.findById(personDTO.id());
                    existingPerson.setName(personDTO.name());
                    existingPerson.setEmail(personDTO.email());
                    log.debug("Updated existing person: {} (ID: {})", existingPerson.getName(), existingPerson.getId());
                } catch (ResourceNotFoundException e) {
                    Person newPerson = personMapper.toEntity(personDTO);
                    newPerson.setSantasList(santasList);
                    santasList.getPersons().add(newPerson);
                    log.debug("Added new person with existing ID: {} (ID: {})", newPerson.getName(), personDTO.id());
                }
            } else {
                Person newPerson = personMapper.toEntity(personDTO);
                newPerson.setSantasList(santasList);
                santasList.getPersons().add(newPerson);
                log.debug("Added new person: {}", newPerson.getName());
            }
        });

        SantasList updatedSantasList = santasListRepository.save(santasList);
        log.info("Saved updated SantasList. Final person count: {}", updatedSantasList.getPersons().size());

        return santasListMapper.toDto(updatedSantasList);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    public List<SantasListOverview> getListsOverviewsByUserId() {
        UUID userId = UUID.fromString((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return santasListRepository.findAllByOwnerUuid(userId)
                .stream()
                .map(SantasListOverview::new)
                .toList();
    }

    public void deleteSantasList(UUID id) {
        UUID userId = UUID.fromString((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        SantasList list = santasListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Santa's list not found with ID: " + id));

        // Check if user is NOT the owner
        if (!list.getOwner().getUuid().equals(userId)) {
            throw new UnauthorizedAccessException("You are not authorized to delete this Santa's list");
        }

        santasListRepository.deleteById(id);
    }

    public SantasListDTO getSantasListById(UUID id) {
        SantasList santasList = santasListRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Santa's list not found with ID: " + id));
        return santasListMapper.toDto(santasList);
    }

    public SantasListDTO updateStatus(UUID id, ListStatus listStatus){
        SantasList santasList = santasListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Santa's list not found with ID: " + id));

        santasList.setStatus(listStatus);
        return santasListMapper.toDto(santasListRepository.save(santasList));
    }

    public ListDetails getListDetails(UUID id) {
        SantasList santasList = santasListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Santa's list not found with ID: " + id));

        ListDetails listDetails = new ListDetails(
                santasList.getId(),
                santasList.getName(),
                santasList.getCreationDate(),
                santasList.getDueDate(),
                santasList.isLocked(),
                personService.getBySantasListId(id).stream()
                        .map(PersonOverview::new)
                        .collect(Collectors.toList()),
                santasList.getStatus().name()
        );
        log.debug("ListDetails created: {}", listDetails);
        return listDetails;
    }
}