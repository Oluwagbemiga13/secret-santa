package cz.oluwagbemiga.santa.be.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "password_reset_tokens")
@Data
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID token;

    @OneToOne
    @JoinColumn(nullable = false, name = "user_uuid")
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiryDate;


}

