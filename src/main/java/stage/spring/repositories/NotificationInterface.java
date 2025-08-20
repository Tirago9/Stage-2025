package stage.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import stage.spring.entities.HistoriqueChatbot;
import stage.spring.entities.Notification;

public interface NotificationInterface extends JpaRepository<Notification, Long> {
}
