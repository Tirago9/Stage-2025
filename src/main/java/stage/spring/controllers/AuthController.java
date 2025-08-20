package stage.spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stage.spring.entities.Utilisateur;
import stage.spring.repositories.UtilisateurRepository;
import stage.spring.services.EmailService;
import stage.spring.services.UtilisateurService;
import stage.spring.session.SessionUtilisateur;


import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private EmailService emailService;


    @Autowired
    private SessionUtilisateur sessionUtilisateur;

    @PostMapping("/signup")
    public Utilisateur signup(@RequestBody Utilisateur utilisateur) {
        Utilisateur newUser = utilisateurService.inscription(utilisateur);
        sessionUtilisateur.setUtilisateurConnecte(newUser);
        return newUser;
    }


    @PostMapping("/login")
    public Utilisateur login(@RequestParam String email, @RequestParam String motdepasse) {
        Utilisateur user = utilisateurService.connexion(email, motdepasse);
        sessionUtilisateur.setUtilisateurConnecte(user);

        int code = new Random().nextInt(900000) + 100000; // 100000 à 999999
        sessionUtilisateur.setEmailVerifCode(code);
        emailService.envoyerCodeVerification(email, code);


        return user;
    }



    @PostMapping("/loginadmin")
    public Utilisateur loginAdmin(@RequestParam String email, @RequestParam String motdepasse) {
        Utilisateur admin = utilisateurService.connexionAdmin(email, motdepasse);
        sessionUtilisateur.setUtilisateurConnecte(admin);
        return admin;
    }

    @PostMapping("/verify-email-code")
    public String verifierCodeEmail(@RequestParam int code) {
        Integer codeSession = sessionUtilisateur.getEmailVerifCode();
        if (codeSession != null && codeSession == code) {
            sessionUtilisateur.setEmailVerifCode(null);
            return "Vérification réussie, accès autorisé.";
        }
        return "Code de vérification invalide.";
    }

    @PostMapping("/logout")
    public String logout() {
        sessionUtilisateur.deconnecter();
        return "Déconnexion réussie";
    }

    @Autowired
    private UtilisateurRepository utilisateurRepository;


    @GetMapping("/utilisateur/{id}")
    public ResponseEntity<Utilisateur> getProfile(@PathVariable Long id) {
        return utilisateurRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profil")
    public ResponseEntity<Utilisateur> updateProfil(@RequestBody Utilisateur updated) {
        Utilisateur user = sessionUtilisateur.getUtilisateurConnecte();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Mise à jour directe de l'utilisateur connecté
        user.setNom(updated.getNom());
        user.setPrenom(updated.getPrenom());
        // ... autres champs
        return ResponseEntity.ok(utilisateurRepository.save(user));
    }

    @DeleteMapping("/utilisateur/{id}")
    public ResponseEntity<String> deleteCompte(@PathVariable Long id) {
        if (!utilisateurRepository.existsById(id)) return ResponseEntity.notFound().build();
        utilisateurRepository.deleteById(id);
        return ResponseEntity.ok("Compte supprimé avec succès");
    }



    @PutMapping("/profil")
    public ResponseEntity<Utilisateur> updateProfil(@RequestBody Utilisateur updated,SessionUtilisateur principal) {
        Optional<Utilisateur> utilisateur = Optional.ofNullable(utilisateurRepository.findByEmail(principal.getUtilisateurConnecte().getNom()));
        if (utilisateur.isPresent()) {
            Utilisateur u = utilisateur.get();
            u.setNom(updated.getNom());
            u.setPrenom(updated.getPrenom());
            u.setEmail(updated.getEmail());
            utilisateurRepository.save(u);
            return ResponseEntity.ok(u);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/profil")
    public ResponseEntity<String> deleteProfil(SessionUtilisateur principal) {
        Optional<Utilisateur> utilisateur = Optional.ofNullable(utilisateurRepository.findByEmail(principal.getUtilisateurConnecte().getNom()));
        if (utilisateur.isPresent()) {
            utilisateurRepository.delete(utilisateur.get());
            return ResponseEntity.ok("Compte supprimé avec succès.");
        }
        return ResponseEntity.notFound().build();
    }






//    @GetMapping("/whoami")
//    public Utilisateur getUtilisateurConnecte() {
//        if (sessionUtilisateur.estConnecte()) {
//            return sessionUtilisateur.getUtilisateurConnecte();
//        }
//        throw new RuntimeException("Pas d'utilisateur connecté");
//    }

}
