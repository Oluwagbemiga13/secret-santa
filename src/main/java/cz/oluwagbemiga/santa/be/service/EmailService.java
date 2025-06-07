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

    public static boolean isValidEmail(String email) {
        if (email == null) return false;

        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(regex);
    }

    /**
     * Sends a request to all persons in the Santa's list to select their gifts.
     * Create gifts and attaches them to Persons
     *
     * @param santasListId
     */
    public void sendRequest(UUID santasListId) {
        UUID userId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().toString());

        SantasListDTO santasList = santasListService.getSantasListById(santasListId);

        if (santasList.ownerId().equals(userId))

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
    String buildRequestContent(PersonDTO person, SantasListDTO santasList, UUID giftId) {
        String giftFormFullUrl = baseUrl + giftFormUrl + giftId;

        StringBuilder sb = new StringBuilder();
        sb.append("<h1>Jolly Greetings, ").append(person.name()).append("! 🎅</h1>");
        sb.append("<p>You've been added to the magical Secret Santa list: <strong>")
                .append(santasList.name()).append("</strong>!</p>");
        sb.append("<p>The elves need your wish by the sleigh-launch deadline: <strong>")
                .append(santasList.dueDate()).append("</strong>.</p>");
        sb.append("<p>Click your little elf feet over to this link and tell us what you'd love to receive: ");
        sb.append("<a href='").append(giftFormFullUrl).append("'>Gift Form</a></p>");
        sb.append("<p>With sugarplum dreams and peppermint wishes,</p>");
        sb.append("<p>— Santa's Little Helpers 🎁</p>");

        return sb.toString();
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
        resultContent.append("<h1>Ho ho ho, ").append(person.getName()).append("! 🎄</h1>")
                .append("<p>You've been chosen to join the enchanted Secret Santa list: <strong>")
                .append(santasList.name()).append("</strong>! ✨</p>")
                .append("<p>Your festive mission is to find this magical gift: <strong>")
                .append(desiredGift.getName()).append("</strong> 🎁</p>")
                .append("<p>The North Pole budget limit is <strong>")
                .append(desiredGift.getBudget()).append("</strong> candy canes! 💰</p>")
                .append("<p>You’ll be bringing cheer to: <strong>")
                .append(recipient.getName()).append("</strong> ‍🎄</p>");

        if (desiredGift.getDescription() != null && !desiredGift.getDescription().isEmpty()) {
            resultContent.append("<p>The elves left a clue about the gift: ")
                    .append(desiredGift.getDescription()).append("</p>");
        }

        if (desiredGift.getStatus().equals(GiftStatus.LINKED)) {
            resultContent.append("<p>The elves scouted a perfect gift! Click here to see it: <strong><a href='")
                    .append(desiredGift.getAffiliateLink()).append("'>Magical Gift Link</a></strong> 🛷</p>");
        }

        resultContent.append("<p>We’re counting on you to spread joy and sparkle! ❄️</p>")
                .append("<p>Warm peppermint wishes,</p>")
                .append("<p>— Santa's Little Helpers </p>");

        return resultContent.toString();
    }

    void sendEmail(String to, String subject, String content) throws MessagingException {
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
        helper.setText(content, true);

        mailSender.send(message);
    }


}