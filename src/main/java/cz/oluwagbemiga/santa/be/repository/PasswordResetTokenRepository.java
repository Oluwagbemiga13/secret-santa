package cz.oluwagbemiga.santa.be.repository;

import cz.oluwagbemiga.santa.be.entity.PasswordResetToken;
import cz.oluwagbemiga.santa.be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(UUID token);
    boolean existsByUserAndExpiryDateAfter(User user, LocalDateTime now);
}

