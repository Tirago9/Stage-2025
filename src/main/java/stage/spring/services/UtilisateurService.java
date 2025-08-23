package stage.spring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import stage.spring.entities.Utilisateur;
import stage.spring.repositories.UtilisateurRepository;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public class EmailDejaUtiliseException extends RuntimeException {
        public EmailDejaUtiliseException(String message) {
            super(message);
        }
    }

    public Utilisateur inscription(Utilisateur utilisateur) {
        if (utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            throw new EmailDejaUtiliseException("Email déjà utilisé");
        }

        // Hasher le mot de passe
        utilisateur.setMotdepasse(passwordEncoder.encode(utilisateur.getMotdepasse()));
        return utilisateurRepository.save(utilisateur);
    }

    public Utilisateur connexion(String email, String motdepasse) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email);
        if (utilisateur == null) {
            throw new RuntimeException("Utilisateur introuvable");
        }

        if (!passwordEncoder.matches(motdepasse, utilisateur.getMotdepasse())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        return utilisateur;
    }

    public Utilisateur connexionAdmin(String email, String motdepasse) {
        Utilisateur admin = utilisateurRepository.findByEmail(email);
        if  (!passwordEncoder.matches(motdepasse, admin.getMotdepasse())) {
            throw new RuntimeException("Cordonnées admin incorrect");
        }
        return admin;
    }
}

