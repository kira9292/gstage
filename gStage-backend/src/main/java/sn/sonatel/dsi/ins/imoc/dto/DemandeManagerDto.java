package sn.sonatel.dsi.ins.imoc.dto;

import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.domain.DemandeStage;

import java.util.Set;

public record DemandeManagerDto(DemandeStage demandeStage , AppUser appUser) {

    public DemandeStage demandeStage() {
        return demandeStage;
    }

    @Override
    public AppUser appUser() {
        return appUser;
    }
}
