package stage.spring;

import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import stage.spring.entities.HistoriqueChatbot;
import stage.spring.repositories.HistoriqueChatbotRepository;
import stage.spring.services.EmailService;

import java.util.List;


@SpringBootTest
public class test {

    @Autowired
    private HistoriqueChatbotRepository historiqueRepo;

    @Test
    public void testAfficherTousMessages() {
        List<HistoriqueChatbot> h = historiqueRepo.findAll();
        h.forEach(m -> {
            if (m.getUtilisateur() != null) {
                System.out.println(m.getId() + " - " + m.getUtilisateur().getId() + " - " + m.getDate());
            } else {
                System.out.println(m.getId() + " - UTILISATEUR NULL - " + m.getDate());
            }
        });
    }



}
