package sn.sonatel.dsi.ins.imoc.controller;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.domain.DemandeStage;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.ERole;
import sn.sonatel.dsi.ins.imoc.dto.ManagerDTO;
import sn.sonatel.dsi.ins.imoc.repository.AppUserRepository;
import sn.sonatel.dsi.ins.imoc.repository.DemandeStageRepository;
import sn.sonatel.dsi.ins.imoc.repository.RoleRepository;
import sn.sonatel.dsi.ins.imoc.service.ManagerService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class ManagerController {

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final DemandeStageRepository demandeStageRepository;
    private final ManagerService managerService;

    public ManagerController(
        AppUserRepository appUserRepository,
        RoleRepository roleRepository,
        DemandeStageRepository demandeStageRepository,
        ManagerService managerService) {
        this.demandeStageRepository = demandeStageRepository;
        this.roleRepository = roleRepository;
        this.appUserRepository = appUserRepository;
        this.managerService = managerService;
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

    @GetMapping("/api/manager-internships")
    public List<DemandeStage> managertodemande(){

        AppUser user =(AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
         String email =  user.getEmail();

         return this.demandeStageRepository.findByAppUser(user);

    }

    @PutMapping("/api/manager-internships/{internshipId}/validate")
    public Optional<String> validateInternship(@PathVariable Long internshipId) {
        return managerService.validateInternshipRequest(internshipId);
    }

    @PutMapping("/api/manager-internships/{internshipId}/reject")
    public Optional<String> rejectInternship(@PathVariable Long internshipId) {
        return managerService.rejectInternshipRequest(internshipId);
    }

}
