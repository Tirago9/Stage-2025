package stage.spring.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stage.spring.entities.Commande;
import stage.spring.entities.LigneCommande;
import stage.spring.entities.Produit;
import stage.spring.entities.Utilisateur;
import stage.spring.repositories.CommandeRepository;
import stage.spring.repositories.LigneCommandeRepository;
import stage.spring.repositories.ProduitRepository;
import stage.spring.repositories.UtilisateurRepository;
import stage.spring.session.SessionUtilisateur;



import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/commandes")
public class CommandeController {

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private LigneCommandeRepository ligneCommandeRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;


    @Autowired
    private SessionUtilisateur sessionUtilisateur;

    @PostMapping("/{userId}")
    public ResponseEntity<String> passerCommande(
            @PathVariable Long userId,
            @RequestBody List<Map<String, Object>> produits) {

        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(userId);
        if (utilisateurOpt.isEmpty()) return ResponseEntity.badRequest().body("Utilisateur introuvable");

        Commande commande = new Commande();
        commande.setUtilisateur(utilisateurOpt.get());
        commandeRepository.save(commande);

        for (Map<String, Object> prodMap : produits) {
            Long produitId = Long.valueOf(prodMap.get("produitId").toString());
            int quantite = Integer.parseInt(prodMap.get("quantite").toString());

            Optional<Produit> produitOpt = produitRepository.findById(produitId);
            if (produitOpt.isEmpty()) continue;

            LigneCommande ligne = new LigneCommande();
            ligne.setCommande(commande);
            ligne.setProduit(produitOpt.get());
            ligne.setQuantite(quantite);
            ligneCommandeRepository.save(ligne);
        }

        return ResponseEntity.ok("Commande enregistrée avec succès");
    }


    @GetMapping("/commandes/utilisateur/{userId}")
    public ResponseEntity<List<Commande>> getCommandesByUtilisateur(@PathVariable Long userId) {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findById(userId);
        return utilisateur.map(u -> ResponseEntity.ok(u.getCommandes()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/commandes/{id}")
    public ResponseEntity<Commande> getCommandeById(@PathVariable Long id) {
        return commandeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/commandes/{commandeId}")
    public ResponseEntity<String> annulerCommande(@PathVariable Long commandeId) {
        Optional<Commande> commandeOpt = commandeRepository.findById(commandeId);
        if (commandeOpt.isEmpty()) return ResponseEntity.notFound().build();

        Commande commande = commandeOpt.get();
        if (!"traitée".equalsIgnoreCase(commande.getEtat())) {
            commandeRepository.delete(commande);
            return ResponseEntity.ok("Commande annulée avec succès");
        }
        return ResponseEntity.badRequest().body("Commande déjà traitée et non annulable");
    }

    @GetMapping("/produits/verifier-stock")
    public ResponseEntity<Boolean> verifierStock(@RequestParam Long produitId, @RequestParam int quantite) {
        Optional<Produit> produitOpt = produitRepository.findById(produitId);
        if (produitOpt.isEmpty()) return ResponseEntity.notFound().build();

        // Si le champ stock est ajouté à Produit, cette logique devra vérifier quantité disponible
        return ResponseEntity.ok(true); // Placeholder : implémenter si gestion de stock
    }


    // CommandeController.java
    @PostMapping("/orders")
    public ResponseEntity<String> createOrder(@RequestBody List<LigneCommande> lignes) {
        Utilisateur user = sessionUtilisateur.getUtilisateurConnecte();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Commande commande = new Commande();
        commande.setUtilisateur(user);
        commande.setDate(LocalDate.from(LocalDateTime.now()));
        commande.setEtat("NON_TRAITEE");
        commandeRepository.save(commande);

        for (LigneCommande ligneReq : lignes) {
            Produit produit = produitRepository.findById(ligneReq.getProduit().getId())
                    .orElseThrow(() -> new RuntimeException("Produit introuvable"));

            LigneCommande ligne = new LigneCommande();
            ligne.setCommande(commande);
            ligne.setProduit(produit);
            ligne.setQuantite(ligneReq.getQuantite());
            ligneCommandeRepository.save(ligne);
        }

        return ResponseEntity.ok("Commande créée avec succès. ID: " + commande.getId());
    }






}

