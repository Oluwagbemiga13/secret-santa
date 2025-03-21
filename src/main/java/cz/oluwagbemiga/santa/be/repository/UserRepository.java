package cz.oluwagbemiga.santa.be.repository;

import cz.oluwagbemiga.santa.be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
