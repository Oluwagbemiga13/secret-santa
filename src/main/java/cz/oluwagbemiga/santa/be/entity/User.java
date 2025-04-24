package cz.oluwagbemiga.santa.be.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<SantasList> santasLists = new ArrayList<>();

    public void addSantasList(SantasList santasList) {
        santasList.setOwner(this);
        santasLists.add(santasList);
    }
    public void removeSantasList(SantasList santasList) {
        santasList.setOwner(null);
        santasLists.remove(santasList);
    }
}

