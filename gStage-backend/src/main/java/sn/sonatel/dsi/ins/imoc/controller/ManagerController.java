package sn.sonatel.dsi.ins.imoc.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.ERole;
import sn.sonatel.dsi.ins.imoc.dto.ManagerDTO;
import sn.sonatel.dsi.ins.imoc.repository.AppUserRepository;
import sn.sonatel.dsi.ins.imoc.repository.RoleRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ManagerController {

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    public ManagerController(AppUserRepository appUserRepository, RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        this.appUserRepository = appUserRepository;

    }
    @GetMapping("api/managers")
    public List<ManagerDTO> getAppUsers() {
        // Récupérer les utilisateurs avec le rôle MANAGER
        List<AppUser> users = appUserRepository.findByRoleName(ERole.MANAGER);

        // Transformer les utilisateurs en ManagerDTO
        List<ManagerDTO> managerDTOs = users.stream()
            .map(user -> {
                // Récupérer le nom du service associé à cet utilisateur
                String serviceName = null;
                if (user.getService() != null) {
                    serviceName = user.getService().getName(); // Accéder au nom du service
                }
                return new ManagerDTO(user.getName(),user.getFirstName() ,user.getEmail(), serviceName);
            })
            .collect(Collectors.toList());

        return managerDTOs;
    }
//@GetMapping("api/managers")
//public List<AppUser> getAppUsers() {
//    // Récupérer les utilisateurs avec le rôle MANAGER
//    List<AppUser> users = appUserRepository.findByRoleName(ERole.MANAGER);
//
//    // Transformer les utilisateurs en ManagerDTO
//    List<ManagerDTO> managerDTOs = users.stream()
//        .map(user -> new ManagerDTO(user.getName(), user.getEmail()))
//        .collect(Collectors.toList());
//
//    return users;
//}


}
