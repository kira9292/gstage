package sn.sonatel.dsi.ins.imoc.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.DemandeStage;
import sn.sonatel.dsi.ins.imoc.repository.DemandeStageRepository;

@Service
public class DemandeStageService {

    @Autowired
    private DemandeStageRepository demandeStageRepository;

    public void postuler(DemandeStage demandeStage) {
        demandeStageRepository.save(demandeStage);
    }

}
