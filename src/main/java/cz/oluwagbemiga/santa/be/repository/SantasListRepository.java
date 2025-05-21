package cz.oluwagbemiga.santa.be.repository;

import cz.oluwagbemiga.santa.be.entity.ListStatus;
import cz.oluwagbemiga.santa.be.entity.SantasList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SantasListRepository extends JpaRepository<SantasList, UUID> {

    List<SantasList> findByStatus(ListStatus status);

    List<SantasList> findByIsLockedFalse();

    List<SantasList> findAllByOwnerUuid(UUID userUuid);
}