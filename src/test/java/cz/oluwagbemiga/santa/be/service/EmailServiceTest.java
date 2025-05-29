package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.dto.GiftDTO;
import cz.oluwagbemiga.santa.be.dto.PersonDTO;
import cz.oluwagbemiga.santa.be.dto.SantasListDTO;
import cz.oluwagbemiga.santa.be.entity.Gift;
import cz.oluwagbemiga.santa.be.entity.GiftStatus;
import cz.oluwagbemiga.santa.be.entity.ListStatus;
import cz.oluwagbemiga.santa.be.entity.Person;
import cz.oluwagbemiga.santa.be.mapper.GiftMapper;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private SantasListService santasListService;

    @Mock
    private GiftService giftService;

    @Mock
    private PersonService personService;

    @Mock
    private GiftMapper giftMapper;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailService emailServiceUnderTest;

    private SantasListDTO santasListDTO;
    private PersonDTO personDTO;
    private PersonDTO recipientDTO;
    private GiftDTO giftDTO;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        // Mock SecurityContext and Authentication using spring-security-test
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UUID listId = UUID.randomUUID();
        UUID personId = UUID.randomUUID();
        UUID giftId = UUID.randomUUID();

        giftDTO = new GiftDTO(
                giftId,
                "Gift Name",
                "Gift Description",
                "Affiliate Link",
                100,
                GiftStatus.SELECTED,
                LocalDate.now().plusDays(1)
        );

        personDTO = new PersonDTO(
                personId,
                "John Doe",
                "john.doe@example.com",
                giftDTO,
                UUID.randomUUID()
        );


        santasListDTO = new SantasListDTO(
                listId,
                "Test List",
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                false,
                List.of(personDTO),
                ListStatus.CREATED.getValue(),
                personId,
                100
        );
    }


    @Test
    void testBuildResultContent() {
        Person person = mock(Person.class);
        Person recipient = mock(Person.class);
        Gift gift = mock(Gift.class);

        when(personService.findById(personDTO.id())).thenReturn(person);
        when(person.getRecipient()).thenReturn(recipient);
        when(person.getName()).thenReturn("Sender Name");
        when(recipient.getName()).thenReturn("Recipient Name");
        when(recipient.getDesiredGift()).thenReturn(gift);
        when(gift.getName()).thenReturn("Gift Name");
        when(gift.getBudget()).thenReturn(100);
        when(gift.getStatus()).thenReturn(GiftStatus.SELECTED);
        when(gift.getDescription()).thenReturn("");

        String result = emailServiceUnderTest.buildResultContent(personDTO, santasListDTO);

        assertNotNull(result);
        verify(gift).getStatus();
        verify(gift, atLeast(1)).getDescription();
    }

    @Test
    void testBuildResultContent_WithDescriptionAndLinkedGift() {
        Person person = mock(Person.class);
        Person recipient = mock(Person.class);
        Gift gift = mock(Gift.class);

        when(personService.findById(personDTO.id())).thenReturn(person);
        when(person.getRecipient()).thenReturn(recipient);
        when(person.getName()).thenReturn("Sender Name");
        when(recipient.getName()).thenReturn("Recipient Name");
        when(recipient.getDesiredGift()).thenReturn(gift);
        when(gift.getName()).thenReturn("Gift Name");
        when(gift.getBudget()).thenReturn(100);
        when(gift.getDescription()).thenReturn("Gift Description");
        when(gift.getStatus()).thenReturn(GiftStatus.LINKED);
        when(gift.getAffiliateLink()).thenReturn("http://example.com/gift");

        String result = emailServiceUnderTest.buildResultContent(personDTO, santasListDTO);

        assertNotNull(result);
        assertTrue(result.contains("Gift Description"));
        assertTrue(result.contains("http://example.com/gift"));
        verify(gift, atLeast(1)).getDescription();
    }

    @Test
    void testSendResults() throws MessagingException {
        Person person = mock(Person.class);
        Person recipient = mock(Person.class);
        Gift gift = mock(Gift.class);

        when(santasListService.getSantasListById(santasListDTO.id())).thenReturn(santasListDTO);
        when(personService.findById(any())).thenReturn(person);
        when(person.getRecipient()).thenReturn(recipient);
        when(recipient.getDesiredGift()).thenReturn(gift);
        when(person.getName()).thenReturn("Test Name");
        when(recipient.getName()).thenReturn("Recipient Name");
        when(gift.getName()).thenReturn("Gift Name");
        when(gift.getBudget()).thenReturn(100);
        when(gift.getStatus()).thenReturn(GiftStatus.SELECTED);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        emailServiceUnderTest.sendResults(santasListDTO.id());

        verify(santasListService).getSantasListById(santasListDTO.id());
        verify(personService).findById(any());
    }


    @Test
    void testBuildResultContent_WithoutDescriptionAndNotLinked() {
        Person person = mock(Person.class);
        Person recipient = mock(Person.class);
        Gift gift = mock(Gift.class);

        when(personService.findById(personDTO.id())).thenReturn(person);
        when(person.getRecipient()).thenReturn(recipient);
        when(person.getName()).thenReturn("Sender Name");
        when(recipient.getName()).thenReturn("Recipient Name");
        when(recipient.getDesiredGift()).thenReturn(gift);
        when(gift.getName()).thenReturn("Gift Name");
        when(gift.getBudget()).thenReturn(100);
        when(gift.getDescription()).thenReturn(null);
        when(gift.getStatus()).thenReturn(GiftStatus.SELECTED);

        String result = emailServiceUnderTest.buildResultContent(personDTO, santasListDTO);

        assertNotNull(result);
        assertFalse(result.contains("The elves left a clue about the gift"));
        assertFalse(result.contains("Magical Gift Link"));
        verify(gift, times(1)).getDescription();
    }

    @Test
    void testSendResults_HandlesEmailException() throws MessagingException {
        Person person = mock(Person.class);
        Person recipient = mock(Person.class);
        Gift gift = mock(Gift.class);

        when(santasListService.getSantasListById(santasListDTO.id())).thenReturn(santasListDTO);
        when(personService.findById(any())).thenReturn(person);
        when(person.getRecipient()).thenReturn(recipient);
        when(recipient.getDesiredGift()).thenReturn(gift);
        when(person.getName()).thenReturn("Test Name");
        when(recipient.getName()).thenReturn("Recipient Name");
        when(gift.getName()).thenReturn("Gift Name");
        when(gift.getBudget()).thenReturn(100);
        when(gift.getStatus()).thenReturn(GiftStatus.SELECTED);
        doThrow(new MessagingException("Failed to send email"))
                .when(emailService).sendEmail(anyString(), anyString(), anyString());

        emailServiceUnderTest.sendResults(santasListDTO.id());

        verify(santasListService).getSantasListById(santasListDTO.id());
        verify(personService).findById(any());
    }


}