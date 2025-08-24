package stage.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import stage.spring.entities.HistoriqueChatbot;

import java.time.LocalDateTime;
import java.util.List;

public interface HistoriqueChatbotRepository extends JpaRepository<HistoriqueChatbot, Long> {

    // Utilisateur par ID
    List<HistoriqueChatbot> findByUtilisateurIdAndDateBetweenOrderByDateAsc(Long userId, LocalDateTime start, LocalDateTime end);

    List<HistoriqueChatbot> findByUtilisateurIdOrderByDateAsc(Long userId);
}
