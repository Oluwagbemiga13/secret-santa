package cz.oluwagbemiga.santa.be.service;

import cz.oluwagbemiga.santa.be.dto.GiftDTO;
import cz.oluwagbemiga.santa.be.dto.PersonDTO;
import cz.oluwagbemiga.santa.be.dto.SantasListDTO;
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

    public void sendEmails(UUID santasListId) {

        log.info(SecurityContextHolder.getContext().getAuthentication().toString());
        SantasListDTO santasList = santasListService.getSantasListById(santasListId);

        for (PersonDTO personDTO : santasList.persons()) {
            GiftDTO giftDTO = giftService.createGift();
            Person person = personMapper.toEntity(personDTO);
            personService.assignPersonGift(person, giftMapper.toEntity(giftDTO));
            try {
                sendEmail(personDTO.email(), "Secret Santa List: " + santasList.name(),
                        buildEmailContent(personDTO, santasList, giftDTO.id()));
            } catch (MessagingException e) {
                log.error("Failed to send email to: {} - {}", personDTO.email(), e.getMessage());
            }
        }
        santasListService.updateStatus(santasListId, ListStatus.PEOPLE_SELECTING_GIFTS);
    }

    private void sendEmail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setFrom(mailUsername);
        helper.setSubject(subject);
        helper.setText(content, true); // true for HTML content

        mailSender.send(message);
    }


    private String buildEmailContent(PersonDTO person, SantasListDTO santasList, UUID giftId) {
        String giftFormFullUrl = baseUrl + giftFormUrl + giftId;

        return "<h1>Hello " + person.name() + "!</h1>" +
                "<p>You are part of the Secret Santa list: <strong>" + santasList.name() + "</strong>.</p>" +
                "<p>Due Date: " + santasList.name() + "</p>" +
                "<p>Please fill in the gift you want to receive at this link: " +
                "<a href='" + giftFormFullUrl + "'>Gift Form</a></p>" +
                "<p>Happy Holidays!</p>";
    }
}