package stage.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import stage.spring.entities.Utilisateur;
import stage.spring.entities.ZoneSurveille;

import java.util.List;
import java.util.Optional;

public interface ZoneSurveilleRepository extends JpaRepository<ZoneSurveille, Long> {

    List<ZoneSurveille> findByUtilisateur(Utilisateur user);
    Optional<ZoneSurveille> findById(Long id);
}