package sn.sonatel.dsi.ins.imoc.controller;


import jakarta.mail.MessagingException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.sonatel.dsi.ins.imoc.domain.Candidat;
import sn.sonatel.dsi.ins.imoc.domain.DemandeStage;
import sn.sonatel.dsi.ins.imoc.domain.ValidationStatuscandidat;
import sn.sonatel.dsi.ins.imoc.dto.DemandeStagecandidatDTO;
import sn.sonatel.dsi.ins.imoc.repository.CandidatRepository;
import sn.sonatel.dsi.ins.imoc.repository.DemandeStageRepository;
import sn.sonatel.dsi.ins.imoc.service.DemandeStageService;
import sn.sonatel.dsi.ins.imoc.service.ValidationCanditatService;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    @PostMapping("/api/sendWelcomeEmail")
    public void contactercadidat( @RequestBody Map<String ,String> mail ) throws MessagingException, UnsupportedEncodingException {
        this.demandeStageService.accepterstagiaire(mail);
    }
    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @PutMapping("/api/internships/{id}/reject")
    public Optional<String> rejectInternship(@PathVariable Long id) {
        return demandeStageService.rejectInternship(id);
    }

    @PutMapping("/api/internships/{id}/archive")
    public Optional<String> archiveInternship(@PathVariable Long id) {
        return demandeStageService.archiveInternship(id);
    }

    @GetMapping("/api/internships/archived")
    public List<DemandeStage> getArchivedInternships() {
        return demandeStageService.findAllArchived();
    }
}
