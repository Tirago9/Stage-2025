package stage.spring.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;
    private String image;
    private String image3D;

    @OneToMany(mappedBy = "produit")
    private List<LigneCommande> lignesCommande;


    public Produit() {
    }

    public Produit(Long id, String image, String description, String nom, String image3D) {
        this.id = id;
        this.image = image;
        this.description = description;
        this.nom = nom;
        this.image3D = image3D;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImage3D() {
        return image3D;
    }

    public void setImage3D(String image3D) {
        this.image3D = image3D;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }


// Getters et Setters
}
