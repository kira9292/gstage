package sn.sonatel.dsi.ins.imoc.controller;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.domain.DemandeStage;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.ERole;
import sn.sonatel.dsi.ins.imoc.dto.ManagerDTO;
import sn.sonatel.dsi.ins.imoc.repository.AppUserRepository;
import sn.sonatel.dsi.ins.imoc.repository.DemandeStageRepository;
import sn.sonatel.dsi.ins.imoc.repository.RoleRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ManagerController {

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final DemandeStageRepository demandeStageRepository;
    public ManagerController(
        AppUserRepository appUserRepository,
        RoleRepository roleRepository,
        DemandeStageRepository demandeStageRepository
    ) {
        this.demandeStageRepository = demandeStageRepository;
        this.roleRepository = roleRepository;
        this.appUserRepository = appUserRepository;

    }
    @GetMapping("api/managers")
    public List<ManagerDTO> getAppUsers() {
        List<AppUser> users = appUserRepository.findByRoleName(ERole.MANAGER);

        List<ManagerDTO> managerDTOs = users.stream()
            .map(user -> {
                String serviceName = null;
                if (user.getService() != null) {
                    serviceName = user.getService().getName(); // Accéder au nom du service
                }
                return new ManagerDTO(user.getName(),user.getFirstName() ,user.getEmail(), serviceName);
            })
            .collect(Collectors.toList());

        return managerDTOs;
    }

    @GetMapping("/api/demande-proposer-manager")
    public List<DemandeStage> managertodemande(){

        AppUser user =(AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
         String email =  user.getEmail();


         return this.demandeStageRepository.findByAppUser(user);



    }


}
