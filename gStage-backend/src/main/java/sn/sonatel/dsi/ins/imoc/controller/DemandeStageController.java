package sn.sonatel.dsi.ins.imoc.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.domain.DemandeStage;
import sn.sonatel.dsi.ins.imoc.repository.DemandeStageRepository;
import sn.sonatel.dsi.ins.imoc.service.DemandeStageService;

@RestController
public class DemandeStageController {

    @Autowired
    DemandeStageRepository demandeStageRepository;
    @PostMapping("/api/postuler")
    public void postuler(@RequestBody DemandeStage demandeStage) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        demandeStage.setAppUser(appUser);
        this.demandeStageRepository.save(demandeStage);
    }
}
