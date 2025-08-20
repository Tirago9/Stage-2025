package stage.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import stage.spring.entities.HistoriqueChatbot;
import stage.spring.entities.Utilisateur;

import java.time.LocalDateTime;
import java.util.List;

public interface HistoriqueChatbotRepository extends JpaRepository<HistoriqueChatbot, Long> {
    List<HistoriqueChatbot> findByUtilisateurAndDateBetweenOrderByDateAsc(
            Utilisateur utilisateur, LocalDateTime start, LocalDateTime end
    );

    // Tout l'historique trié
    List<HistoriqueChatbot> findByUtilisateurOrderByDateAsc(Utilisateur utilisateur);
}
