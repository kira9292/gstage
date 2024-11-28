package sn.sonatel.dsi.ins.imoc.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.domain.DemandeStage;
import sn.sonatel.dsi.ins.imoc.domain.StagiairesProposer;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.InternshipStatus;
import sn.sonatel.dsi.ins.imoc.dto.DemandeManagerDto;
import sn.sonatel.dsi.ins.imoc.repository.AppUserRepository;
import sn.sonatel.dsi.ins.imoc.repository.CandidatRepository;
import sn.sonatel.dsi.ins.imoc.repository.DemandeStageRepository;
import sn.sonatel.dsi.ins.imoc.repository.StagiairesProposerRepository;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Set;

@RestController
public class AssistantGWTEController {
    @Autowired
    private DemandeStageRepository demandeStageRepository;
    @Autowired
    private CandidatRepository candidatRepository;

    private final StagiairesProposerRepository stagiairesProposerRepository;
    private final AppUserRepository appUserRepository;

    public AssistantGWTEController(StagiairesProposerRepository stagiairesProposerRepository , AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
        this.stagiairesProposerRepository = stagiairesProposerRepository;
    }

    @PostMapping("proposer-to-manager")
    public ResponseEntity<Void> createStagiairesProposer(@RequestBody StagiairesProposer stagiairesProposer){
        stagiairesProposerRepository.save(stagiairesProposer);
        return ResponseEntity.ok().build();
    }

    @PostMapping("api/demande-to-manager")
    public ResponseEntity<Void> associerDemandeToManager(@RequestBody DemandeManagerDto demandeManagerDto) {
        // Récupérer AppUser et DemandeStage existants dans la base de données
        AppUser appUser = appUserRepository.findByEmail(demandeManagerDto.appUser().getEmail())
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + demandeManagerDto.appUser().getId()));

        DemandeStage demandeStage = demandeStageRepository.findById(demandeManagerDto.demandeStage().getId())
            .orElseThrow(() -> new RuntimeException("Demande de stage non trouvée avec l'ID : " + demandeManagerDto.demandeStage().getId()));

        // Ajouter la demande de stage à l'utilisateur
        demandeStage.setStatus(InternshipStatus.PROPOSE);
        demandeStage.setAppUser(appUser);
        demandeStageRepository.save(demandeStage);
        System.out.println();
        return ResponseEntity.ok().build();
    }
}
