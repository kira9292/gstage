package sn.sonatel.dsi.ins.imoc.controller;


import jakarta.mail.MessagingException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.domain.DemandeStage;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.ERole;
import sn.sonatel.dsi.ins.imoc.dto.AttestationPDTO;
import sn.sonatel.dsi.ins.imoc.dto.DemandeStagecandidatDTO;
import sn.sonatel.dsi.ins.imoc.dto.ManagerDTO;
import sn.sonatel.dsi.ins.imoc.repository.AppUserRepository;
import sn.sonatel.dsi.ins.imoc.repository.DemandeStageRepository;
import sn.sonatel.dsi.ins.imoc.repository.RoleRepository;
import sn.sonatel.dsi.ins.imoc.service.ManagerService;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
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
        DemandeStageRepository demandeStageRepository, ManagerService managerService
    ) {
        this.demandeStageRepository = demandeStageRepository;
        this.roleRepository = roleRepository;
        this.appUserRepository = appUserRepository;

        this.managerService = managerService;
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

    @GetMapping("/api/manager-internships")
    public List<DemandeStagecandidatDTO> managertodemande() {
        // Récupérer l'utilisateur actuellement connecté
        AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Rechercher toutes les demandes associées à cet utilisateur
        List<DemandeStage> demandes = this.demandeStageRepository.findByAppUser(user);

        // Transformer chaque demande en un DTO contenant la demande et le candidat associé
        return demandes.stream()
            .map(demandeStage -> new DemandeStagecandidatDTO(
                demandeStage,
                demandeStage.getCandidat() // Suppose que DemandeStage a une relation avec Candidat
            ))
            .toList();
    }

    @PutMapping("/api/manager-internships/{internshipId}/validate")
    public Optional<String> validateInternship(@PathVariable Long internshipId) {
        return managerService.validateInternshipRequest(internshipId);
    }

    @PutMapping("/api/manager-internships/{internshipId}/reject")
    public Optional<String> rejectInternship(@PathVariable Long internshipId) {
        return managerService.rejectInternshipRequest(internshipId);
    }

    @PutMapping("/api/manager-internships/{internshipId}/confirmDebut")
    public Optional<String> confirmInternshipStart(@PathVariable Long internshipId) {
        return managerService.confirmInternshipStart(internshipId);
    }

    @PutMapping("/api/manager-internships/{internshipId}/markAsEnded")
    public Optional<String> markInternshipAsEnded(@PathVariable Long internshipId) {
        return managerService.markInternshipAsEnded(internshipId);
    }

    @PostMapping("/api/manager-internships/sendAttestation")
    public void attestationStagiaire( @RequestBody Map<String ,String> mail ) throws MessagingException, UnsupportedEncodingException {
        this.managerService.generateAttestation(mail);
    }


    @PostMapping("/api/manager-internships/send-presence-attestation")
    public void attestationPresence(@RequestBody AttestationPDTO request) throws MessagingException, UnsupportedEncodingException {
        this.managerService.generateAttestationPresence(request);
    }
}
