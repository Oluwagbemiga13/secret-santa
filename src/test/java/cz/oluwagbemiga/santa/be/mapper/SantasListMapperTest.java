package cz.oluwagbemiga.santa.be.mapper;

import cz.oluwagbemiga.santa.be.config.EmailMockConfig;
import cz.oluwagbemiga.santa.be.dto.SantasListDTO;
import cz.oluwagbemiga.santa.be.entity.ListStatus;
import cz.oluwagbemiga.santa.be.entity.SantasList;
import cz.oluwagbemiga.santa.be.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.UUID;

@SpringBootTest
@Import(EmailMockConfig.class)
class SantasListMapperTest extends AbstractMapperTest<SantasList, SantasListDTO> {

    private final UUID listUuid = UUID.randomUUID();
    private final UUID ownerId = UUID.randomUUID();
    private final LocalDate creationDate = LocalDate.now();
    private final LocalDate dueDate = creationDate.plusDays(30);


    @Autowired
    private SantasListMapper santasListMapper;

    @Override
    protected GenericMapper<SantasList, SantasListDTO> mapper() {
        return santasListMapper;
    }

    @Override
    protected SantasList entityInstance() {
        User owner = new User();
        owner.setUuid(ownerId);

        return SantasList.builder()
                .id(listUuid)
                .name("Christmas List")
                .creationDate(creationDate)
                .dueDate(dueDate)
                .isLocked(false)
                .persons(null)
                .status(ListStatus.CREATED)
                .owner(owner)
                .budgetPerGift(10)
                .build();
    }

    @Override
    protected SantasListDTO dtoInstance() {
        return new SantasListDTO(
                listUuid,
                "Christmas List",
                creationDate,
                dueDate,
                false,
                null,
                ListStatus.CREATED.getValue(),
                ownerId,
                10);
    }

    @Test
    void testMapper() {
        test();
    }
}