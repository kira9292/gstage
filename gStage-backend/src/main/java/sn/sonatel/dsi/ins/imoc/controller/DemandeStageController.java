package sn.sonatel.dsi.ins.imoc.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.sonatel.dsi.ins.imoc.domain.Candidat;
import sn.sonatel.dsi.ins.imoc.domain.DemandeStage;
import sn.sonatel.dsi.ins.imoc.domain.ValidationStatuscandidat;
import sn.sonatel.dsi.ins.imoc.dto.DemandeStagecandidatDTO;
import sn.sonatel.dsi.ins.imoc.repository.CandidatRepository;
import sn.sonatel.dsi.ins.imoc.repository.DemandeStageRepository;
import sn.sonatel.dsi.ins.imoc.service.DemandeStageService;
import sn.sonatel.dsi.ins.imoc.service.ValidationCanditatService;

import java.util.Map;

@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class DemandeStageController {

    @Autowired
    DemandeStageRepository demandeStageRepository;

    @Autowired
    private CandidatRepository candidatRepository;
    @Autowired
    private ValidationCanditatService validationCanditatService;
    @Autowired
    private DemandeStageService demandeStageService;

    @PostMapping("/api/postuler")
    public void soumettre(@RequestBody DemandeStagecandidatDTO demande) {
        Candidat c = demande.getCandidat();
        this.candidatRepository.save(c);

        DemandeStage d = demande.getDemandeStage();
        d.setCandidat(c);
        this.demandeStageRepository.save(d);
        this.validationCanditatService.enregistrer(c);


    }

    @PostMapping("/api/validerdemande")
    public void activation(@RequestBody Map<String ,String> code) {

        this.demandeStageService.activation(code);
    }

}
