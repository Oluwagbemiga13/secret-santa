package cz.oluwagbemiga.santa.be.mapper;

import cz.oluwagbemiga.santa.be.config.EmailMockConfig;
import cz.oluwagbemiga.santa.be.dto.PersonDTO;
import cz.oluwagbemiga.santa.be.entity.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;


@SpringBootTest
@Import(EmailMockConfig.class)
public class PersonMapperTest extends AbstractMapperTest<Person, PersonDTO> {

    @Autowired
    private PersonMapper personMapper;

    UUID recipientId = UUID.randomUUID();

    @Override
    protected GenericMapper<Person, PersonDTO> mapper() {
        return personMapper;
    }

    @BeforeEach
    protected void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Override
    protected Person entityInstance() {

        Person recipient = new Person();
        recipient.setId(recipientId);

        return Person.builder()
                .name("John Doe")
                .id(UUID.fromString("3df567a8-5f13-4161-8f64-a549d466259e"))
                .email("email@email.email")
                .desiredGift(null)
                .hasSelectedGift(false)
                .recipient(recipient)
                .build();
    }

    @Override
    protected PersonDTO dtoInstance() {
        return new PersonDTO(
                UUID.fromString("3df567a8-5f13-4161-8f64-a549d466259e"),
                "John Doe",
                "email@email.email",
                null,
                recipientId);

    }

    @Test
    void testMapper() {
        test();
    }
}
