package stage.spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stage.spring.entities.HistoriqueChatbot;
import stage.spring.entities.Utilisateur;
import stage.spring.services.ChatbotService;
import stage.spring.session.SessionUtilisateur;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @Autowired
    private SessionUtilisateur sessionUtilisateur;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/message")
    public ResponseEntity<Map<String, String>> envoyerMessage(@RequestParam String message) {
        try {
            // Récupération de l'utilisateur connecté via la session
            Utilisateur user = sessionUtilisateur.getUtilisateurConnecte();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Aucun utilisateur connecté !"));
            }

            String reponse = chatbotService.sendPromptToChatBot(user, message);

            // Retourner une réponse structurée pour Angular
            return ResponseEntity.ok(Map.of(
                    "success", "true",
                    "message", reponse
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors du traitement: " + e.getMessage()));
        }
    }
    @GetMapping("/today")
    public List<HistoriqueChatbot> getConversationDuJour() {
        return chatbotService.getConversationDuJour();
    }

    @GetMapping("/history-by-day")
    public Map<LocalDate, List<HistoriqueChatbot>> getHistoriqueParJour() {
        return chatbotService.getHistoriqueParJour();
    }


    // ChatbotController.java
    @PostMapping("/chat/fire")
    public String askFireQuestion(@RequestParam String question) {
        Utilisateur user = sessionUtilisateur.getUtilisateurConnecte();
        if (user == null) {
            throw new RuntimeException("Utilisateur non connecté");
        }

        // Prompt optimisé pour les questions incendie
        String prompt = "Tu es un expert en détection d'incendies de forêt. " +
                "Réponds uniquement sur ce sujet de manière concise. " +
                "Question : " + question;

        return chatbotService.envoyerMessage(user, prompt);
    }


}
