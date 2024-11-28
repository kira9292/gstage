package sn.sonatel.dsi.ins.imoc.dto;

import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.domain.Candidat;

public record CandidatToStagiaireDTO(AppUser appUser , Candidat candidat) {

    @Override
    public AppUser appUser() {
        return appUser;
    }

    @Override
    public Candidat candidat() {
        return candidat;
    }
}
