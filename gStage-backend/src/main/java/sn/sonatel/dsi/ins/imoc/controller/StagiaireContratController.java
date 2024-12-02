package sn.sonatel.dsi.ins.imoc.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.sonatel.dsi.ins.imoc.domain.*;
import sn.sonatel.dsi.ins.imoc.repository.AttestationFinStageRepository;
import sn.sonatel.dsi.ins.imoc.repository.AttestationPresenceRepository;
import sn.sonatel.dsi.ins.imoc.repository.CandidatRepository;
import sn.sonatel.dsi.ins.imoc.repository.ContratRepository;

import java.util.List;
import java.util.Optional;

@RestController
public class StagiaireContratController {

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private AttestationPresenceRepository attestationPresenceRepository;

    @Autowired
    private AttestationFinStageRepository attestationFinStageRepository;

    @GetMapping("/api/stagiaire/contrat")
    public Contrat getContrat(){

        AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal() ;

        Candidat ca = this.candidatRepository.findByAppUser(user);

        List<Contrat> contrats = this.contratRepository.findByCandidat(ca);

        return (contrats.isEmpty()) ? null : contrats.get(contrats.size()-1);
    }

    @GetMapping("/api/stagiare/attestation-de-presence")
    public List<AttestationPresence> getAttestationPresence(){
        AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal() ;
        Candidat ca = this.candidatRepository.findByAppUser(user);

       List<AttestationPresence>  attestationPresence = this.attestationPresenceRepository.findByCandidat(ca);

        return attestationPresence;
    }

    @GetMapping("/api/stagiare/attestation-de-finstage")
    public Optional<AttestationFinStage> getAttestationFinStage(){
        AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal() ;
        Candidat ca = this.candidatRepository.findByAppUser(user);

        AttestationFinStage fs = ca.getAttestationFinStage();

        return this.attestationFinStageRepository.findById(fs.getId());
    }




}
