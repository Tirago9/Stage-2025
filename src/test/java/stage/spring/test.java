package stage.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import stage.spring.services.EmailService;

public class test {

    @Test
    public void hello(){
        JavaMailSender javaMailSender = new JavaMailSenderImpl();
        EmailService emailService = new EmailService(javaMailSender);
        emailService.envoyerMailTest("klrayen671@gmail.com");
        System.out.println("envoie avec succe");

    }



}
