package stage.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stage.spring.entities.Commande;

import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {

    // Trouver toutes les commandes d'un utilisateur par son id
    List<Commande> findByUtilisateurId(Long utilisateurId);

    // Tu peux ajouter d'autres méthodes personnalisées ici si besoin
}
