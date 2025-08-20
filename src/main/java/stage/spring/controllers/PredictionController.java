package stage.spring.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import stage.spring.services.AgentService;
import stage.spring.services.PredictionService;
import stage.spring.services.ZoneSurveilleService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/prediction")
public class PredictionController {


    @Autowired
    private PredictionService PredictionService;

    @Autowired
    private AgentService agentService;

    @GetMapping("/data")
    public String getAllData(@RequestParam double lat, @RequestParam double lon) {
        return PredictionService.getAllDataAsString(lat, lon);
    }

    @GetMapping("/predict")
    public String getPrediction(@RequestParam double lat, @RequestParam double lon) {
        String Prompt = /*PredictionService.getAllDataAsString(lat, lon);*/ "hello";
        String clear=PredictionService.nettoyerMessage(Prompt);
        String res =  agentService.sendPromptToAgent(clear);
        return res;
    }


    @PostMapping("/ask")
    public String askAgent() {
        String res="parles moi de la tunisie3 ";
        String res2 =  agentService.sendPromptToAgent(res);
        System.out.println(res2);
        return res2;
    }

}
