package sn.sonatel.dsi.ins.imoc.service;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.domain.ValidationStatusUser;
import sn.sonatel.dsi.ins.imoc.repository.ValidationStatusUserRepository;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
public class ValidationUserService {

    @Autowired
    private ValidationStatusUserRepository validationStatusUserRepository;

    @Autowired
    private NotificationService notificationService;

    public void enregistrer(AppUser user) {
        try {
            // Création d'une nouvelle entité de validation
            ValidationStatusUser validation = new ValidationStatusUser();
            validation.setAppUser(user);

            Instant created = Instant.now();
            Instant expiration = created.plus(10, MINUTES);
            validation.setExpire(expiration);

            // Génération du code aléatoire à 6 chiffres
            Random random = new Random();
            int randomInt = random.nextInt(999999);
            String code = String.format("%06d", randomInt);  // Code à 6 chiffres

            validation.setCode(code);

            // Sauvegarde de l'entité dans la base de données
            this.validationStatusUserRepository.save(validation);

            // Envoi du mail
            this.notificationService.envoyer(validation);

        } catch (MessagingException | UnsupportedEncodingException e) {
            // Gestion des erreurs d'envoi d'email
            e.printStackTrace();  // Remplacez par un logger si nécessaire
            throw new RuntimeException("Erreur lors de l'envoi de l'email. Veuillez réessayer.");
        }
    }

    public ValidationStatusUser getUserByCode(String code) {
        return this.validationStatusUserRepository
            .findByCode(code)
            .orElseThrow(() -> new RuntimeException("Votre code est invalide"));
    }
}
