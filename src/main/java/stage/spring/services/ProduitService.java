package stage.spring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stage.spring.entities.Produit;
import stage.spring.repositories.ProduitRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProduitService {

    @Autowired
    private ProduitRepository produitRepository;

    // Ajouter un produit
    public Produit ajouterProduit(Produit produit) {
        return produitRepository.save(produit);
    }

    // Récupérer tous les produits
    public List<Produit> getAllProduits() {
        return produitRepository.findAll();
    }

    // Récupérer un produit par ID
    public Optional<Produit> getProduitById(Long id) {
        return produitRepository.findById(id);
    }

    // Modifier un produit
    public Produit modifierProduit(Long id, Produit updatedProduit) {
        return produitRepository.findById(id).map(produit -> {
            produit.setNom(updatedProduit.getNom());
            produit.setDescription(updatedProduit.getDescription());
            produit.setImage(updatedProduit.getImage());
            produit.setImage3D(updatedProduit.getImage3D());
            return produitRepository.save(produit);
        }).orElse(null);
    }

    // Supprimer un produit
    public boolean supprimerProduit(Long id) {
        if (produitRepository.existsById(id)) {
            produitRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Rechercher un produit par nom
    public List<Produit> rechercherParNom(String nom) {
        return produitRepository.findByNomContainingIgnoreCase(nom);
    }

    // Supprimer tous les produits
    public void supprimerTousLesProduits() {
        produitRepository.deleteAll();
    }
}

