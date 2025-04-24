package cz.oluwagbemiga.santa.be.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@Table(name = "santas_list")
public class SantasList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDate creationDate;

    private LocalDate dueDate;

    private boolean isLocked;

    @Builder.Default
    @OneToMany(mappedBy = "santasList", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Person> persons = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "owner_uuid", nullable = false)
    private User owner;


    public void addPerson(Person person) {
        persons.add(person);
        person.setSantasList(this);
    }

    public void removePerson(Person person) {
        persons.remove(person);
        person.setSantasList(null);


    }


}