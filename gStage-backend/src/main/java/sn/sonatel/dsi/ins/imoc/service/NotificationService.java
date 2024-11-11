package sn.sonatel.dsi.ins.imoc.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.ValidationStatusUser;
import sn.sonatel.dsi.ins.imoc.domain.ValidationStatuscandidat;

import java.io.UnsupportedEncodingException;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;

    @Autowired
    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
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
            + "<img src='https://www.sonatel.sn/wp-content/uploads/2020/06/logo-sonatel.png' alt='Sonatel Logo' style='width: 150px; margin-bottom: 20px;'>"
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
}
