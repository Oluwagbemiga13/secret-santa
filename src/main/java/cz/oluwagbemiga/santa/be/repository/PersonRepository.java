package cz.oluwagbemiga.santa.be.repository;

import cz.oluwagbemiga.santa.be.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {
    // Custom query method to find persons by Santa's list ID
    List<Person> findBySantasListId(UUID santasListId);

    // Custom query method to find persons by recipient ID
    List<Person> findByRecipientId(UUID recipientId);

    Optional<Person> findByDesiredGiftId(UUID giftId);
}
