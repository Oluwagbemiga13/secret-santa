package cz.oluwagbemiga.santa.be.mapper;

import cz.oluwagbemiga.santa.be.dto.GiftDTO;
import cz.oluwagbemiga.santa.be.entity.Gift;
import cz.oluwagbemiga.santa.be.entity.GiftStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
public class GiftMapperTest extends AbstractMapperTest<Gift, GiftDTO> {

    @Override
    protected GenericMapper<Gift, GiftDTO> mapper() {
        return Mappers.getMapper(GiftMapper.class);
    }

    @Override
    protected Gift entityInstance() {
        return Gift.builder()
                .id(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"))
                .name("Book")
                .description("A great book")
                .affiliateLink("http://example.com/book")
                .budget(50)
                .expirationDate(LocalDate.of(2026, 1, 1))
                .status(GiftStatus.CREATED)
                .build();

    }

    @Override
    protected GiftDTO dtoInstance() {
        UUID id = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
        String name = "Book";
        String description = "A great book";
        String affiliateLink = "http://example.com/book";
        int budgetPerGift = 50;
        LocalDate expirationDate = LocalDate.of(2026, 1, 1);
        GiftStatus status = GiftStatus.CREATED;

        return new GiftDTO(id, name, description, affiliateLink, budgetPerGift, status, expirationDate);
    }

    @Test
    void testMapper() {
        test();
    }

}