package sn.sonatel.dsi.ins.imoc.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.sonatel.dsi.ins.imoc.domain.*;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.ERole;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.InternshipStatus;
import sn.sonatel.dsi.ins.imoc.dto.AttestationPDTO;
import sn.sonatel.dsi.ins.imoc.repository.AppUserRepository;
import sn.sonatel.dsi.ins.imoc.repository.NotificationRepository;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;
    private final ContratService contratService;
    private final AttestationService attestationService;
    private final NotificationRepository notificationRepository;
    private final AppUserRepository appUserRepository;

    @Autowired
    public NotificationService(JavaMailSender mailSender, ContratService contratService, AttestationService attestationService, NotificationRepository notificationRepository, AppUserRepository appUserRepository) {
        this.mailSender = mailSender;
        this.contratService = contratService;
        this.attestationService = attestationService;
        this.notificationRepository = notificationRepository;
        this.appUserRepository = appUserRepository;
    }

    // Envoi de mail avec HTML pour l'utilisateur
    public void envoyer(ValidationStatusUser validation) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("abdoulkarimly008@gmail.com", "Sonatel Stage");  // Email de l'expéditeur
        helper.setTo(validation.getAppUser().getEmail());
        helper.setSubject("Votre code d'activation");

        // Construction du contenu HTML
        String htmlContent = "<html><body style='font-family: Arial, sans-serif; color: #333;'>"
            + "<table width='100%' cellspacing='0' cellpadding='10' style='background-color: #f4f4f4;'>"
            + "<tr><td style='text-align: center;'>"
            + "<h2 style='color: #4CAF50;'>Bonjour " + validation.getAppUser().getName() + ",</h2>"
            + "<p>Votre code d'activation est : <strong style='font-size: 24px; color: #4CAF50;'>" + validation.getCode() + "</strong></p>"
            + "<p>Merci de vous inscrire chez Sonatel !</p>"
            + "<p style='font-size: 14px;'>À bientôt,</p>"
            + "<p style='font-size: 14px;'>L'équipe Sonatel</p>"
            + "<hr style='border: 0; border-top: 1px solid #ddd;'>"
            + "<footer style='font-size: 12px; color: #aaa; text-align: center;'>"
            + "<p>Ce message est généré automatiquement, merci de ne pas y répondre.</p>"
            + "</footer>"
            + "</td></tr></table></body></html>";

        helper.setText(htmlContent, true);  // true indique que le contenu est en HTML

        mailSender.send(message);
    }

    // Envoi de mail avec HTML pour le candidat
    public void envoyercandidat(ValidationStatuscandidat validation) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("abdoulkarimly008@gmail.com", "Sonatel Stage");  // Email de l'expéditeur
        helper.setTo(validation.getCandidat().getEmail());
        helper.setSubject("Votre code d'activation");

        // Construction du contenu HTML pour le candidat
        String htmlContent = "<html><body style='font-family: Arial, sans-serif; color: #333;'>"
            + "<table width='100%' cellspacing='0' cellpadding='20' style='background-color: #f4f4f4;'>"
            + "<tr><td style='text-align: center;'>"
            + "<img src='https://media.licdn.com/dms/image/v2/D4E0BAQFkSnqxS1MfTw/company-logo_200_200/company-logo_200_200/0/1730735216594/groupesonatel_logo?e=2147483647&v=beta&t=fP94m6ULPSu4X4kyuOSv6C8oiUv464rGn8DwgsB7ods' alt='Sonatel Logo' style='width: 150px; margin-bottom: 20px;'>"
            + "<h2 style='color: #4CAF50;'>Bonjour " + validation.getCandidat().getFirstName() + " " + validation.getCandidat().getLastName() + ",</h2>"
            + "<p>Nous sommes ravis que vous souhaitiez rejoindre Sonatel pour un stage !</p>"
            + "<p style='font-size: 16px;'>Pour confirmer votre adresse email et continuer le processus de candidature, veuillez utiliser le code de validation suivant :</p>"
            + "<p style='font-size: 24px; font-weight: bold; color: #4CAF50;'>" + validation.getCode() + "</p>"
            + "<p style='font-size: 16px;'>Ce code est valable pendant les prochaines 10 minutes. Veuillez ne pas le partager avec quiconque.</p>"
            + "<hr style='border: 0; border-top: 1px solid #ddd; margin: 20px 0;'>"
            + "<p style='font-size: 14px;'>Si vous n'êtes pas à l'origine de cette demande, veuillez ignorer ce message.</p>"
            + "<p style='font-size: 14px;'>Cordialement,<br>L'équipe Sonatel</p>"
            + "<footer style='font-size: 12px; color: #aaa; text-align: center;'>"
            + "<p>Ce message est généré automatiquement. Merci de ne pas y répondre.</p>"
            + "</footer>"
            + "</td></tr></table></body></html>";

        helper.setText(htmlContent, true);  // true indique que le contenu est en HTML
        mailSender.send(message);
    }

    public void envoyerDocument(ValidationStatuscandidat validation) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("abdoulkarimly008@gmail.com", "Sonatel Stage");
        helper.setTo(validation.getCandidat().getEmail());
        helper.setSubject("Félicitations : Votre candidature a été retenue chez Sonatel !");
        String htmlContent = "<html><body style='font-family: Arial, sans-serif; color: #333;'>"
            + "<table width='100%' cellspacing='0' cellpadding='20' style='background-color: #f4f4f4;'>"
            + "<tr><td style='text-align: center;'>"
            + "<img src='https://media.licdn.com/dms/image/v2/D4E0BAQFkSnqxS1MfTw/company-logo_200_200/company-logo_200_200/0/1730735216594/groupesonatel_logo?e=2147483647&v=beta&t=fP94m6ULPSu4X4kyuOSv6C8oiUv464rGn8DwgsB7ods' alt='Sonatel Logo' style='width: 150px; margin-bottom: 20px;'>"
            + "<h2 style='color: #4CAF50;'>Bonjour " + validation.getCandidat().getFirstName() + " " + validation.getCandidat().getLastName() + ",</h2>"
            + "<p>Félicitations ! Votre candidature a été retenue pour un stage chez Sonatel.</p>"
            + "<p style='font-size: 16px;'>Voici les détails de votre stage :</p>"
            + "<ul style='font-size: 16px; text-align: left; display: inline-block; margin: auto;'>"
            + "<li><b>Date de démarrage :</b> " + validation.getCandidat().getDemandeStage().getStartDate() + "</li>"
            + "<li><b>Date de fin :</b> " + validation.getCandidat().getDemandeStage().getEndDate() + "</li>"
            + "<li><b>Service :</b> " + validation.getCandidat().getDemandeStage().getAppUser().getService().getName() + "</li>"
            + "</ul>"
            + "<p style='font-size: 16px; color: #D35400;'><b>Documents à fournir :</b></p>"
            + "<p style='font-size: 16px;'>Vous trouverez ci-joint votre contrat de stage. Veuillez :</p>"
            + "<ol style='font-size: 16px; text-align: left; display: inline-block; margin: auto;'>"
            + "<li>Imprimer le contrat en 3 exemplaires</li>"
            + "<li>Signer chaque exemplaire</li>"
            + "<li>Les apporter avec vous le jour de votre arrivée</li>"
            + "</ol>"
            + "<p style='background-color: #FEF9E7; padding: 10px; border-radius: 5px; margin: 20px 0;'>"
            + "<i class='fas fa-info-circle'></i> <b>Important :</b> Sans ces documents signés, votre stage ne pourra pas commencer."
            + "</p>"
            + "<p>Si vous avez des questions, n'hésitez pas à nous contacter.</p>"
            + "<hr style='border: 0; border-top: 1px solid #ddd; margin: 20px 0;'>"
            + "<p style='font-size: 14px;'>Cordialement,<br>L'équipe Sonatel</p>"
            + "<footer style='font-size: 12px; color: #aaa; text-align: center;'>"
            + "<p>Ce message est généré automatiquement. Merci de ne pas y répondre.</p>"
            + "</footer>"
            + "</td></tr></table></body></html>";
        helper.setText(htmlContent, true);
        // Générer et joindre le contrat
        ByteArrayResource contrat = contratService.genererContrat(validation);
        helper.addAttachment(
            String.format("Contrat_Stage_Sonatel_%s_%s.docx",
                validation.getCandidat().getFirstName(),
                validation.getCandidat().getLastName()),
            new ByteArrayResource(contrat.getByteArray())
        );

        mailSender.send(message);
    }


    public void notifyAssistants(String message, InternshipStatus type) {
        List<AppUser> assistants = appUserRepository.findByRoleName(ERole.ASSISTANT_GWTE);
        for (AppUser assistant : assistants) {
            Notification notification = new Notification();
            notification.setMessage(message);
            notification.setTypeNotification(type);
            notification.setSendingDate(LocalDate.now());
            notification.setRead(false);
            notification.setAppUser(assistant);
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findByIdAndAppUserId(notificationId, userId)
            .orElseThrow(() -> new RuntimeException("Notification not found or not authorized"));

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByAppUserIdAndReadFalse(userId);
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findByIdAndAppUserId(notificationId, userId)
            .orElseThrow(() -> new RuntimeException("Notification not found or not authorized"));
        notificationRepository.delete(notification);
    }

    @Transactional
    public void deleteAllNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByAppUserId(userId);
        notificationRepository.deleteAll(notifications);
    }

    public void envoyerAttestationPresence(ValidationStatuscandidat validation, AttestationPDTO request) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("amethndiaye840@gmail.com", "Sonatel Stage");
        helper.setTo(validation.getCandidat().getEmail());
        helper.setSubject("Votre attestation de presence chez Sonatel");

        String htmlContent = "<html><body style='font-family: Arial, sans-serif; color: #333;'>" +
            "<div style='max-width: 600px; margin: auto; padding: 20px; text-align: center;'>" +
            "<img src='https://media.licdn.com/dms/image/v2/D4E0BAQFkSnqxS1MfTw/company-logo_200_200/company-logo_200_200/0/1730735216594/groupesonatel_logo?e=2147483647&v=beta&t=fP94m6ULPSu4X4kyuOSv6C8oiUv464rGn8DwgsB7ods' alt='Sonatel Logo' style='width: 150px; margin-bottom: 20px;'>" +
            "<h2>Bonjour " + validation.getCandidat().getFirstName() + " " + validation.getCandidat().getLastName() + ",</h2>" +
            "<p>Vous trouverez ci-joint votre attestation de présence du "+ request.startDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " au " + request.endDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +  " de stage à Sonatel.</p>" +
            "<p>Cordialement,<br>L'équipe Sonatel</p>" +
            "<footer style='font-size: 12px; color: #aaa;'>Message généré automatiquement</footer>" +
            "</div></body></html>";

        helper.setText(htmlContent, true);

        // Générer et joindre l'attestation
        ByteArrayResource attestation = attestationService.genererAttestationPresence(validation, request);
        helper.addAttachment(
            String.format("Attestation_%s_%s.docx",
                validation.getCandidat().getFirstName(),
                validation.getCandidat().getLastName()),
            new ByteArrayResource(attestation.getByteArray())
        );
        mailSender.send(message);
    }

    public void envoyerAttestation(ValidationStatuscandidat validation ) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("amethndiaye840@gmail.com", "Sonatel Stage");
        helper.setTo(validation.getCandidat().getEmail());
        helper.setSubject("Votre attestation de stage chez Sonatel ");
        String htmlContent = "<html><body style='font-family: Arial, sans-serif; color: #333;'>"
            + "<table width='100%' cellspacing='0' cellpadding='20' style='background-color: #f4f4f4;'>"
            + "<tr><td style='text-align: center;'>"
            + "<img src='https://media.licdn.com/dms/image/v2/D4E0BAQFkSnqxS1MfTw/company-logo_200_200/company-logo_200_200/0/1730735216594/groupesonatel_logo?e=2147483647&v=beta&t=fP94m6ULPSu4X4kyuOSv6C8oiUv464rGn8DwgsB7ods' alt='Sonatel Logo' style='width: 150px; margin-bottom: 20px;'>"
            + "<h2 style='color: #4CAF50;'>Bonjour " + validation.getCandidat().getFirstName() + " " + validation.getCandidat().getLastName() + ",</h2>"
            + "<p>Voici votre attestation de stage à Sonatel.</p>"
            + "<p style='font-size: 16px;'>Nous espérons que votre expérience de stage a été enrichissante.</p>"
            + "<p>Vous trouverez ci-joint votre attestation de stage.</p>"
            + "<hr style='border: 0; border-top: 1px solid #ddd; margin: 20px 0;'>"
            + "<p style='font-size: 14px;'>Cordialement,<br>L'équipe Sonatel</p>"
            + "<footer style='font-size: 12px; color: #aaa; text-align: center;'>"
            + "<p>Ce message est généré automatiquement. Merci de ne pas y répondre.</p>"
            + "</footer>"
            + "</td></tr></table></body></html>";
        helper.setText(htmlContent, true);

        // Générer et joindre l'attestation
        ByteArrayResource attestation = attestationService.genererAttestation(validation);
        helper.addAttachment(
            String.format("Attestation_%s_%s.docx",
                validation.getCandidat().getFirstName(),
                validation.getCandidat().getLastName()),
            new ByteArrayResource(attestation.getByteArray())
        );
        mailSender.send(message);
    }
}
