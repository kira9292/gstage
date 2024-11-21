package sn.sonatel.dsi.ins.imoc.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import sn.sonatel.dsi.ins.imoc.dto.ValidationRequest;
import sn.sonatel.dsi.ins.imoc.repository.DemandeStageRepository;
import sn.sonatel.dsi.ins.imoc.service.ManagerService;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/manager-internships")
public class ManagerController {
    @Autowired
    private ManagerService managerService;

    @PatchMapping("/{requestId}/validate")
    public ResponseEntity<?> validateInternshipRequest(
        @PathVariable Long requestId,
        @RequestBody ValidationRequest validationRequest
    ) {
        return managerService.validateInternshipRequest(requestId, validationRequest);
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

    @GetMapping("/api/demande-proposer-manager")
    public List<DemandeStage> managertodemande(){

        AppUser user =(AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
         String email =  user.getEmail();


         return this.demandeStageRepository.findByAppUser(user);



    }

