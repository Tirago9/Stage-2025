package stage.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import stage.spring.entities.Produit;

import java.util.List;

public interface ProduitRepository extends JpaRepository<Produit,Long> {
    List<Produit> findByNomContainingIgnoreCase(String nom);
}
