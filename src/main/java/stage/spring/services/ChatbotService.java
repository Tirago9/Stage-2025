package stage.spring.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import stage.spring.entities.HistoriqueChatbot;
import stage.spring.entities.Utilisateur;
import stage.spring.repositories.HistoriqueChatbotRepository;
import stage.spring.session.SessionUtilisateur;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class ChatbotService {

    @Autowired
    private SessionUtilisateur sessionUtilisateur;

    @Autowired
    private final HistoriqueChatbotRepository historiqueRepo;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String chatbotUrl = "http://localhost:5678/webhook/fea68e2b-32e7-44cb-ad14-aa5688e3e10e";

    public ChatbotService(HistoriqueChatbotRepository historiqueRepo, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.historiqueRepo = historiqueRepo;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
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





    public String sendPromptToChatBot(Utilisateur utilisateur, String prompt) {
        try {
            // 1. Préparer les headers avec le message dans la clé "message"
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("message", prompt); // Important : clé exacte attendue par l'agent IA

            HttpEntity<String> request = new HttpEntity<>(null, headers); // Pas de body

            // 2. Envoi de la requête
            System.out.println("Envoi à n8n avec header 'message': " + prompt);
            ResponseEntity<String> response = restTemplate.postForEntity(chatbotUrl, request, String.class);

            // Stocker le corps de la réponse pour l'utiliser deux fois
            String responseBody = response.getBody();
            String texteBrut = ""; // Variable pour stocker le texte brut

            // 3. Traitement de la réponse pour extraire le texte brut
            if (responseBody == null || responseBody.isEmpty()) {
                texteBrut = "Erreur: Réponse vide (code 200)";
            } else {
                try {
                    JsonNode root = objectMapper.readTree(responseBody);

                    if (root.isArray() && root.size() > 0) {
                        JsonNode firstItem = root.get(0);
                        if (firstItem.has("output")) {
                            texteBrut = firstItem.get("output").asText();
                        } else {
                            texteBrut = firstItem.toString();
                        }
                    } else if (root.has("output")) {
                        texteBrut = root.get("output").asText();
                    } else {
                        texteBrut = root.toString();
                    }
                } catch (Exception e) {
                    texteBrut = responseBody; // Retour brut si erreur de parsing
                }
            }

            // 4. Enregistrement dans la base de données
            HistoriqueChatbot historique = new HistoriqueChatbot();
            historique.setUtilisateur(utilisateur);
            historique.setMessageUser(prompt);
            historique.setReponseIA(texteBrut); // CHANGEMENT ICI: on enregistre le texte brut sans JSON
            historique.setDate(LocalDateTime.now());
            historiqueRepo.save(historique);

            System.out.println("Réponse brute: " + responseBody);
            System.out.println("Texte brut extrait: " + texteBrut);

            return texteBrut;

        } catch (HttpClientErrorException e) {
            String errorMessage = "Erreur HTTP: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();

            // Enregistrement de l'erreur
            HistoriqueChatbot historique = new HistoriqueChatbot();
            historique.setUtilisateur(utilisateur);
            historique.setMessageUser(prompt);
            historique.setReponseIA(errorMessage);
            historique.setDate(LocalDateTime.now());
            historiqueRepo.save(historique);

            return errorMessage;
        } catch (Exception e) {
            String errorMessage = "Erreur: " + e.getMessage();

            // Enregistrement de l'erreur
            HistoriqueChatbot historique = new HistoriqueChatbot();
            historique.setUtilisateur(utilisateur);
            historique.setMessageUser(prompt);
            historique.setReponseIA(errorMessage);
            historique.setDate(LocalDateTime.now());
            historiqueRepo.save(historique);

            return errorMessage;
        }
    }


    // 🔹 Conversation du jour avec ID
    public List<HistoriqueChatbot> getConversationDuJour() {
        Utilisateur user = sessionUtilisateur.getUtilisateurConnecte();
        System.out.println("Utilisateur connecté : " + user.getEmail() + user.getId());
        if (user == null) return Collections.emptyList();

        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(23, 59, 59);

        return historiqueRepo.findByUtilisateurIdAndDateBetweenOrderByDateAsc(user.getId(), start, end);
    }

    // 🔹 Historique complet par jour
    public Map<LocalDate, List<HistoriqueChatbot>> getHistoriqueParJour() {
        Utilisateur user = sessionUtilisateur.getUtilisateurConnecte();
        if (user == null) return Collections.emptyMap();

        List<HistoriqueChatbot> allMessages = historiqueRepo.findByUtilisateurIdOrderByDateAsc(user.getId());
        return allMessages.stream()
                .collect(Collectors.groupingBy(
                        msg -> msg.getDate().toLocalDate(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }


}

