package stage.spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import stage.spring.services.OpenRouterService;

@RestController
@RequestMapping("/api/ai")
public class OpenRouterController {

    private final OpenRouterService aiService;

    public OpenRouterController(OpenRouterService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/ask")
    public String askQuestion(@RequestParam String question) {
        try {
            return aiService.chat(question);
        } catch (Exception e) {
            return "Erreur : " + e.getMessage();
        }
    }
}
