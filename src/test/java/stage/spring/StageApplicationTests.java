package stage.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import stage.spring.entities.RoleUtilisateur;
import stage.spring.entities.Utilisateur;
import stage.spring.repositories.UtilisateurRepository;

@SpringBootTest
class StageApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Test
    void testFindByEmail() {
        Utilisateur user = new Utilisateur();
        user.setNom("Rayen");
        user.setEmail("rayen@example.com");
        user.setMotdepasse("pass123");
        user.setRole(RoleUtilisateur.CLIENT);

        utilisateurRepository.save(user);
    }

}
