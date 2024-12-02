package sn.sonatel.dsi.ins.imoc.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.sonatel.dsi.ins.imoc.domain.*;
import sn.sonatel.dsi.ins.imoc.repository.CandidatRepository;
import sn.sonatel.dsi.ins.imoc.repository.ContratRepository;
import sn.sonatel.dsi.ins.imoc.service.StagiaireService;

import java.util.List;

@RestController
public class StagiaireContratController {

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private CandidatRepository candidatRepository;


    @GetMapping("/api/stagiaire/contrat")
    public Contrat getContrat(){

        AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal() ;

        Candidat ca = this.candidatRepository.findByAppUser(user);

        Contrat contrat = this.contratRepository.findByCandidat(ca);

        return contrat ;
    }






}
