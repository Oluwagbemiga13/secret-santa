package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.dto.GiftDTO;
import cz.oluwagbemiga.santa.be.dto.PersonDTO;
import cz.oluwagbemiga.santa.be.dto.SantasListDTO;
import cz.oluwagbemiga.santa.be.entity.Gift;
import cz.oluwagbemiga.santa.be.entity.GiftStatus;
import cz.oluwagbemiga.santa.be.entity.ListStatus;
import cz.oluwagbemiga.santa.be.entity.Person;
import cz.oluwagbemiga.santa.be.mapper.GiftMapper;
import cz.oluwagbemiga.santa.be.mapper.PersonMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final SantasListService santasListService;
    private final GiftService giftService;
    private final PersonMapper personMapper;
    private final GiftMapper giftMapper;
    private final PersonService personService;

    @Value("${secret-santa.fe.base-url}")
    private String baseUrl;

    @Value("${secret-santa.fe.gift-form-url}")
    private String giftFormUrl;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${secret-santa.email-service.enabled}")
    private boolean emailServiceEnabled;

    /**
     * Sends a request to all persons in the Santa's list to select their gifts.
     * Create gifts and attaches them to Persons
     *
     * @param santasListId
     */
    public void sendRequest(UUID santasListId) {

        log.info(SecurityContextHolder.getContext().getAuthentication().toString());
        SantasListDTO santasList = santasListService.getSantasListById(santasListId);

        for (PersonDTO personDTO : santasList.persons()) {
            GiftDTO giftDTO = giftService.createGift(
                    santasList.budgetPerGift(),
                    santasList.dueDate().minusDays(1));
            personService.assignPersonGift(personDTO.id(), giftMapper.toEntity(giftDTO));
            try {
                sendEmail(personDTO.email(), "Secret Santa List: " + santasList.name(),
                        buildRequestContent(personDTO, santasList, giftDTO.id()));
            } catch (MessagingException e) {
                log.error("Failed to send email to: {} - {}", personDTO.email(), e.getMessage());
            }
        }
        santasListService.updateStatus(santasListId, ListStatus.PEOPLE_SELECTING_GIFTS);
    }

    /**
     * Builds email content for requesting gift selection.
     *
     * @param person
     * @param santasList
     * @param giftId
     * @return
     */
    private String buildRequestContent(PersonDTO person, SantasListDTO santasList, UUID giftId) {
        String giftFormFullUrl = baseUrl + giftFormUrl + giftId;

        return "<h1>Hello " + person.name() + "!</h1>" +
                "<p>You are part of the Secret Santa list: <strong>" + santasList.name() + "</strong>.</p>" +
                "<p>Due Date: " + santasList.name() + "</p>" +
                "<p>Please fill in the gift you want to receive at this link: " +
                "<a href='" + giftFormFullUrl + "'>Gift Form</a></p>" +
                "<p>Happy Holidays!</p>";
    }

    public void sendResults(UUID santasListId) {
        SantasListDTO santasList = santasListService.getSantasListById(santasListId);

        for (PersonDTO personDTO : santasList.persons()) {
            try {
                sendEmail(personDTO.email(), "Secret Santa List: " + santasList.name(),
                        buildResultContent(personDTO, santasList));
                log.debug("Result sent to: {}", personDTO.email());
            } catch (MessagingException e) {
                log.error("Failed to send result to: {} - {}", personDTO.email(), e.getMessage());
            }
        }
    }

    public String buildResultContent(PersonDTO personDTO, SantasListDTO santasList) {
        Person person = personService.findById(personDTO.id());
        StringBuilder resultContent = new StringBuilder();
        Person recipient = person.getRecipient();
        Gift desiredGift = recipient.getDesiredGift();
        resultContent.append("<h1>Hello ").append(person.getName()).append("!</h1>")
                .append("<p>You are part of the Secret Santa list: <strong>").append(santasList.name()).append("</strong>.</p>")
                .append("<p>Due Date: ").append(santasList.name()).append("</p>")
                .append("<p>Your assigned gift is: <strong>").append(desiredGift.getName()).append("</strong></p>")
                .append("<p>Budget is <strong>").append(desiredGift.getBudget()).append("</strong> </p>")
                .append("<p>It should be purchased for: <strong>").append(recipient.getName()).append("</strong>");
        if (desiredGift.getDescription() == null) {
            resultContent.append("<p> Details: ").append(desiredGift.getDescription());
        }
        if (desiredGift.getStatus().equals(GiftStatus.LINKED)) {
            resultContent.append("<p>Elfes found perfect match. <strong> <a href='").append(desiredGift.getAffiliateLink()).append("'>");
        }
        resultContent.append("<p>Happy Holidays!</p>");
        return resultContent.toString();
    }

    private void sendEmail(String to, String subject, String content) throws MessagingException {
        if (!emailServiceEnabled) {
            log.warn("Email service is disabled. Email not sent to: {}", to);
            log.debug("Email content: {}", content);
            return;
        }
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setFrom(mailUsername);
        helper.setSubject(subject);
        helper.setText(content, true); // true for HTML content

        mailSender.send(message);
    }


}