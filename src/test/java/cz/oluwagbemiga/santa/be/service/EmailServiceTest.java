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
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

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

        when(santasListService.findById(santasListDTO.id())).thenReturn(santasListDTO);
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

        verify(santasListService).findById(santasListDTO.id());
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

        when(santasListService.findById(santasListDTO.id())).thenReturn(santasListDTO);
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

        verify(santasListService).findById(santasListDTO.id());
        verify(personService).findById(any());
    }

    @Test
    void testSendRequest() {
        UUID listId = santasListDTO.id();
        UUID giftId = UUID.randomUUID();
        Gift gift = new Gift();
        gift.setId(giftId);

        when(santasListService.findById(listId)).thenReturn(santasListDTO);
        when(giftService.createGift(anyInt(), any())).thenReturn(giftDTO);
        when(giftMapper.toEntity(giftDTO)).thenReturn(gift);
        doNothing().when(personService).assignPersonGift(any(), any());

        emailServiceUnderTest.sendRequest(listId);

        verify(santasListService).findById(listId);
        verify(giftService).createGift(santasListDTO.budgetPerGift(),
                santasListDTO.dueDate().minusDays(1));
        verify(personService).assignPersonGift(personDTO.id(), gift);
        verify(santasListService).updateStatus(listId, ListStatus.PEOPLE_SELECTING_GIFTS);
        log.info("Send request completed for list: {}", listId);
    }

    @Test
    void testSendRequest_HandlesEmailException() throws MessagingException {
        UUID listId = santasListDTO.id();
        Gift gift = new Gift();
        gift.setId(UUID.randomUUID());

        when(santasListService.findById(listId)).thenReturn(santasListDTO);
        when(giftService.createGift(anyInt(), any())).thenReturn(giftDTO);
        when(giftMapper.toEntity(giftDTO)).thenReturn(gift);
        doNothing().when(personService).assignPersonGift(any(), any());
        doThrow(new MessagingException("Failed to send email"))
                .when(emailService).sendEmail(anyString(), anyString(), anyString());

        emailServiceUnderTest.sendRequest(listId);

        verify(santasListService).updateStatus(listId, ListStatus.PEOPLE_SELECTING_GIFTS);
        log.error("Failed to send request emails for list: {}", listId);
    }

    @Test
    void testBuildRequestContent() {
        String baseUrl = "http://localhost:8080";
        String giftFormUrl = "/gift-form/";
        UUID giftId = UUID.randomUUID();

        ReflectionTestUtils.setField(emailServiceUnderTest, "baseUrl", baseUrl);
        ReflectionTestUtils.setField(emailServiceUnderTest, "giftFormUrl", giftFormUrl);

        String content = emailServiceUnderTest.buildRequestContent(personDTO, santasListDTO, giftId);

        assertNotNull(content);
        assertTrue(content.contains(personDTO.name()));
        assertTrue(content.contains(santasListDTO.name()));
        assertTrue(content.contains(baseUrl + giftFormUrl + giftId));
        assertTrue(content.contains(santasListDTO.dueDate().toString()));
        log.info("Request content built successfully for person: {}", personDTO.name());
    }

    @Test
    void testSendEmail_WhenServiceDisabled() throws MessagingException {
        ReflectionTestUtils.setField(emailServiceUnderTest, "emailServiceEnabled", false);
        ReflectionTestUtils.setField(emailServiceUnderTest, "mailUsername", "test@test.com");

        emailServiceUnderTest.sendEmail("to@test.com", "subject", "content");

        verify(mailSender, never()).send(any(MimeMessage.class));
        log.info("Email service disabled - email not sent");
    }

    @Test
    void testSendEmail_WhenServiceEnabled() throws MessagingException {
        ReflectionTestUtils.setField(emailServiceUnderTest, "emailServiceEnabled", true);
        ReflectionTestUtils.setField(emailServiceUnderTest, "mailUsername", "test@test.com");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailServiceUnderTest.sendEmail("to@test.com", "subject", "content");

        verify(mailSender).send(any(MimeMessage.class));
        log.info("Email sent successfully");
    }

}