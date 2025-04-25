package cz.oluwagbemiga.santa.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@Table(name = "santas_list")
@NoArgsConstructor
@AllArgsConstructor
public class SantasList {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private LocalDate creationDate;

    private LocalDate dueDate;

    private boolean isLocked;

    @Builder.Default
    private ListStatus status = ListStatus.CREATED;

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