package cz.oluwagbemiga.santa.be.repository;

import cz.oluwagbemiga.santa.be.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {
    // Custom query method to find persons by Santa's list ID
    List<Person> findBySantasListId(Long santasListId);

    // Custom query method to find persons by recipient ID
    List<Person> findByRecipientId(Long recipientId);
}
