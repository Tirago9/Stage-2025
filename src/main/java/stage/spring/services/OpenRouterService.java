package stage.spring.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OpenRouterService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.max.tokens}")
    private int maxTokens;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String chat(String prompt) throws Exception {
        // 1. Construire le corps de la requête
        String requestBody = String.format("""
            {
                "model": "openai/gpt-3.5-turbo",
                "messages": [{"role": "user", "content": "%s"}],
                "max_tokens": %d
            }
            """, prompt.replace("\"", "\\\""), maxTokens);

        // 2. Créer la requête HTTP
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://openrouter.ai/api/v1/chat/completions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("HTTP-Referer", "https://votre-site.com")
                .header("X-Title", "Mon App Spring")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // 3. Envoyer la requête et récupérer la réponse
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        String jsonResponse = response.body();
        int contentStart = jsonResponse.indexOf("\"content\":\"") + 11;
        int contentEnd = jsonResponse.indexOf("\"", contentStart);
        String content = jsonResponse.substring(contentStart, contentEnd)
                .replace("\\n", "\n")
                .replace("\\\"", "\"");

        System.out.println("Réponse de l'IA: " + content);
        return content;
    }
}