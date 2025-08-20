package stage.spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import stage.spring.entities.HistoriqueChatbot;
import stage.spring.entities.Utilisateur;
import stage.spring.services.ChatbotService;
import stage.spring.session.SessionUtilisateur;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
    public String envoyerMessage(@RequestParam String message) {
        // Récupération de l'utilisateur connecté via la session
        Utilisateur user = sessionUtilisateur.getUtilisateurConnecte();
        if (user == null) {
            throw new RuntimeException("Aucun utilisateur connecté !");
        }

        return chatbotService.envoyerMessage(user, message);
    }
    // 🔹 Messages du jour
    @GetMapping("/today")
    public List<HistoriqueChatbot> getConversationDuJour() {
        Utilisateur user = sessionUtilisateur.getUtilisateurConnecte();
        return chatbotService.getConversationDuJour(user);
    }

    // 🔹 Historique regroupé par jour
    @GetMapping("/history-by-day")
    public Map<LocalDate, List<HistoriqueChatbot>> getHistoriqueParJour() {
        Utilisateur user = sessionUtilisateur.getUtilisateurConnecte();
        return chatbotService.getHistoriqueParJour(user);
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
