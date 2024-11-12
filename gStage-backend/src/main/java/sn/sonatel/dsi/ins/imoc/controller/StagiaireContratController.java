package sn.sonatel.dsi.ins.imoc.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.sonatel.dsi.ins.imoc.domain.*;
import sn.sonatel.dsi.ins.imoc.repository.ContratRepository;
import sn.sonatel.dsi.ins.imoc.service.StagiaireService;

import java.util.List;

@RestController
public class StagiaireContratController {

    @Autowired
    private StagiaireService stagiaireService;



    @GetMapping("/api/stagiaire/documents/contrats")
    public List<Contrat> getContrat() {

        AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return this.stagiaireService.getContrat(user) ;
    }
    @GetMapping("/api/stagiaire/documents/presence-attestations")
    public List<AttestationPresence> attestationdepresence() {

        AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return this.stagiaireService.getattestationdepresence(user) ;
    }


    @GetMapping("/api/stagiaire/documents/ending-attestation")
    public AttestationFinStage attestationFinStage() {

        AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return this.stagiaireService.getattestationfinstage(user) ;
    }

    @GetMapping("/api/stagiaire/payments")
    public List<EtatPaiement> etatPaiementList() {

        AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return this.stagiaireService.getEtatpaiement(user) ;
    }

}
