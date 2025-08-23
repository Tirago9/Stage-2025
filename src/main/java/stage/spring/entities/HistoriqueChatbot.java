package stage.spring.entities;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class HistoriqueChatbot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String messageUser;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String reponseIA;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Utilisateur utilisateur;

    public HistoriqueChatbot() {
    }

    public HistoriqueChatbot(Long id, Utilisateur utilisateur, LocalDateTime date, String reponseIA, String messageUser) {
        this.id = id;
        this.utilisateur = utilisateur;
        this.date = date;
        this.reponseIA = reponseIA;
        this.messageUser = messageUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getReponseIA() {
        return reponseIA;
    }

    public void setReponseIA(String reponseIA) {
        this.reponseIA = reponseIA;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }
// Getters et Setters
}

