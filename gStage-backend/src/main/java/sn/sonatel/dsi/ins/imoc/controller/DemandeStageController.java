package sn.sonatel.dsi.ins.imoc.controller;


import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import sn.sonatel.dsi.ins.imoc.domain.Candidat;
import sn.sonatel.dsi.ins.imoc.domain.DemandeStage;
import sn.sonatel.dsi.ins.imoc.domain.ValidationStatuscandidat;
import sn.sonatel.dsi.ins.imoc.dto.DemandeStagecandidatDTO;
import sn.sonatel.dsi.ins.imoc.repository.CandidatRepository;
import sn.sonatel.dsi.ins.imoc.repository.DemandeStageRepository;
import sn.sonatel.dsi.ins.imoc.service.DemandeStageService;
import sn.sonatel.dsi.ins.imoc.service.ValidationCanditatService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @GetMapping("/api/demandes")
    public List<DemandeStagecandidatDTO> getAllDemandes() {
        // Récupérer toutes les demandes de stage avec leurs candidats associés
        List<DemandeStage> demandes = demandeStageRepository.findAll();

        // Transformer chaque demande en un DemandeStagecandidatDTO avec le candidat associé
        return demandes.stream()
            .map(demande -> new DemandeStagecandidatDTO(
                demande,
                demande.getCandidat()
            ))
            .collect(Collectors.toList());
    }

//    @PostMapping("/resendcode")
//    public void renvoicode( @RequestBody Map<String ,String> mail ) {
//
//        this.demandeStageService.resendcode(mail);
//
//    }


}
