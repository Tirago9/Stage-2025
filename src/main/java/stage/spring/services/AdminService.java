package stage.spring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stage.spring.entities.Utilisateur;
import stage.spring.repositories.UtilisateurRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // Lire tous les utilisateurs
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    // Rechercher par nom
    public List<Utilisateur> searchByNom(String nom) {
        return utilisateurRepository.findByNomContainingIgnoreCase(nom);
    }

    // Rechercher par email
    public Optional<Utilisateur> searchByEmail(String email) {
        return utilisateurRepository.findOptionalByEmail(email);
    }

    // Supprimer par ID
    public boolean deleteById(Long id) {
        if (utilisateurRepository.existsById(id)) {
            utilisateurRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Supprimer par email
    public boolean deleteByEmail(String email) {
        Optional<Utilisateur> userOpt = utilisateurRepository.findOptionalByEmail(email);
        if (userOpt.isPresent()) {
            utilisateurRepository.delete(userOpt.get());
            return true;
        }
        return false;
    }
}

