package sn.sonatel.dsi.ins.imoc.dto;

import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.domain.Candidat;
import sn.sonatel.dsi.ins.imoc.domain.Role;
import sn.sonatel.dsi.ins.imoc.domain.Service;


public record UserDTO(AppUser appUser, Role role , Service service ) {



    public Service getService() {
        return service;
    }

    public AppUser getAppUser() {
        return appUser;
    }
    public Role getRole() {
        return role;
    }
}
