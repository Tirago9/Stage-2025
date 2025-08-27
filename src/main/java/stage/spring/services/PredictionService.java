package stage.spring.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stage.spring.entities.NiveauRisque;
import stage.spring.entities.Prediction;
import stage.spring.entities.Utilisateur;
import stage.spring.entities.ZoneSurveille;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PredictionService{

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private void validateCoordinates(double lat, double lon) {
        if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
            throw new IllegalArgumentException("Latitude/Longitude invalide : " + lat + ", " + lon);
        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private String sendGetRequest(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        return sendRequest(request);
    }

    private String sendRequest(HttpRequest request) throws Exception {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String getWeatherFromWeatherstack(double latitude, double longitude) throws Exception {
        validateCoordinates(latitude, longitude);
        String url = String.format(Locale.US,"http://api.weatherstack.com/current?access_key=a4b1746cbf4bf333a3b5e0b8a0ff6ede&query=%f,%f", latitude, longitude);
        return sendGetRequest(url);
    }

    public String getWeatherFromVisualCrossing(double latitude, double longitude) throws Exception {
        validateCoordinates(latitude, longitude);
        String url = String.format(Locale.US,"https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/%.6f,%.6f?key=KUGMZMLV2U7C78D8NV9KVBV3F&include=current", latitude, longitude);
        return sendGetRequest(url);
    }

    public String getWeatherFromOpenMeteo(double latitude, double longitude) throws Exception {
        validateCoordinates(latitude, longitude);
        String url = String.format(Locale.US,"https://api.open-meteo.com/v1/forecast?latitude=%.6f&longitude=%.6f&hourly=temperature_2m,wind_speed_10m&forecast_days=1&temporal_resolution=hourly_6", latitude, longitude);
        return sendGetRequest(url);
    }

    public String getWeatherFromWeatherAPI(double latitude, double longitude) throws Exception {
        validateCoordinates(latitude, longitude);
        String url = String.format(Locale.US,"http://api.weatherapi.com/v1/current.json?key=a7ee8e59a8fb477cb61155201251408&q=%f,%f", latitude, longitude);
        return sendGetRequest(url);
    }

    public String getNearestFiresFromNASA(double latitude, double longitude) throws Exception {
        validateCoordinates(latitude, longitude);
        String url = "https://firms.modaps.eosdis.nasa.gov/api/area/csv/00041452a455c47776f553e300b70f02/VIIRS_NOAA20_NRT/world/1";
        String response = sendGetRequest(url);

        String[] lines = response.split("\n");
        if (lines.length <= 1) return "Aucun feu détecté.";

        String[] headers = lines[0].split(",");
        int latIndex = Arrays.asList(headers).indexOf("latitude");
        int lonIndex = Arrays.asList(headers).indexOf("longitude");

        if (latIndex == -1 || lonIndex == -1) return "Colonnes latitude/longitude introuvables.";

        return Arrays.stream(lines).skip(1)
                .map(line -> line.split(","))
                .filter(parts -> parts.length > Math.max(latIndex, lonIndex))
                .sorted(Comparator.comparingDouble(parts -> distance(latitude, longitude,
                        Double.parseDouble(parts[latIndex]), Double.parseDouble(parts[lonIndex]))))
                .limit(3)
                .map(parts -> String.format("Feu: lat=%s, lon=%s, date=%s, satellite=%s, frp=%s, daynight=%s",
                        parts[latIndex],
                        parts[lonIndex],
                        parts[Arrays.asList(headers).indexOf("acq_date")],
                        parts[Arrays.asList(headers).indexOf("satellite")],
                        parts[Arrays.asList(headers).indexOf("frp")],
                        parts[Arrays.asList(headers).indexOf("daynight")]))
                .collect(Collectors.joining("\n"));
    }

    public String getElevation(double latitude, double longitude) throws Exception {
        validateCoordinates(latitude, longitude);
        String url = String.format(Locale.US,"https://api.open-elevation.com/api/v1/lookup?locations=%.6f,%.6f", latitude, longitude);
        return sendGetRequest(url);
    }

    public String getAirQualityFromOpenAQ(double latitude, double longitude) throws Exception {
        validateCoordinates(latitude, longitude);
        String url = String.format(Locale.US,
                "https://api.openaq.org/v3/latest?coordinates=%.6f,%.6f&radius=25000&limit=1",
                latitude, longitude);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("x-api-key", "7d38db877e6a529a6f2e2ac383f62e702c5e0465add7d666a8047dae50f4fcba")
                .GET().build();
        return sendRequest(request);
    }

    public String getAirQualityFromIQAir(double latitude, double longitude) throws Exception {
        String url = String.format(Locale.US,
                "https://api.airvisual.com/v2/nearest_city?lat=%f&lon=%f&key=04d47a81-fbff-4f8d-a673-2c243f4f7ba9",
                latitude, longitude);
        return sendGetRequest(url);
    }



    public String getAllDataAsString(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();

        try {
            result.append("Réponse API Weatherstack:\n")
                    .append(getWeatherFromWeatherstack(latitude, longitude))
                    .append("\n\n");
        } catch (Exception e) {
            result.append("L'API Weatherstack a échoué à avoir les infos.\n\n");
        }

        try {
            result.append("Réponse API VisualCrossing:\n")
                    .append(getWeatherFromVisualCrossing(latitude, longitude))
                    .append("\n\n");
        } catch (Exception e) {
            result.append("L'API VisualCrossing a échoué à avoir les infos.\n\n");
        }

        try {
            result.append("Réponse API Open-Meteo:\n")
                    .append(getWeatherFromOpenMeteo(latitude, longitude))
                    .append("\n\n");
        } catch (Exception e) {
            result.append("L'API Open-Meteo a échoué à avoir les infos.\n\n");
        }

        try {
            result.append("Réponse API WeatherAPI:\n")
                    .append(getWeatherFromWeatherAPI(latitude, longitude))
                    .append("\n\n");
        } catch (Exception e) {
            result.append("L'API WeatherAPI a échoué à avoir les infos.\n\n");
        }

        try {
            result.append("Réponse API NASA FIRMS (3 feux les plus proches):\n")
                    .append(getNearestFiresFromNASA(latitude, longitude))
                    .append("\n\n");
        } catch (Exception e) {
            result.append("L'API NASA FIRMS a échoué à avoir les infos.\n\n");
        }

        try {
            result.append("Réponse API Open-Elevation:\n")
                    .append(getElevation(latitude, longitude))
                    .append("\n\n");
        } catch (Exception e) {
            result.append("L'API Open-Elevation a échoué à avoir les infos.\n\n");
        }

        try {
            result.append("Réponse API OpenAQ:\n")
                    .append(getAirQualityFromOpenAQ(latitude, longitude))
                    .append("\n\n");
        } catch (Exception e) {
            result.append("L'API OpenAQ a échoué à avoir les infos.\n\n");
        }

        try {
            result.append("Réponse API IQAir:\n")
                    .append(getAirQualityFromIQAir(latitude, longitude))
                    .append("\n\n");
        } catch (Exception e) {
            result.append("L'API IQAir a échoué à avoir les infos.\n\n");
        }


        return result.toString();
    }

    public String nettoyerMessage(String texte) {
        if (texte == null) return "";

        return texte
                .replaceAll("[^a-zA-Z0-9 ]", " ")  // remplace tout sauf lettres, chiffres, espaces
                .replaceAll("\\s{2,}", " ")        // réduit les espaces multiples
                .trim();                           // enlève les espaces au début/fin
    }




    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////



    @Autowired
    private AgentService agentService;

    public String getPrediction(double lat, double lon) {
        String prompt = getAllDataAsString(lat, lon);
        String clear = nettoyerMessage(prompt);
        String res = agentService.sendPromptToAgent(clear);
        return res;
    }

    public Prediction extractPredictionFromResponse(String agentResponse, ZoneSurveille zone, Utilisateur user) {
        Prediction prediction = new Prediction();

        try {
            String jsonString;

            // Vérifier si la réponse contient les balises ```json
            if (agentResponse.contains("```json")) {
                // Extraire le JSON avec les balises
                jsonString = agentResponse.substring(agentResponse.indexOf("```json") + 7);
                jsonString = jsonString.substring(0, jsonString.indexOf("```"));
            } else {
                // Si pas de balises, prendre toute la réponse comme JSON
                jsonString = agentResponse;
            }

            jsonString = jsonString.trim();

            // Parser le JSON
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            // Remplir l'entité Prediction
            prediction.setDatePrediction(LocalDateTime.now());
            prediction.setScoreRisque(jsonNode.get("scoreRisque").asInt());
            prediction.setNiveauRisque(NiveauRisque.valueOf(jsonNode.get("niveauRisque").asText()));
            prediction.setExplication(jsonNode.get("explication").asText());
            prediction.setCausesPrincipales(jsonNode.get("causesPrincipales").asText());

            // Données météo
            if (jsonNode.has("temperature")) prediction.setTemperature(jsonNode.get("temperature").asDouble());
            if (jsonNode.has("humidite")) prediction.setHumidite(jsonNode.get("humidite").asDouble());
            if (jsonNode.has("vitesseVent")) prediction.setVitesseVent(jsonNode.get("vitesseVent").asDouble());
            if (jsonNode.has("pressionAtmospherique")) prediction.setPressionAtmospherique(jsonNode.get("pressionAtmospherique").asDouble());

            // Données qualité air
            if (jsonNode.has("indiceQualiteAir")) prediction.setIndiceQualiteAir(jsonNode.get("indiceQualiteAir").asInt());
            if (jsonNode.has("concentrationPM25")) prediction.setConcentrationPM25(jsonNode.get("concentrationPM25").asDouble());

            // Données feux proches
            if (jsonNode.has("nombreFeuxProches")) prediction.setNombreFeuxProches(jsonNode.get("nombreFeuxProches").asInt());
            if (jsonNode.has("distanceFeuxPlusProche")) prediction.setDistanceFeuxPlusProche(jsonNode.get("distanceFeuxPlusProche").asDouble());
            if (jsonNode.has("intensiteFeuxMax")) prediction.setIntensiteFeuxMax(jsonNode.get("intensiteFeuxMax").asDouble());

            // Données topographiques
            if (jsonNode.has("altitude")) prediction.setAltitude(jsonNode.get("altitude").asDouble());
            if (jsonNode.has("sourcesDonnees")) prediction.setSourcesDonnees(jsonNode.get("sourcesDonnees").asText());

            // Relations
            prediction.setZoneSurveille(zone);
            prediction.setUtilisateur(user);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'extraction: " + e.getMessage(), e);
        }

        return prediction;
    }


}
