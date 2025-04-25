package cz.oluwagbemiga.santa.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String desiredGift;

    @OneToOne(cascade = CascadeType.PERSIST)
    private Person recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    private SantasList santasList;
}