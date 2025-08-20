package stage.spring.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import stage.spring.entities.HistoriqueChatbot;
import stage.spring.entities.Utilisateur;
import stage.spring.repositories.HistoriqueChatbotRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class ChatbotService {

    private final HistoriqueChatbotRepository historiqueRepo;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String chatbotUrl = "http://localhost:5678/webhook/e2baa463-1892-4c78-bbad-ecc8b17f6479";

    public ChatbotService(HistoriqueChatbotRepository historiqueRepo) {
        this.historiqueRepo = historiqueRepo;
    }

    public String envoyerMessage(Utilisateur utilisateur, String messageUser) {
        // Préparer le corps de la requête à l’IA
        Map<String, String> payload = new HashMap<>();
        payload.put("message", messageUser);

        // Envoyer au chatbot et récupérer la réponse
        String reponseIA = restTemplate.postForObject(chatbotUrl, payload, String.class);

        // Sauvegarder dans l’historique
        HistoriqueChatbot historique = new HistoriqueChatbot();
        historique.setUtilisateur(utilisateur);
        historique.setMessageUser(messageUser);
        historique.setReponseIA(reponseIA);
        historique.setDate(LocalDateTime.now());
        historiqueRepo.save(historique);

        return reponseIA;
    }


    // 🔹 Conversation du jour
    public List<HistoriqueChatbot> getConversationDuJour(Utilisateur utilisateur) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(23, 59, 59);
        return historiqueRepo.findByUtilisateurAndDateBetweenOrderByDateAsc(utilisateur, start, end);
    }

    // 🔹 Historique regroupé par jour
    public Map<LocalDate, List<HistoriqueChatbot>> getHistoriqueParJour(Utilisateur utilisateur) {
        List<HistoriqueChatbot> allMessages = historiqueRepo.findByUtilisateurOrderByDateAsc(utilisateur);
        return allMessages.stream()
                .collect(Collectors.groupingBy(
                        msg -> msg.getDate().toLocalDate(), // clé = date sans heure
                        LinkedHashMap::new,                 // garder l'ordre
                        Collectors.toList()
                ));
    }
}

