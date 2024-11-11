package sn.sonatel.dsi.ins.imoc.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.sonatel.dsi.ins.imoc.repository.CandidatRepository;
import sn.sonatel.dsi.ins.imoc.repository.DemandeStageRepository;

@RestController
public class AssistantGWTEController {


    @Autowired
    private DemandeStageRepository demandeStageRepository;
    @Autowired
    private CandidatRepository candidatRepository;


    @GetMapping
    public Object listdemande() {
        return null;
    }






}
