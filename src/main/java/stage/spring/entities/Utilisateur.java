package stage.spring.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String email;
    private String motdepasse;
    @Enumerated(EnumType.STRING) // Stocke le nom de l'enum en base
    private RoleUtilisateur role;
    private String adresse;
    private String numtel;


    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<ZoneSurveille> zonesSurveillees;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<HistoriqueChatbot> histoiresChatbot;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<Commande> commandes;

    public Utilisateur() {
    }

    public Utilisateur(String numtel, RoleUtilisateur role, String adresse, String motdepasse, String email, String prenom, String nom) {
        this.numtel = numtel;
        this.role = role;
        this.adresse = adresse;
        this.motdepasse = motdepasse;
        this.email = email;
        this.prenom = prenom;
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getNumtel() {
        return numtel;
    }

    public void setNumtel(String numtel) {
        this.numtel = numtel;
    }

    public Utilisateur(RoleUtilisateur role, String motdepasse, String email, String nom, Long id) {
        this.role = role;
        this.motdepasse = motdepasse;
        this.email = email;
        this.nom = nom;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotdepasse() {
        return motdepasse;
    }

    public void setMotdepasse(String motdepasse) {
        this.motdepasse = motdepasse;
    }

    public RoleUtilisateur getRole() {
        return role;
    }

    public void setRole(RoleUtilisateur role) {
        this.role = role;
    }

    public List<Commande> getCommandes() {
        return commandes;
    }

    public void setCommandes(List<Commande> commandes) {
        this.commandes = commandes;
    }

    public List<HistoriqueChatbot> getHistoiresChatbot() {
        return histoiresChatbot;
    }

    public void setHistoiresChatbot(List<HistoriqueChatbot> histoiresChatbot) {
        this.histoiresChatbot = histoiresChatbot;
    }

    public List<ZoneSurveille> getZonesSurveillees() {
        return zonesSurveillees;
    }

    public void setZonesSurveillees(List<ZoneSurveille> zonesSurveillees) {
        this.zonesSurveillees = zonesSurveillees;
    }

    // Getters et Setters
}
