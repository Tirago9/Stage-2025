package stage.spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stage.spring.entities.ZoneSurveille;
import stage.spring.entities.Utilisateur;
import stage.spring.repositories.UtilisateurRepository;
import stage.spring.repositories.ZoneSurveilleRepository;
import stage.spring.services.ZoneSurveilleService;
import stage.spring.session.SessionUtilisateur;

import stage.spring.session.SessionUtilisateur;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/zones")
public class ZoneSurveilleController {

    @Autowired
    private ZoneSurveilleService zoneSurveilleService;


    @Autowired
    private ZoneSurveilleRepository zoneSurveilleRepository;


    @Autowired
    private UtilisateurRepository utilisateurRepository;




    @Autowired
    private SessionUtilisateur sessionUtilisateur;


    @PostMapping("/ajouter")
    public ZoneSurveille ajouterZone(@RequestBody ZoneSurveille zone) {
        return zoneSurveilleService.ajouterZoneSurveille(zone);
    }

    @GetMapping("/zones/utilisateur/{userId}")
    public ResponseEntity<List<ZoneSurveille>> getZonesByUtilisateur(@PathVariable Long userId) {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findById(userId);
        return utilisateur.map(u -> ResponseEntity.ok(u.getZonesSurveillees()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/zones/{id}")
    public ResponseEntity<String> supprimerZone(@PathVariable Long id) {
        if (!zoneSurveilleRepository.existsById(id)) return ResponseEntity.notFound().build();
        zoneSurveilleRepository.deleteById(id);
        return ResponseEntity.ok("Zone supprimée");
    }

    @PutMapping("/zones/{id}")
    public ResponseEntity<ZoneSurveille> modifierZone(@PathVariable Long id, @RequestBody ZoneSurveille updatedZone) {
        return zoneSurveilleRepository.findById(id).map(zone -> {
            zone.setNom(updatedZone.getNom());
            zone.setDescription(updatedZone.getDescription());
            zone.setRayon(updatedZone.getRayon());
            zone.setLaltitude(updatedZone.getLaltitude());
            zone.setLongitude(updatedZone.getLongitude());
            return ResponseEntity.ok(zoneSurveilleRepository.save(zone));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/zones/{id}")
    public ResponseEntity<ZoneSurveille> getZoneById(@PathVariable Long id) {
        return zoneSurveilleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/zones")
    public ResponseEntity<List<ZoneSurveille>> getZonesUtilisateur(SessionUtilisateur principal) {
        Optional<Utilisateur> utilisateur = Optional.ofNullable(utilisateurRepository.findByEmail(principal.getUtilisateurConnecte().getNom()));
        return utilisateur.map(u -> ResponseEntity.ok(zoneSurveilleRepository.findByUtilisateur(u)))
                .orElse(ResponseEntity.notFound().build());
    }



    @PutMapping("/zone")
    public ResponseEntity<ZoneSurveille> modifierZone(@RequestBody ZoneSurveille updatedZone) {
        Optional<ZoneSurveille> zone = zoneSurveilleRepository.findById(updatedZone.getId());
        if (zone.isPresent()) {
            ZoneSurveille z = zone.get();
            z.setNom(updatedZone.getNom());
            z.setDescription(updatedZone.getDescription());
            z.setRayon(updatedZone.getRayon());
            z.setLongitude(updatedZone.getLongitude());
            z.setLaltitude(updatedZone.getLaltitude());
            zoneSurveilleRepository.save(z);
            return ResponseEntity.ok(z);
        }
        return ResponseEntity.notFound().build();
    }


    // ZoneSurveilleController.java
    @PostMapping("/zones/check")
    public ResponseEntity<Boolean> checkZoneOverlap(@RequestBody ZoneSurveille newZone) {
        Utilisateur user = sessionUtilisateur.getUtilisateurConnecte();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<ZoneSurveille> existingZones = zoneSurveilleRepository.findByUtilisateur(user);

        boolean overlaps = existingZones.stream().anyMatch(existingZone ->
                calculateDistance(
                        existingZone.getLaltitude(),
                        existingZone.getLongitude(),
                        newZone.getLaltitude(),
                        newZone.getLongitude()
                ) < (existingZone.getRayon() + newZone.getRayon())
        );

        return ResponseEntity.ok(overlaps);
    }

    // Méthode utilitaire pour calculer la distance entre 2 points (en km)
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Rayon de la Terre en km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }





}

