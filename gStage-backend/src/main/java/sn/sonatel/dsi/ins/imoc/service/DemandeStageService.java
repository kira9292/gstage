package sn.sonatel.dsi.ins.imoc.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.*;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.InternshipStatus;
import sn.sonatel.dsi.ins.imoc.repository.CandidatRepository;
import sn.sonatel.dsi.ins.imoc.repository.DemandeStageRepository;
import sn.sonatel.dsi.ins.imoc.repository.ValidationStatuscandidatRepository;

import java.time.Instant;
import java.util.Map;

@Service
public class DemandeStageService {

    @Autowired
    private DemandeStageRepository demandeStageRepository;
    @Autowired
    private ValidationStatuscandidatRepository validationStatuscandidatRepository;
    @Autowired
    private ValidationCanditatService validationCanditatService;

    @Autowired
    private CandidatRepository candidatRepository;

    public void activation(Map<String, String> code) {
        ValidationStatuscandidat validation = this.validationCanditatService.getUserByCode(code.get("code"));
        if (Instant.now().isAfter(validation.getExpire())){
            Candidat c = candidatRepository.findByValidationStatuscandidatCode(code.get("code"));
//            this.validationStatuscandidatRepository.delete(validation);
//            this.demandeStageRepository.delete(c.getDemandeStage());
//            this.candidatRepository.delete(c);
            throw new RuntimeException("code expired");

        }else {
            validation.setActivation(Instant.now());
            this.validationStatuscandidatRepository.save(validation);

            Candidat c = candidatRepository.findByValidationStatuscandidatCode(code.get("code"));
            c.getDemandeStage().setStatus(InternshipStatus.EN_ATTENTE);

        }


    }
}
