package sn.sonatel.dsi.ins.imoc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.ValidationStatusUser;

@Service
public class NotificationService {


    private JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void envoyer(ValidationStatusUser validation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("akly@ept.sn");
        message.setTo(validation.getAppUser().getEmail());
        message.setSubject("votre code d'activation");

       String texte = String.format("bonjour %S, <br?> votre code d'activatoin est %S; A bientot",
            validation.getAppUser().getName(),
            validation.getCode()

            );
        message.setText(texte);

        mailSender.send(message);
    }
}
