package stage.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import stage.spring.services.EmailService;

import java.util.Properties;

@SpringBootTest
public class EmailTest {

    @Autowired
    private JavaMailSender mailSender; // Déjà configuré par Spring

    @Test
    public void testEnvoiEmail() {
        EmailService emailService = new EmailService(mailSender);
        emailService.envoyerMailTest("klrayen671@gmail.com");
    }
}