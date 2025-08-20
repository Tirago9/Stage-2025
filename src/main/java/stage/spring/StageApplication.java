package stage.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import stage.spring.repositories.UtilisateurRepository;

@SpringBootApplication
public class StageApplication {

    public static void main(String[] args) {

        SpringApplication.run(StageApplication.class, args);
    }

}
