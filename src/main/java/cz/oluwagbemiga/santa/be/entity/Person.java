package cz.oluwagbemiga.santa.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String email;

    @Builder.Default
    private boolean hasSelectedGift = false;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "desired_gift_id")
    private Gift desiredGift;

    @OneToOne(cascade = CascadeType.PERSIST)
    private Person recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "santas_list_id", nullable = false) // Ensure proper join column mapping
    private SantasList santasList;
}