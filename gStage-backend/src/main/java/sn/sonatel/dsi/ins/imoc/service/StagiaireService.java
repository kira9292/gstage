package sn.sonatel.dsi.ins.imoc.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.*;
import sn.sonatel.dsi.ins.imoc.repository.AttestationFinStageRepository;
import sn.sonatel.dsi.ins.imoc.repository.AttestationPresenceRepository;
import sn.sonatel.dsi.ins.imoc.repository.ContratRepository;
import sn.sonatel.dsi.ins.imoc.repository.EtatPaiementRepository;

import java.util.List;

@Service
public class StagiaireService {


    private final ContratRepository contratRepository;

    private final AttestationPresenceRepository attestationPresenceRepository;

    private final AttestationFinStageRepository attestationFinStageRepository;

    private final EtatPaiementRepository etatPaiementRepository;

    @Autowired
    public StagiaireService(ContratRepository contratRepository, AttestationPresenceRepository attestationPresenceRepository, AttestationFinStageRepository attestationFinStageRepository, EtatPaiementRepository etatPaiementRepository) {
        this.contratRepository = contratRepository;
        this.attestationPresenceRepository = attestationPresenceRepository;
        this.attestationFinStageRepository = attestationFinStageRepository;
        this.etatPaiementRepository = etatPaiementRepository;
    }





    public List<Contrat> getContrat(AppUser user) {

        return contratRepository.getAllByAppUser(user);
    }

    public List<AttestationPresence> getattestationdepresence(AppUser user) {

        return this.attestationPresenceRepository.getAllByAppUser(user);
    }

    public AttestationFinStage getattestationfinstage(AppUser user) {
        return attestationFinStageRepository.getByAppuser(user);
    }

    public List<EtatPaiement> getEtatpaiement(AppUser user) {

        return etatPaiementRepository.getAllByAppUser(user);
    }
}
