package sn.sonatel.dsi.ins.imoc.dto;

import lombok.Getter;
import lombok.Setter;
import sn.sonatel.dsi.ins.imoc.domain.Candidat;
import sn.sonatel.dsi.ins.imoc.domain.DemandeStage;


public record DemandeStagecandidatDTO(DemandeStage demandeStage, Candidat candidat) {
    public DemandeStage getDemandeStage() {
        return demandeStage;
    }
    public Candidat getCandidat() {
        return candidat;
    }
}
