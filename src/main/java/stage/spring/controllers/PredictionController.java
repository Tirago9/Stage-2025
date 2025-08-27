package stage.spring.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import stage.spring.entities.Notification;
import stage.spring.entities.Prediction;
import stage.spring.entities.Utilisateur;
import stage.spring.entities.ZoneSurveille;
import stage.spring.repositories.NotificationRepository;
import stage.spring.repositories.PredictionRepository;
import stage.spring.services.AgentService;
import stage.spring.services.PredictionService;
import stage.spring.services.ZoneSurveilleService;
import org.springframework.web.bind.annotation.*;
import stage.spring.session.SessionUtilisateur;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

//    @GetMapping("/predict")
//    public String getPrediction(@RequestParam double lat, @RequestParam double lon) {
//        String Prompt = PredictionService.getAllDataAsString(lat, lon);
//        System.out.println(Prompt);
//        String clear=PredictionService.nettoyerMessage(Prompt);
//        System.out.println(clear);
//        String res =  agentService.sendPromptToAgent(clear);
//        System.out.println(res);
//        return res;
//    }




    @Autowired
    private ZoneSurveilleService zoneSurveilleService;

    @GetMapping("/predict")
    public String getPrediction(@RequestParam double lat, @RequestParam double lon) {

        String Prompt = PredictionService.getAllDataAsString(lat, lon); // recuperation des données a partir les api's
        String clear=PredictionService.nettoyerMessage(Prompt); // nettoyage de message , genre enlever les {,[," ...
        String res =  agentService.sendPromptToAgent(clear); // envoie de message vers l'agent
        return res; // ici rends le return un truc qui va avec le frontend angular
    }





    @Autowired
    private SessionUtilisateur sessionUtilisateur;

    @Autowired
    private PredictionRepository predictionRepository;

    @Autowired
    private NotificationRepository notificationRepository;


    @PostMapping("/predict-zone/{zoneId}")
    public ResponseEntity<?> predictForZone(@PathVariable Long zoneId) {
        try {
            // Récupérer la zone et vérifier les autorisations
            ZoneSurveille zone = zoneSurveilleService.findById(zoneId);
            Utilisateur user = sessionUtilisateur.getUtilisateurConnecte();

            if (zone == null) {
                return ResponseEntity.badRequest().body("Zone non trouvée");
            }
            if (user == null || !zone.getUtilisateur().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès non autorisé");
            }

            // Appeler l'agent avec les coordonnées de la zone
            // String agentResponse = predictionService.getPrediction(zone.getLaltitude(), zone.getLongitude()); =>hethi s7i7a
            String agentResponse = "```json\n" +
                    "{\n" +
                    "  \"scoreRisque\": 95,\n" +
                    "  \"niveauRisque\": \"CRITIQUE\",\n" +
                    "  \"explication\": \"Le risque d'incendie pour cette zone est classé comme critique, avec un score de 95%. Cette évaluation est principalement due à la présence de trois feux actifs détectés par NASA FIRMS le 25 août 2025. " +
                    "Le feu le plus proche se situe à environ 12.8 kilomètres de votre position, avec une intensité de 0.26 FRP (Fire Radiative Power). Les températures maximales journalières atteignant jusqu'à 35.6°C (rapportées par Open-Meteo et VisualCrossing) contribuent fortement à un environnement propice à la propagation des incendies. " +
                    "L'humidité relative actuelle est de 43%, ce qui est modéré mais reste sec dans ce contexte de chaleur. La qualité de l'air est jugée modérée avec un indice AQI de 53 et une concentration de PM2.5 élevée (48.47 µg/m³), ce qui pourrait indiquer des particules provenant de feux existants ou des conditions atmosphériques défavorables. " +
                    "La vitesse du vent actuelle est faible, environ 4.7 km/h, ce qui est un facteur atténuant à court terme, mais les conditions régionales indiquent une activité des feux de forêt supérieure à la normale. L'altitude de 186 mètres n'est pas un facteur aggravant direct pour ce point.\",\n" +
                    "  \"causesPrincipales\": \"Feux actifs à proximité, Températures élevées, Humidité modérément basse, Qualité de l'air\",\n" +
                    "  \"temperature\": 21.2,\n" +
                    "  \"humidite\": 43.0,\n" +
                    "  \"vitesseVent\": 4.7,\n" +
                    "  \"pressionAtmospherique\": 1012.0,\n" +
                    "  \"indiceQualiteAir\": 53,\n" +
                    "  \"concentrationPM25\": 48.47,\n" +
                    "  \"nombreFeuxProches\": 3,\n" +
                    "  \"distanceFeuxPlusProche\": 12.8,\n" +
                    "  \"intensiteFeuxMax\": 1.99,\n" +
                    "  \"altitude\": 186.0,\n" +
                    "  \"sourcesDonnees\": \"Weatherstack, VisualCrossing, Open-Meteo, WeatherAPI, NASA FIRMS, Open-Elevation, IQAir, SerpAPI\"\n" +
                    "}\n" +
                    "```";

            // Extraire et sauvegarder la prédiction
            Prediction prediction = PredictionService.extractPredictionFromResponse(agentResponse, zone, user);
            Prediction savedPrediction = predictionRepository.save(prediction);

            Notification notification = new Notification("Resultat Prediction disponible sur la zone : "+zone.getNom()
                    +" faite le :"+prediction.getDatePrediction(),"notification","non lu",(LocalDateTime.now()),"pas d'image",user);

            notificationRepository.save(notification);
            // Retourner toute l'entité Prediction pour le frontend
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "prediction", savedPrediction
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }


    @PostMapping("/ask")
    public String askAgent() {
        String res="parles moi de la tunisie3 ";
        String res2 =  agentService.sendPromptToAgent(res);
        System.out.println(res2);
        return res2;
    }

}
