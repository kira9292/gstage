package sn.sonatel.dsi.ins.imoc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.domain.Candidat;
import sn.sonatel.dsi.ins.imoc.domain.ValidationStatusUser;
import sn.sonatel.dsi.ins.imoc.domain.ValidationStatuscandidat;
import sn.sonatel.dsi.ins.imoc.repository.ValidationStatusUserRepository;
import sn.sonatel.dsi.ins.imoc.repository.ValidationStatuscandidatRepository;

import java.time.Instant;
import java.util.Random;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;

@Service
public class ValidationCanditatService {

    @Autowired
    private ValidationStatuscandidatRepository validation;
    @Autowired
    private NotificationService notificationService;

    public   void enregistrer(Candidat user) {
        ValidationStatuscandidat validation = new ValidationStatuscandidat();
        validation.setCandidat(user);
        Instant created = Instant.now();
        Instant expiration = created.plus(10, MINUTES);
        validation.setExpire(expiration);
        validation.setCreation(created);
        Random random = new Random();
        int randomInt = random.nextInt(999999);
        String code = String.format("%04d", randomInt);
        validation.setCode(code);
         this.validation.save(validation);

        this.notificationService.envoyerCandidat(validation);
    }

    public ValidationStatuscandidat getUserByCode(String code) {
        return this.validation.findByCode(code).orElseThrow(()->new RuntimeException("votre code est invalide")) ;
    }
}
