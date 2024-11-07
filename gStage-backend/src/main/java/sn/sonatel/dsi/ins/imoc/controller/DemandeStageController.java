package sn.sonatel.dsi.ins.imoc.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.domain.Candidat;
import sn.sonatel.dsi.ins.imoc.domain.DemandeStage;
import sn.sonatel.dsi.ins.imoc.dto.DemandeStagecandidatDTO;
import sn.sonatel.dsi.ins.imoc.repository.CandidatRepository;
import sn.sonatel.dsi.ins.imoc.repository.DemandeStageRepository;
import sn.sonatel.dsi.ins.imoc.service.DemandeStageService;

@RestController
public class DemandeStageController {

    @Autowired
    DemandeStageRepository demandeStageRepository;

    @Autowired
    private CandidatRepository candidatRepository;

    @PostMapping("/api/demandeStage")
    public void create(@RequestBody DemandeStage demandeStage) {
        this.demandeStageRepository.save(demandeStage);
    }
    @PostMapping("/api/candidater")
    public void postuler(@RequestBody Candidat candidat) {

        this.candidatRepository.save(candidat);
    }

    @PostMapping("/api/candidater")
    public void soumettre(@RequestBody DemandeStagecandidatDTO demande) {

        Candidat c = demande.getCandidat();
        this.candidatRepository.save(c);
        DemandeStage d = demande.getDemandeStage();
        d.setCandidat(c);
        this.candidatRepository.save(c);
        
    }

}
