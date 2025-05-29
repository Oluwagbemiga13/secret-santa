package cz.oluwagbemiga.santa.be.repository;

import cz.oluwagbemiga.santa.be.entity.Gift;
import cz.oluwagbemiga.santa.be.entity.GiftStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GiftRepository extends JpaRepository<Gift, UUID> {

    List<Gift> getAllByStatus(GiftStatus status);
}