package sn.sonatel.dsi.ins.imoc.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.domain.ValidationStatusUser;
import sn.sonatel.dsi.ins.imoc.repository.ValidationStatusUserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
public class ValidationUserService {
    @Autowired
    private ValidationStatusUserRepository validation;

    @Autowired
    private NotificationService notificationService;
    public   void enregistrer(AppUser user) {
        ValidationStatusUser validation = new ValidationStatusUser();
        validation.setAppUser(user);
        Instant created = Instant.now();
        Instant expiration = created.plus(10, MINUTES);
        validation.setExpire(expiration);
        Random random = new Random();
        int randomInt = random.nextInt(999999);
        String code = String.format("%04d", randomInt);

        validation.setCode(code);
        this.validation.save(validation);
        this.notificationService.envoyer(validation);
    }

    public ValidationStatusUser getUserByCode(String code) {
       return this.validation.findByCode(code).orElseThrow(()->new RuntimeException("votre code est invalide")) ;
    }
}
