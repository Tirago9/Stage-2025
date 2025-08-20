package stage.spring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import stage.spring.entities.Utilisateur;
import stage.spring.entities.ZoneSurveille;
import stage.spring.repositories.ZoneSurveilleRepository;
import stage.spring.session.SessionUtilisateur;

import java.time.LocalDate;
import java.util.List;

@Service
public class ZoneSurveilleService {

    @Autowired
    private ZoneSurveilleRepository zoneSurveilleRepository;

    @Autowired
    private SessionUtilisateur sessionUtilisateur;

    public ZoneSurveille ajouterZoneSurveille(ZoneSurveille zone) {
        // Récupérer le user connecté
        Utilisateur userConnecte = sessionUtilisateur.getUtilisateurConnecte();

        if (userConnecte == null) {
            throw new RuntimeException("Aucun utilisateur connecté !");
        }

        // Associer la zone au user connecté
        zone.setUtilisateur(userConnecte);
        zone.setDateAjout(LocalDate.now());

        return zoneSurveilleRepository.save(zone);
    }




}
