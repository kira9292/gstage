package sn.sonatel.dsi.ins.imoc.dto;

import sn.sonatel.dsi.ins.imoc.domain.*;

import java.util.List;

public record CandidatCompleTDO(
    DemandeStage demandeStage ,
    Candidat candidat,
    List<AttestationPresence> attestationPresence,
    AttestationFinStage attestationFinStage,
    Contrat contrat
    ) {
    @Override
    public Contrat contrat() {
        return contrat;
    }

    @Override
    public DemandeStage demandeStage() {
        return demandeStage;
    }



    @Override
    public AttestationFinStage attestationFinStage() {
        return attestationFinStage;
    }

    @Override
    public List<AttestationPresence> attestationPresence() {
        return attestationPresence;
    }

    @Override
    public Candidat candidat() {
        return candidat;
    }
}
