package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.dto.PersonDTO;
import cz.oluwagbemiga.santa.be.dto.SantasListDTO;
import cz.oluwagbemiga.santa.be.entity.Person;
import cz.oluwagbemiga.santa.be.entity.SantasList;
import cz.oluwagbemiga.santa.be.mapper.PersonMapper;
import cz.oluwagbemiga.santa.be.mapper.SantasListMapper;
import cz.oluwagbemiga.santa.be.repository.SantasListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SantasListService {

    private final SantasListRepository santasListRepository;
    private final SantasListMapper santasListMapper;
    private final PersonMapper personMapper;
    private final PersonService personService;
    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_USER')")
    public SantasListDTO createSantasList(SantasListDTO santasListDTO) {
        String userUuid = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        SantasList newList = santasListRepository.save(
                SantasList.builder()
                        .creationDate(LocalDate.now())
                        .dueDate(santasListDTO.dueDate())
                        .name(santasListDTO.name())
                        .owner(userService.findUserById(userUuid))
                        .build());

        santasListDTO.persons()
                .forEach(e ->
                        personService.createPerson(
                                Person.builder()
                                .name(e.name())
                                .email(e.email())
                                .desiredGift(e.desiredGift())
                                .santasList(newList)
                                .build()));

        return santasListMapper.toDto(santasListRepository.findById(newList.getId())
                .orElseThrow(() -> new IllegalArgumentException("Santa's list not found with ID: " + newList.getId())));
    }

    public SantasListDTO editPersonInSantasList(Long santasListId, Long personId, PersonDTO updatedPersonDTO) {
        SantasList santasList = santasListRepository.findById(santasListId)
                .orElseThrow(() -> new IllegalArgumentException("Santa's list not found with ID: " + santasListId));

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

    public SantasListDTO deletePersonFromSantasList(Long santasListId, Long personId) {
        SantasList santasList = santasListRepository.findById(santasListId)
                .orElseThrow(() -> new IllegalArgumentException("Santa's list not found with ID: " + santasListId));

        Person person = santasList.getPersons().stream()
                .filter(p -> p.getId().equals(personId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Person not found with ID: " + personId));

        santasList.getPersons().remove(person);

        SantasList updatedSantasList = santasListRepository.save(santasList);
        return santasListMapper.toDto(updatedSantasList);
    }

    public SantasListDTO addPersonToSantasList(Long santasListId, PersonDTO newPersonDTO) {
        SantasList santasList = santasListRepository.findById(santasListId)
                .orElseThrow(() -> new IllegalArgumentException("Santa's list not found with ID: " + santasListId));

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
    public SantasListDTO updateSantasList(Long id, SantasListDTO santasListDTO) {
        SantasList santasList = santasListRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Santa's list not found with ID: " + id));

        santasList.setName(santasListDTO.name());
        santasList.setDueDate(santasListDTO.dueDate());

        SantasList updatedSantasList = santasListRepository.save(santasList);
        return santasListMapper.toDto(updatedSantasList);
    }

    public void deleteSantasList(Long id) {
        if (!santasListRepository.existsById(id)) {
            throw new IllegalArgumentException("Santa's list not found with ID: " + id);
        }
        santasListRepository.deleteById(id);
    }

    public SantasListDTO getSantasListById(Long id) {
        SantasList santasList = santasListRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Santa's list not found with ID: " + id));
        return santasListMapper.toDto(santasList);
    }

}