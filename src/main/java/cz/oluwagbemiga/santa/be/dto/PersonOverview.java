package cz.oluwagbemiga.santa.be.dto;

import cz.oluwagbemiga.santa.be.entity.Person;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PersonOverview(
        @NotBlank(message = "Person name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        String email,

        boolean hasSelectedGift) {

    public PersonOverview(PersonDTO personDTO) {
        this(personDTO.name(), personDTO.email(), personDTO.hasSelectedGift());
    }

    public PersonOverview(Person person) {
        this(
                person.getName(),
                person.getEmail(),
                person.getDesiredGift() != null
        );
    }
}
