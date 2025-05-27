package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.dto.*;
import cz.oluwagbemiga.santa.be.entity.ListStatus;
import cz.oluwagbemiga.santa.be.entity.Person;
import cz.oluwagbemiga.santa.be.entity.SantasList;
import cz.oluwagbemiga.santa.be.exception.InvalidRequestException;
import cz.oluwagbemiga.santa.be.exception.ResourceNotFoundException;
import cz.oluwagbemiga.santa.be.exception.UnauthorizedAccessException;
import cz.oluwagbemiga.santa.be.mapper.PersonMapper;
import cz.oluwagbemiga.santa.be.mapper.SantasListMapper;
import cz.oluwagbemiga.santa.be.repository.SantasListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * Validates the SantasListDTO object to ensure all required fields are present and valid.
     * Throws InvalidRequestException if any validation fails.
     *
     * @param santasListDTO
     */
    private void validateSantasList(SantasListDTO santasListDTO) {
        santasListDTO.persons().forEach(personDTO -> {
            if (personDTO.name() == null || personDTO.name().isBlank()) {
                throw new InvalidRequestException("Person name cannot be null or blank");
            }
            if (personDTO.email() == null || personDTO.email().isBlank()) {
                throw new InvalidRequestException("Person email cannot be null or blank");
            }
        });
        if (santasListDTO.name() == null || santasListDTO.name().isBlank()) {
            throw new InvalidRequestException("Santa's list name cannot be null or blank");
        }
        if (santasListDTO.budgetPerGift() <= 0) {
            throw new InvalidRequestException("Budget per gift must be greater than zero");
        }
    }

    /**
     * Creates a new Santa's list based on the provided SantasListDTO.
     *
     * @param santasListDTO
     * @return
     */
    public SantasListDTO createSantasList(SantasListDTO santasListDTO) {
        String userUuid = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        validateSantasList(santasListDTO);
        SantasList newList = SantasList.builder()
                .creationDate(LocalDate.now())
                .dueDate(santasListDTO.dueDate())
                .name(santasListDTO.name())
                .owner(userService.findUserById(userUuid))
                .budgetPerGift(santasListDTO.budgetPerGift())
                .build();

        santasListDTO.persons().forEach(personDTO -> {
            Person person = Person.builder()
                    .name(personDTO.name())
                    .email(personDTO.email())
                    .desiredGift(null)
                    .build();
            newList.addPerson(person);
        });

        SantasList savedList = santasListRepository.save(newList);
        return santasListMapper.toDto(savedList);
    }

    /**
     * Edits a person in the Santa's list.
     *
     * @param santasListId
     * @param personId
     * @param updatedPersonDTO
     * @return
     */
    public SantasListDTO editPersonInSantasList(UUID santasListId, UUID personId, PersonDTO updatedPersonDTO) {
        SantasList santasList = santasListRepository.findById(santasListId)
                .orElseThrow(() -> new ResourceNotFoundException("Santa's list not found with ID: " + santasListId));

        Person person = santasList.getPersons().stream()
                .filter(p -> p.getId().equals(personId))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("Person not found with ID: " + personId));

        person.setName(updatedPersonDTO.name());
        person.setEmail(updatedPersonDTO.email());

        SantasList updatedSantasList = santasListRepository.save(santasList);
        return santasListMapper.toDto(updatedSantasList);
    }

    /**
     * Deletes a person from the Santa's list and removes them from the database.
     *
     * @param santasListId
     * @param personId
     * @return
     */
    public SantasListDTO deletePersonFromSantasList(UUID santasListId, UUID personId) {
        SantasList santasList = santasListRepository.findById(santasListId)
                .orElseThrow(() -> new ResourceNotFoundException("Santa's list not found with ID: " + santasListId));

        Person person = santasList.getPersons().stream()
                .filter(p -> p.getId().equals(personId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with ID: " + personId));

        person.setSantasList(null);
        santasList.getPersons().remove(person);

        santasList = santasListRepository.save(santasList);

        personService.deletePerson(personId);

        return santasListMapper.toDto(santasList);
    }

    /**
     * Adds a new person to the Santa's list.
     *
     * @param santasListId
     * @param newPersonDTO
     * @return updated SantasListDTO
     */
    public SantasListDTO addPersonToSantasList(UUID santasListId, PersonDTO newPersonDTO) {
        SantasList santasList = santasListRepository.findById(santasListId)
                .orElseThrow(() -> new ResourceNotFoundException("Santa's list not found with ID: " + santasListId));

        Person newPerson = personMapper.toEntity(newPersonDTO);
        newPerson.setSantasList(santasList);

        santasList.getPersons().add(newPerson);

        SantasList updatedSantasList = santasListRepository.save(santasList);
        return santasListMapper.toDto(updatedSantasList);
    }

    public SantasListDTO updateSantasList(SantasListDTO santasListDTO) {
        return santasListMapper.toDto(santasListRepository.save(santasListMapper.toEntity(santasListDTO)));
    }

    /**
     * Updates only approved attributes. Excluding isLocked and creationDate.
     *
     * @param id
     * @param santasListDTO
     * @return updated SantasListDTO
     */
    public SantasListDTO updateSantasList(UUID id, SantasListDTO santasListDTO) {

        validateSantasList(santasListDTO);
        log.info("Starting update for SantasList ID: {}", id);

        SantasList santasList = santasListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Santa's list not found with ID: " + id));
        log.debug("Found existing SantasList: {}, with {} persons", santasList.getName(), santasList.getPersons().size());

        santasList.setName(santasListDTO.name());
        santasList.setDueDate(santasListDTO.dueDate());
        santasList.setBudgetPerGift(santasListDTO.budgetPerGift());

        List<UUID> updatedPersonIds = santasListDTO.persons().stream()
                .map(PersonDTO::id)
                .filter(Objects::nonNull)
                .toList();

        List<Person> personsToRemove = santasList.getPersons().stream()
                .filter(person -> !updatedPersonIds.contains(person.getId()))
                .toList();

        personsToRemove.forEach(person -> {
            santasList.getPersons().remove(person);
            personService.deletePerson(person.getId());
            log.debug("Removed and deleted person: {} (ID: {})", person.getName(), person.getId());
        });

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

    /**
     * Retrieves all Santa's lists owned by the currently authenticated user.
     *
     * @return List of SantasListOverview objects representing the user's lists.
     */
    public List<SantasListOverview> getListsOverviewsByUserId() {
        UUID userId = UUID.fromString((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return santasListRepository.findAllByOwnerUuid(userId)
                .stream()
                .map(SantasListOverview::new)
                .toList();
    }

    /**
     * Deletes a Santa's list by its ID.
     *
     * @param id
     */
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
                .orElseThrow(() -> new InvalidRequestException("Santa's list not found with ID: " + id));
        return santasListMapper.toDto(santasList);
    }

    public SantasListDTO updateStatus(UUID id, ListStatus listStatus) {
        SantasList santasList = santasListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Santa's list not found with ID: " + id));

        santasList.setStatus(listStatus);
        return santasListMapper.toDto(santasListRepository.save(santasList));
    }

    /**
     * Retrieves detailed information about a Santa's list by its ID.
     *
     * @param id
     * @return
     */
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
                santasList.getStatus().name(),
                santasList.getBudgetPerGift()
        );
        log.debug("ListDetails created: {}", listDetails);
        return listDetails;
    }

    /**
     * Retrieves all Santa's lists with the specified status.
     *
     * @param listStatus
     * @return
     */
    public List<SantasList> getAllByStatus(ListStatus listStatus) {
        return santasListRepository.findByStatus(listStatus);
    }
}