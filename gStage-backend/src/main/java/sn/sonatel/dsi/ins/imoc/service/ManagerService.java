package sn.sonatel.dsi.ins.imoc.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import sn.sonatel.dsi.ins.imoc.domain.*;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.ERole;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.InternshipStatus;
import sn.sonatel.dsi.ins.imoc.dto.AttestationPDTO;
import sn.sonatel.dsi.ins.imoc.exceptions.InternshipRequestValidationException;
import sn.sonatel.dsi.ins.imoc.repository.AppUserRepository;
import sn.sonatel.dsi.ins.imoc.repository.DemandeStageRepository;
import sn.sonatel.dsi.ins.imoc.repository.RoleRepository;
import sn.sonatel.dsi.ins.imoc.repository.ValidationStatuscandidatRepository;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManagerService {

    @Autowired
    private DemandeStageRepository demandeStageRepository;

    private final NotificationService notificationService;
    private final ValidationStatuscandidatRepository validationStatuscandidatRepository;
    private final AppUserRepository appUserRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    private final JavaMailSender mailSender;


    public ManagerService(NotificationService notificationService, ValidationStatuscandidatRepository validationStatuscandidatRepository, AppUserRepository appUserRepository, JavaMailSender mailSender) {
        this.notificationService = notificationService;
        this.validationStatuscandidatRepository = validationStatuscandidatRepository;
        this.appUserRepository = appUserRepository;
        this.mailSender = mailSender;
    }

    @Transactional
    public Optional<String> validateInternshipRequest(Long internshipId) {
        return demandeStageRepository
            .findById(internshipId)
            .map(demandeStage -> {
                // Vérifier que la demande est dans un état valide pour validation
                if (demandeStage.getStatus() != InternshipStatus.PROPOSE && demandeStage.getStatus() != InternshipStatus.EN_COURS) {
                    throw new InternshipRequestValidationException(
                        "La demande ne peut être traitée que si elle est en statut PROPOSE ou EN_COURS"
                    );
                }                // Mettre à jour le statut
                demandeStage.setStatus(InternshipStatus.ACCEPTE);
                // Sauvegarder la demande
                demandeStageRepository.save(demandeStage);

                // Notification
                String message = "La demande de stage " + demandeStage.getReference() + " a été acceptée par le service " + demandeStage.getAppUser().getService().getName();
                notificationService.notifyAssistants(message, InternshipStatus.ACCEPTE);

                return "Demande Stage " + internshipId.toString() + " accepted";
            });
    }

    @Transactional
    public Optional<String> rejectInternshipRequest(Long internshipId) {
        return demandeStageRepository
            .findById(internshipId)
            .map(demandeStage -> {
                // Vérifier que la demande est dans un état valide pour validation/rejet
                validateRequestStatus(demandeStage, InternshipStatus.PROPOSE);
                // Mettre à jour le statut
                demandeStage.setStatus(InternshipStatus.REFUSE);
                // Sauvegarder la demande
                demandeStageRepository.save(demandeStage);

                // Notification
                String message = "La demande de stage " + demandeStage.getReference() + " a été rejetée par le service " + demandeStage.getAppUser().getService().getName();
                notificationService.notifyAssistants(message, InternshipStatus.REFUSE);
                return "Demande Stage " + internshipId.toString() + " refused";
            });
    }

    @Transactional
    public Optional<String> confirmInternshipStart(Long internshipId) {
        return demandeStageRepository
            .findById(internshipId)
            .map(demandeStage -> {
                // Vérifier que la demande est dans un état valide pour validation/rejet
                // Vérifier que la demande est dans un état valide pour validation/rejet
                if (demandeStage.getStatus() != InternshipStatus.ACCEPTE && demandeStage.getStatus() != InternshipStatus.TERMINE) {
                    throw new InternshipRequestValidationException(
                        "La demande ne peut être mise EN_COURS que si elle est en statut ACCEPTE ou TERMINE "
                    );
                }
                // Récupérer le candidat associé à la demande de stage
                Candidat candidat = demandeStage.getCandidat();

                // Générer un username unique
                String username = generateUniqueUsername(candidat);

                // Generer un email unique
                String email = generateUniqueEmail(candidat);

                // Générer un mot de passe temporaire
                String tempPassword = generateTemporaryPassword();

                // Créer un nouvel AppUser avec le rôle STAGIAIRE
                AppUser stagiaire = new AppUser();
                stagiaire.setUsername(username);
                stagiaire.setEmail(email);
                stagiaire.setPassword(bCryptPasswordEncoder.encode(tempPassword)); // Assurez-vous d'utiliser un passwordEncoder
                stagiaire.setName(candidat.getLastName());
                stagiaire.setFirstName(candidat.getFirstName());
                stagiaire.setPhone(candidat.getPhone());
                stagiaire.setFormation(candidat.getFormation().toString());
                stagiaire.setNiveau(candidat.getEducationLevel());
                stagiaire.setStatus(true);

                Optional<Role> stagiaireRole = roleRepository.findByName(ERole.STAGIAIRE);
                stagiaireRole.ifPresent(stagiaire::setRole);

                stagiaire.addCandidat(candidat);
                appUserRepository.save(stagiaire);
                demandeStage.setStatus(InternshipStatus.EN_COURS);
                demandeStageRepository.save(demandeStage);

                // Envoyer un email de bienvenue avec les identifiants temporaires
                sendWelcomeEmail(stagiaire, tempPassword);

                // Notification
                String message = "La demande de stage " + demandeStage.getReference() + " est en cours dans le service " + demandeStage.getAppUser().getService().getName();
                notificationService.notifyAssistants(message, InternshipStatus.EN_COURS);
                return "Demande Stage " + internshipId.toString() + " started";
            });
    }


    private String generateUniqueUsername(Candidat candidat) {
        String firstNameConcatenated = candidat.getFirstName().toLowerCase().replaceAll("\\s+", "");
        String lastNameConcatenated = candidat.getLastName().toLowerCase();

        String baseUsername = firstNameConcatenated + "." + lastNameConcatenated ;

        String username = baseUsername;
        int counter = 1;

        while (appUserRepository.findByUsername(username).isPresent()) {
            username = baseUsername + counter;
            counter++;
        }

        return username;
    }

    private String generateUniqueEmail(Candidat candidat) {
        String firstNameConcatenated = candidat.getFirstName().toLowerCase().replaceAll("\\s+", "");

        // Convert last name to lowercase
        String lastNameConcatenated = candidat.getLastName().toLowerCase();

        // Create email in the format firstName.lastName@orange-sonatel.com
        String baseEmail = firstNameConcatenated + "." + lastNameConcatenated + "@orange-sonatel.com";

        String email = baseEmail;
        int counter = 1;

        while (appUserRepository.findByEmail(email).isPresent()) {
            email = firstNameConcatenated + "." + lastNameConcatenated + counter + "@orange-sonatel.com";
            counter++;
        }

        return email;
    }

    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 12);
    }

    private void sendWelcomeEmail(AppUser stagiaire, String tempPassword) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("amethndiaye840@gmail.com", "Sonatel Stage");

            // Récupérer l'email du candidat associé au stagiaire
            String destinationEmail = stagiaire.getCandidats().stream()
                .findFirst()
                .map(Candidat::getEmail)
                .orElseThrow(() -> new RuntimeException("Aucun candidat trouvé pour ce stagiaire"));

            helper.setTo(destinationEmail);
            helper.setSubject("Bienvenue chez Sonatel - Vos identifiants de connexion");

            String htmlContent = "<html><body style='font-family: Arial, sans-serif; color: #333;'>" +
                "<div style='max-width: 600px; margin: auto; padding: 20px; text-align: center;'>" +
                "<img src='https://media.licdn.com/dms/image/v2/D4E0BAQFkSnqxS1MfTw/company-logo_200_200/company-logo_200_200/0/1730735216594/groupesonatel_logo?e=2147483647&v=beta&t=fP94m6ULPSu4X4kyuOSv6C8oiUv464rGn8DwgsB7ods' alt='Sonatel Logo' style='width: 150px; margin-bottom: 20px;'>" +
                "<h2>Bienvenue " + stagiaire.getFirstName() + " " + stagiaire.getName() + ",</h2>" +
                "<p>Voici vos identifiants temporaires pour l'application Sonatel Stage :</p>" +
                "<p><strong>Nom d'utilisateur :</strong> " + stagiaire.getEmail() + "</p>" +
                "<p><strong>Mot de passe temporaire :</strong> " + tempPassword + "</p>" +
                "<p>Veuillez changer votre mot de passe lors de votre première connexion.</p>" +
                "<p>Cordialement,<br>L'équipe Sonatel</p>" +
                "<footer style='font-size: 12px; color: #aaa;'>Message généré automatiquement</footer>" +
                "</div></body></html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            // Log the error or handle it appropriately
            throw new RuntimeException("Erreur lors de l'envoi de l'email de bienvenue", e);
        }
    }


    @Transactional
    public Optional<String> markInternshipAsEnded(Long internshipId) {
        return demandeStageRepository
            .findById(internshipId)
            .map(demandeStage -> {
                // Vérifier que la demande est dans un état valide pour validation/rejet
                validateRequestStatus(demandeStage, InternshipStatus.EN_COURS);
                // Mettre à jour le statut
                demandeStage.setStatus(InternshipStatus.TERMINE);
                // Sauvegarder la demande
                demandeStageRepository.save(demandeStage);

                // Notification
                String message = "Le stage " + demandeStage.getReference() + " est termine dans le service " + demandeStage.getAppUser().getService().getName();
                notificationService.notifyAssistants(message, InternshipStatus.TERMINE);
                return "Demande Stage " + internshipId.toString() + " ended";
            });
    }

    private void validateRequestStatus(DemandeStage demandeStage, InternshipStatus status) {
        // Vérifier que la demande est dans un état valide pour validation/rejet
        if (demandeStage.getStatus() != status) {
                throw new InternshipRequestValidationException(
                    "La demande ne peut être traitée que si elle est en statut PROPOSE"
                );
            }
    }

    @Transactional
    public void generateAttestation(Map<String, String> mail) throws MessagingException, UnsupportedEncodingException {
        ValidationStatuscandidat validationStatusOptional = validationStatuscandidatRepository.findTopByCandidatEmailOrderByCreationDesc(mail.get("mail"));
        if (validationStatusOptional!=null) {
            this.notificationService.envoyerAttestation(validationStatusOptional);
        } else {
            throw new RuntimeException("Aucun candidat trouvé avec cet email");
        }
    }
    @Transactional
    public void generateAttestationPresence(AttestationPDTO request) throws MessagingException, UnsupportedEncodingException {
        ValidationStatuscandidat validation = validationStatuscandidatRepository
            .findTopByCandidatEmailOrderByCreationDesc(request.email());

        if (validation == null) {
            throw new RuntimeException("Aucun candidat trouvé avec cet email");
        }

        this.notificationService.envoyerAttestationPresence(validation, request);
    }
}
