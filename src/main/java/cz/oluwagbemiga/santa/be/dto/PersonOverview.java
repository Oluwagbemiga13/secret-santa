package cz.oluwagbemiga.santa.be.dto;

import cz.oluwagbemiga.santa.be.entity.Person;

public record PersonOverview(
        String name,
        String email,
        boolean hasSelectedGift) {

    public PersonOverview(PersonDTO personDTO) {
        this(personDTO.name(), personDTO.email(), personDTO.hasSelectedGift());
    }
    public PersonOverview(Person person){
        this(
                person.getName(),
                person.getEmail(),
                person.getDesiredGift() != null
        );
    }
}
