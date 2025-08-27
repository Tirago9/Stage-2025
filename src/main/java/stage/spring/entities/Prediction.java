package stage.spring.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime datePrediction;

    @Column(columnDefinition = "TEXT")
    private String explication;

    private Integer scoreRisque;

    @Enumerated(EnumType.STRING)
    private NiveauRisque niveauRisque;

    private String causesPrincipales;

    // Données météo au moment de la prédiction
    private Double temperature;
    private Double humidite;
    private Double vitesseVent;
    private Double pressionAtmospherique;

    // Données de qualité d'air
    private Integer indiceQualiteAir;
    private Double concentrationPM25;

    // Données des feux proches
    private Integer nombreFeuxProches;
    private Double distanceFeuxPlusProche;
    private Double intensiteFeuxMax;

    // Données topographiques
    private Double altitude;


    private String sourcesDonnees;


    @ManyToOne
    @JoinColumn(name = "zone_id")
    @JsonIgnore
    private ZoneSurveille zoneSurveille;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private Utilisateur utilisateur;



    public Prediction() {
    }

    public Prediction(ZoneSurveille zoneSurveille, Long id, LocalDateTime datePrediction, String explication, Integer scoreRisque,
                      NiveauRisque niveauRisque, String causesPrincipales, Double humidite, Double vitesseVent, Integer indiceQualiteAir,
                      Double concentrationPM25, Double distanceFeuxPlusProche, Double intensiteFeuxMax, Integer nombreFeuxProches,
                      Double pressionAtmospherique, Double temperature, Utilisateur utilisateur ,Double altitude,String sourcesDonnees) {
        this.zoneSurveille = zoneSurveille;
        this.id = id;
        this.datePrediction = datePrediction;
        this.explication = explication;
        this.scoreRisque = scoreRisque;
        this.niveauRisque = niveauRisque;
        this.causesPrincipales = causesPrincipales;
        this.humidite = humidite;
        this.vitesseVent = vitesseVent;
        this.indiceQualiteAir = indiceQualiteAir;
        this.concentrationPM25 = concentrationPM25;
        this.distanceFeuxPlusProche = distanceFeuxPlusProche;
        this.intensiteFeuxMax = intensiteFeuxMax;
        this.nombreFeuxProches = nombreFeuxProches;
        this.pressionAtmospherique = pressionAtmospherique;
        this.temperature = temperature;
        this.utilisateur = utilisateur;
        this.altitude = altitude;
        this.sourcesDonnees = sourcesDonnees;
    }

    public String getSourcesDonnees() {
        return sourcesDonnees;
    }

    public void setSourcesDonnees(String sourcesDonnees) {
        this.sourcesDonnees = sourcesDonnees;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExplication() {
        return explication;
    }

    public void setExplication(String explication) {
        this.explication = explication;
    }

    public LocalDateTime getDatePrediction() {
        return datePrediction;
    }

    public void setDatePrediction(LocalDateTime datePrediction) {
        this.datePrediction = datePrediction;
    }

    public Integer getScoreRisque() {
        return scoreRisque;
    }

    public void setScoreRisque(Integer scoreRisque) {
        this.scoreRisque = scoreRisque;
    }

    public NiveauRisque getNiveauRisque() {
        return niveauRisque;
    }

    public void setNiveauRisque(NiveauRisque niveauRisque) {
        this.niveauRisque = niveauRisque;
    }

    public String getCausesPrincipales() {
        return causesPrincipales;
    }

    public void setCausesPrincipales(String causesPrincipales) {
        this.causesPrincipales = causesPrincipales;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getHumidite() {
        return humidite;
    }

    public void setHumidite(Double humidite) {
        this.humidite = humidite;
    }

    public Double getVitesseVent() {
        return vitesseVent;
    }

    public void setVitesseVent(Double vitesseVent) {
        this.vitesseVent = vitesseVent;
    }

    public Double getPressionAtmospherique() {
        return pressionAtmospherique;
    }

    public void setPressionAtmospherique(Double pressionAtmospherique) {
        this.pressionAtmospherique = pressionAtmospherique;
    }

    public Integer getIndiceQualiteAir() {
        return indiceQualiteAir;
    }

    public void setIndiceQualiteAir(Integer indiceQualiteAir) {
        this.indiceQualiteAir = indiceQualiteAir;
    }

    public Double getConcentrationPM25() {
        return concentrationPM25;
    }

    public void setConcentrationPM25(Double concentrationPM25) {
        this.concentrationPM25 = concentrationPM25;
    }

    public Integer getNombreFeuxProches() {
        return nombreFeuxProches;
    }

    public void setNombreFeuxProches(Integer nombreFeuxProches) {
        this.nombreFeuxProches = nombreFeuxProches;
    }

    public Double getDistanceFeuxPlusProche() {
        return distanceFeuxPlusProche;
    }

    public void setDistanceFeuxPlusProche(Double distanceFeuxPlusProche) {
        this.distanceFeuxPlusProche = distanceFeuxPlusProche;
    }

    public Double getIntensiteFeuxMax() {
        return intensiteFeuxMax;
    }

    public void setIntensiteFeuxMax(Double intensiteFeuxMax) {
        this.intensiteFeuxMax = intensiteFeuxMax;
    }

    public ZoneSurveille getZoneSurveille() {
        return zoneSurveille;
    }

    public void setZoneSurveille(ZoneSurveille zoneSurveille) {
        this.zoneSurveille = zoneSurveille;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }
}