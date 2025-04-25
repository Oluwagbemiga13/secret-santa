package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.dto.PersonDTO;
import cz.oluwagbemiga.santa.be.entity.Person;
import cz.oluwagbemiga.santa.be.mapper.PersonMapper;
import cz.oluwagbemiga.santa.be.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    public List<PersonDTO> getBySantasListId(Long santasListId) {
        // Fetch all persons associated with the given Santa's list ID
        List<Person> persons = personRepository.findBySantasListId(santasListId);

        // Map the entities to DTOs
        return personMapper.toDto(persons);
    }

    public PersonDTO createPerson(Person person) {
        Person savedPerson = personRepository.save(person);
        return personMapper.toDto(savedPerson);
    }

    public PersonDTO createPerson(PersonDTO personDTO) {
        Person person = personMapper.toEntity(personDTO);

        Person savedPerson = personRepository.save(person);

        // Return the saved entity as a DTO
        return personMapper.toDto(savedPerson);
    }

    public void deletePerson(Long personId) {
        // Delete the person by ID
        personRepository.deleteById(personId);
    }
}