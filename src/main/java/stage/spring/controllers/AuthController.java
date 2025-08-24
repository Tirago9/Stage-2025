package stage.spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import stage.spring.entities.Utilisateur;
import stage.spring.repositories.UtilisateurRepository;
import stage.spring.services.EmailService;
import stage.spring.services.UtilisateurService;
import stage.spring.session.SessionUtilisateur;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;


@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
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
    public ResponseEntity<?> signup(@RequestBody Utilisateur utilisateur) {
        try {
            Utilisateur newUser = utilisateurService.inscription(utilisateur);
            sessionUtilisateur.setUtilisateurConnecte(newUser);
            return ResponseEntity.ok(newUser);
        } catch (UtilisateurService.EmailDejaUtiliseException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Erreur lors de l'inscription: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String motdepasse = credentials.get("password");

            Utilisateur user = utilisateurService.connexion(email, motdepasse);
            sessionUtilisateur.setUtilisateurConnecte(user);

            int code = new Random().nextInt(900000) + 100000;
            sessionUtilisateur.setEmailVerifCode(code);
            emailService.envoyerCodeVerification(email, code);

            // Retourner une réponse structurée
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Connexion réussie");
            response.put("user", Map.of(
                    "id", user.getId(),
                    "nom", user.getNom(),
                    "prenom", user.getPrenom(),
                    "email", user.getEmail()
            ));
            response.put("status", "success");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Email ou mot de passe incorrect");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }




    @PostMapping("/loginadmin")
    public Utilisateur loginAdmin(@RequestParam String email, @RequestParam String motdepasse) {
        Utilisateur admin = utilisateurService.connexionAdmin(email, motdepasse);
        sessionUtilisateur.setUtilisateurConnecte(admin);
        return admin;
    }

    @PostMapping("/verify-email-code")
    public ResponseEntity<Map<String, String>> verifierCodeEmail(@RequestBody Map<String, Integer> requestBody) {
        Integer code = requestBody.get("code");
        Integer codeSession = sessionUtilisateur.getEmailVerifCode();
        System.out.println(code);
        System.out.println(codeSession);

        Map<String, String> response = new HashMap<>();

        if (codeSession != null && codeSession.equals(code)) {
            response.put("message", "Vérification réussie, accès autorisé.");
            return ResponseEntity.ok(response);
        }

        response.put("message", "Code de vérification invalide.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @PostMapping("/logout")
    public String logout() {
        sessionUtilisateur.deconnecter();
        return "Déconnexion réussie";
    }

    @Autowired
    private UtilisateurRepository utilisateurRepository;





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








    @GetMapping("/whoami")
    public ResponseEntity<?> getUtilisateurConnecte() {
        Utilisateur u = sessionUtilisateur.getUtilisateurConnecte();
        if (u != null) {
            // Créer un DTO léger
            Map<String, Object> userDto = Map.of(
                    "id", u.getId(),
                    "nom", u.getNom(),
                    "prenom", u.getPrenom(),
                    "email", u.getEmail()
            );
            return ResponseEntity.ok(userDto);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Pas d'utilisateur connecté"));
    }



}
