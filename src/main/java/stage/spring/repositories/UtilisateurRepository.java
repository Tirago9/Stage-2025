package stage.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stage.spring.entities.Utilisateur;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    boolean existsByEmail(String email);
    Utilisateur findByEmail(String email);
    List<Utilisateur> findByNomContainingIgnoreCase(String nom);
    Optional<Utilisateur> findOptionalByEmail(String email);
}
