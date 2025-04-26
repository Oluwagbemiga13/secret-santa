package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.dto.PersonDTO;
import cz.oluwagbemiga.santa.be.dto.SantasListDTO;
import cz.oluwagbemiga.santa.be.dto.SantasListOverview;
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
                    .desiredGift(personDTO.desiredGift())
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
        person.setDesiredGift(updatedPersonDTO.desiredGift());
        person.setEmail(updatedPersonDTO.email());

        SantasList updatedSantasList = santasListRepository.save(santasList);
        return santasListMapper.toDto(updatedSantasList);
    }

    public SantasListDTO deletePersonFromSantasList(UUID santasListId, Long personId) {
        SantasList santasList = santasListRepository.findById(santasListId)
                .orElseThrow(() -> new IllegalArgumentException("Santa's list not found with ID: " + santasListId));

        Person person = santasList.getPersons().stream()
                .filter(p -> p.getId().equals(personId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with ID: " + personId));

        santasList.getPersons().remove(person);

        SantasList updatedSantasList = santasListRepository.save(santasList);
        return santasListMapper.toDto(updatedSantasList);
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

        santasList.setName(santasListDTO.name());
        santasList.setDueDate(santasListDTO.dueDate());

        log.info("Current persons in list: {}",
                santasList.getPersons().stream()
                        .map(p -> String.format("ID: %s, Name: %s", p.getId(), p.getName()))
                        .collect(Collectors.joining(", "))
        );

        log.info("Incoming persons in DTO: {}",
                santasListDTO.persons().stream()
                        .map(p -> String.format("ID: %s, Name: %s", p.id(), p.name()))
                        .collect(Collectors.joining(", "))
        );

        // Clear the persons collection and rebuild it
        santasList.getPersons().clear();
        log.debug("Cleared existing persons collection");

        // Update/create persons
        santasListDTO.persons().forEach(personDTO -> {
            Person person;
            if (personDTO.id() != null) {
                log.debug("Attempting to update existing person with ID: {}", personDTO.id());
                try {
                    person = personService.findById(personDTO.id());
                    log.info("Found existing person: {} (ID: {})", person.getName(), person.getId());
                    person.setName(personDTO.name());
                    person.setEmail(personDTO.email());
                    person.setDesiredGift(personDTO.desiredGift());
                } catch (ResourceNotFoundException e) {
                    log.warn("Person with ID {} not found, creating new", personDTO.id());
                    person = personMapper.toEntity(personDTO);
                }
            } else {
                log.debug("Creating new person: {}", personDTO.name());
                person = personMapper.toEntity(personDTO);
            }
            person.setSantasList(santasList);
            santasList.getPersons().add(person);
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

}