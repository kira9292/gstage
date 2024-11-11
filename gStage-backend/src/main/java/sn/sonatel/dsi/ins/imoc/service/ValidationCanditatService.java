package sn.sonatel.dsi.ins.imoc.service;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.Candidat;
import sn.sonatel.dsi.ins.imoc.domain.ValidationStatuscandidat;
import sn.sonatel.dsi.ins.imoc.repository.ValidationStatuscandidatRepository;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.Random;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
public class ValidationCanditatService {

    @Autowired
    private ValidationStatuscandidatRepository validationStatuscandidatRepository;

    @Autowired
    private NotificationService notificationService;

    public void enregistrer(Candidat user) {
        try {
            // Création d'une nouvelle entité de validation
            ValidationStatuscandidat validation = new ValidationStatuscandidat();
            validation.setCandidat(user);
            Instant created = Instant.now();
            Instant expiration = created.plus(10, MINUTES);
            validation.setExpire(expiration);
            validation.setCreation(created);

            // Génération du code aléatoire à 6 chiffres
            Random random = new Random();
            int randomInt = random.nextInt(999999);
            String code = String.format("%06d", randomInt);  // Code à 6 chiffres

            validation.setCode(code);

            // Sauvegarde de l'entité dans la base de données
            this.validationStatuscandidatRepository.save(validation);

            // Envoi du mail
            this.notificationService.envoyercandidat(validation);

        } catch (MessagingException | UnsupportedEncodingException e) {
            // Gestion des erreurs d'envoi d'email
            e.printStackTrace();  // Remplacez par un logger si nécessaire
            throw new RuntimeException("Erreur lors de l'envoi de l'email. Veuillez réessayer.");
        } catch (Exception e) {
            // Gestion des autres erreurs
            e.printStackTrace();  // Remplacez par un logger si nécessaire
            throw new RuntimeException("Une erreur est survenue lors de l'enregistrement de l'utilisateur.");
        }
    }

    public ValidationStatuscandidat getUserByCode(String code) {
        return this.validationStatuscandidatRepository
            .findByCode(code)
            .orElseThrow(() -> new RuntimeException("Votre code est invalide"));
    }
}
