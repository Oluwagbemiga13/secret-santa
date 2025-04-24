package cz.oluwagbemiga.santa.be.repository;

import cz.oluwagbemiga.santa.be.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
