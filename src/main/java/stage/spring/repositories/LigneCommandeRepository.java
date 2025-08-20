package stage.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import stage.spring.entities.LigneCommande;

public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {}

