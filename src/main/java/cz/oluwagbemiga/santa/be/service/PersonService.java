package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.dto.PersonDTO;
import cz.oluwagbemiga.santa.be.entity.Gift;
import cz.oluwagbemiga.santa.be.entity.Person;
import cz.oluwagbemiga.santa.be.exception.ResourceNotFoundException;
import cz.oluwagbemiga.santa.be.mapper.PersonMapper;
import cz.oluwagbemiga.santa.be.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    public List<PersonDTO> getBySantasListId(UUID santasListId) {
        // Fetch all persons associated with the given Santa's list ID
        List<Person> persons = personRepository.findBySantasListId(santasListId);

        // Map the entities to DTOs
        return personMapper.toDto(persons);
    }

    public Person findById(UUID personId) {
        // Fetch the person by ID
        return personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with ID: " + personId));
    }



    public PersonDTO createPerson(Person person) {
        Person savedPerson = personRepository.save(person);
        return personMapper.toDto(savedPerson);
    }

    public void assignPersonGift(Person person, Gift gift) {
        person.setDesiredGift(gift);
        Person updatedPerson = personRepository.save(person);
        personMapper.toDto(updatedPerson);

    }

    public void updatePerson(Person person) {
        personRepository.save(person);
    }

    public Person findByGiftId(UUID giftId) {
        return personRepository.findByDesiredGiftId(giftId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with Gift ID: " + giftId));
    }

    public PersonDTO createPerson(PersonDTO personDTO) {
        Person person = personMapper.toEntity(personDTO);

        Person savedPerson = personRepository.save(person);

        // Return the saved entity as a DTO
        return personMapper.toDto(savedPerson);
    }

    public void deletePerson(UUID personId) {
        // Delete the person by ID
        personRepository.deleteById(personId);
    }
}