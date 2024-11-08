package sn.sonatel.dsi.ins.imoc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.ValidationStatusUser;
import sn.sonatel.dsi.ins.imoc.domain.ValidationStatuscandidat;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;

    @Autowired
    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void envoyer(ValidationStatusUser validation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("akly@ept.sn");
        message.setTo(validation.getAppUser().getEmail());
        message.setSubject("Votre code d'activation");

        // Texte sans HTML, pour SimpleMailMessage
        String texte = String.format("Bonjour %s,\n\nVotre code d'activation est : %s.\nÀ bientôt.",
            validation.getAppUser().getName(),
            validation.getCode()
        );
        message.setText(texte);

        mailSender.send(message);
    }

    public void envoyerCandidat(ValidationStatuscandidat validation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("akly@ept.sn");
        message.setTo(validation.getCandidat().getEmail());
        message.setSubject("Votre code d'activation");

        String texte = String.format("Bonjour %s,\n\nBienvenue chez sonatel ! Votre code d'activation est : %s.",
            validation.getCandidat().getLastName(),
            validation.getCode()
        );
        message.setText(texte);

        mailSender.send(message);
    }
}
