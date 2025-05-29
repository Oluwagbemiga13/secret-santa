package cz.oluwagbemiga.santa.be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Gift {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String description;

    private String affiliateLink;

    private int budget;

    private LocalDate expirationDate;

    @Builder.Default
    @Enumerated(EnumType.ORDINAL)
    private GiftStatus status = GiftStatus.CREATED;

    @OneToOne(mappedBy = "desiredGift")
    private Person person;

}