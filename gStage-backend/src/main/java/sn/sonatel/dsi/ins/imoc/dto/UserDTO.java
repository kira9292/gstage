package sn.sonatel.dsi.ins.imoc.dto;

import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.domain.Role;


public record UserDTO(AppUser appUser, Role role) {

    public AppUser getAppUser() {
        return appUser;
    }
    public Role getRole() {
        return role;
    }
}
