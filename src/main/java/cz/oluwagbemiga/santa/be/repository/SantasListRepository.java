package cz.oluwagbemiga.santa.be.repository;

import cz.oluwagbemiga.santa.be.entity.SantasList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SantasListRepository extends JpaRepository<SantasList, Long> {
}