package stage.spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stage.spring.entities.Produit;
import stage.spring.services.ProduitService;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
@CrossOrigin(origins = "*") // autorise les requêtes CORS
public class ProduitController {

    @Autowired
    private ProduitService produitService;

    // ➕ Ajouter un produit
    @PostMapping
    public ResponseEntity<Produit> ajouterProduit(@RequestBody Produit produit) {
        return ResponseEntity.ok(produitService.ajouterProduit(produit));
    }

    // 🔁 Modifier un produit
    @PutMapping("/{id}")
    public ResponseEntity<?> modifierProduit(@PathVariable Long id, @RequestBody Produit produit) {
        Produit updated = produitService.modifierProduit(id, produit);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // 🔍 Rechercher tous les produits
    @GetMapping
    public ResponseEntity<List<Produit>> getAllProduits() {
        return ResponseEntity.ok(produitService.getAllProduits());
    }

    // 🔍 Rechercher par ID
    @GetMapping("/{id}")
    public ResponseEntity<Produit> getProduitById(@PathVariable Long id) {
        return produitService.getProduitById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔍 Rechercher par nom
    @GetMapping("/search")
    public ResponseEntity<List<Produit>> searchByNom(@RequestParam String nom) {
        return ResponseEntity.ok(produitService.rechercherParNom(nom));
    }

    // ❌ Supprimer un produit
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerProduit(@PathVariable Long id) {
        return produitService.supprimerProduit(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    // ❌ Supprimer tous les produits
    @DeleteMapping
    public ResponseEntity<?> supprimerTous() {
        produitService.supprimerTousLesProduits();
        return ResponseEntity.ok().build();
    }
}

