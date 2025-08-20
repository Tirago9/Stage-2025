package stage.spring.services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class AgentService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String WEBHOOK_URL = "http://localhost:5678/webhook/e2baa463-1892-4c78-bbad-ecc8b17f6479";

    public AgentService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String sendPromptToAgent(String prompt) {
        try {
            // 1. Préparer les headers avec le message dans la clé "message"
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("message", prompt); // Important : clé exacte attendue par l'agent IA

            HttpEntity<String> request = new HttpEntity<>(null, headers); // Pas de body

            // 2. Envoi de la requête
            System.out.println("Envoi à n8n avec header 'message': " + prompt);
            ResponseEntity<String> response = restTemplate.postForEntity(WEBHOOK_URL, request, String.class);
            System.out.println("Réponse brute: " + response.getBody());

            // 3. Traitement de la réponse
            if (response.getBody() == null || response.getBody().isEmpty()) {
                return "Erreur: Réponse vide (code 200)";
            }

            try {
                JsonNode root = objectMapper.readTree(response.getBody());

                if (root.isArray() && root.size() > 0) {
                    JsonNode firstItem = root.get(0);
                    if (firstItem.has("output")) {
                        return firstItem.get("output").asText();
                    }
                    return firstItem.toString();
                }

                if (root.has("output")) {
                    return root.get("output").asText();
                }

                return root.toString();

            } catch (Exception e) {
                return response.getBody(); // Retour brut si erreur de parsing
            }

        } catch (HttpClientErrorException e) {
            return "Erreur HTTP: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();
        } catch (Exception e) {
            return "Erreur: " + e.getMessage();
        }
    }


}