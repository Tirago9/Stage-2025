package stage.spring.session;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import stage.spring.entities.Utilisateur;

@Component
@SessionScope // Indique que cet objet vit le temps de la session HTTP
public class SessionUtilisateur {

    private Utilisateur utilisateurConnecte;
    private Integer emailVerifCode;

    public Integer getEmailVerifCode() {
        return emailVerifCode;
    }

    public void setEmailVerifCode(Integer emailVerifCode) {
        this.emailVerifCode = emailVerifCode;
    }

    public Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public void setUtilisateurConnecte(Utilisateur utilisateurConnecte) {
        if (this.utilisateurConnecte==null) {
            this.utilisateurConnecte = utilisateurConnecte;
        }
    }

    public boolean estConnecte() {
        return utilisateurConnecte != null;
    }

    public void deconnecter() {
        this.utilisateurConnecte = null;
        this.emailVerifCode = null;
    }
}
