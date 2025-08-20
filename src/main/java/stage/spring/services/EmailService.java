package stage.spring.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import stage.spring.exceptions.EmailException;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // Injection par constructeur (recommandé)
    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void envoyerMailTest(String to) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Test d'envoi mail");
            message.setText("Ceci est un mail de test envoyé depuis Spring Boot.");
            mailSender.send(message);
        } catch (Exception e) {
            throw new EmailException("Échec d'envoi du mail de test", e);
        }
    }

    public void envoyerCodeVerification(String email, int code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Code de vérification");
            message.setText("Votre code de vérification est : " + code);
            mailSender.send(message);
        } catch (Exception e) {
            throw new EmailException("Échec d'envoi du code de vérification", e);
        }
    }
}