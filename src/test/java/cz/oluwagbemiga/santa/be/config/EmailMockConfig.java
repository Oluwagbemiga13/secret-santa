package cz.oluwagbemiga.santa.be.config;

import cz.oluwagbemiga.santa.be.service.EmailService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;

@TestConfiguration
public class EmailMockConfig {

    @Bean
    @Primary
    public EmailService emailService() {
        return Mockito.mock(EmailService.class);
    }

    @Bean
    @Primary
    public JavaMailSender mailSender() {
        return Mockito.mock(JavaMailSender.class);
    }
}
