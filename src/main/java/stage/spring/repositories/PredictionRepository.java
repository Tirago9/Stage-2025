package stage.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stage.spring.entities.Prediction;

import java.util.List;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    List<Prediction> findByUtilisateurId(Long userId);
    List<Prediction> findByZoneSurveilleId(Long zoneId);
    List<Prediction> findByUtilisateurIdAndZoneSurveilleId(Long userId, Long zoneId);
}
