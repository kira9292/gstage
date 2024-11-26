package sn.sonatel.dsi.ins.imoc.controller;


import io.micrometer.common.util.internal.logging.InternalLogger;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.domain.Departement;
import sn.sonatel.dsi.ins.imoc.domain.Role;
import sn.sonatel.dsi.ins.imoc.domain.Service;
import sn.sonatel.dsi.ins.imoc.dto.ServiceWithDepartmentDTO;
import sn.sonatel.dsi.ins.imoc.dto.UserDTO;
import sn.sonatel.dsi.ins.imoc.dto.UserForAdminDTO;
import sn.sonatel.dsi.ins.imoc.repository.*;
import sn.sonatel.dsi.ins.imoc.service.AppUserService;
import tech.jhipster.web.util.PaginationUtil;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@RestController
public class AdminController {
    private final AppUserService appUserService;
    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final ServiceRepository serviceRepository;
    private final DemandeStageRepository demandeStageRepository;
    private final DepartementRepository departementRepository;

    public AdminController(
        AppUserService appUserService,
        AppUserRepository appUserRepository,
        RoleRepository roleRepository,
        ServiceRepository serviceRepository,
        DemandeStageRepository demandeStageRepository,
        DepartementRepository departementRepository
    ) {
        this.appUserService = appUserService;
        this.appUserRepository = appUserRepository;
        this.roleRepository = roleRepository;
        this.serviceRepository = serviceRepository;
        this.demandeStageRepository = demandeStageRepository;
        this.departementRepository = departementRepository;
    }

    @PostMapping("/api/inscription")
    public void incription(@RequestBody UserDTO appUser) {
        this.appUserService.inscription(appUser);
    }

    @GetMapping("/api/list-users")
    public List<UserForAdminDTO> getUsers() {
        return this.appUserRepository.findAll()
            .stream()
            .map(appUser -> new UserForAdminDTO(
                appUser.getId(),
                appUser.getUsername(),
                appUser.getEmail(),
                appUser.getName(),
                appUser.getFirstName(),
                appUser.getPhone(),
                appUser.getRole() != null ? String.valueOf(appUser.getRole().getName()) : null,
                appUser.getService() != null ? appUser.getService().getName() : null
            ))
            .collect(Collectors.toList());


    }


    @PutMapping("/api/users/{id}")
    public ResponseEntity<UserForAdminDTO> updateUser(
        @PathVariable Long id,
        @RequestBody UserForAdminDTO userDto) {

        // Vérifiez si l'utilisateur existe
        AppUser existingUser = appUserRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID : " + id));

        // Mettez à jour les champs nécessaires
        existingUser.setName(userDto.name());
        existingUser.setFirstName(userDto.firstName());
        existingUser.setEmail(userDto.email());
        existingUser.setPhone(userDto.phone());

        // Si le rôle est mis à jour
        if (userDto.roleName() != null) {
            Role role = roleRepository.findByName(userDto.roleName())
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé : " + userDto.roleName()));
            existingUser.setRole(role);
        }

        // Si le service est mis à jour
        if (userDto.serviceName() != null) {
            Service service = serviceRepository.findByName(userDto.serviceName())
                .orElseThrow(() -> new RuntimeException("Service non trouvé : " + userDto.serviceName()));
            existingUser.setService(service);
        }

        // Sauvegardez les modifications
        AppUser updatedUser = appUserRepository.save(existingUser);

        // Retournez un DTO
        UserForAdminDTO updatedDto = new UserForAdminDTO(
            updatedUser.getId(),
            updatedUser.getUsername(),
            updatedUser.getEmail(),
            updatedUser.getName(),
            updatedUser.getFirstName(),
            updatedUser.getPhone(),
            updatedUser.getRole() != null ? String.valueOf(updatedUser.getRole().getName()) : null,
            updatedUser.getService() != null ? updatedUser.getService().getName() : null
        );

        return ResponseEntity.ok(updatedDto);
    }

    @Transactional
    @DeleteMapping("/api/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // Vérifiez si l'utilisateur existe
        if (!appUserRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé avec l'ID : " + id);
        }


        // Optionnel : Supprimer les enregistrements enfants avant de supprimer l'utilisateur
//        demandeStageRepository.deleteByAppUserId(id);

        // Supprimez l'utilisateur
        appUserRepository.deleteById(id);

        // Retournez une réponse vide avec le statut HTTP 204 (No Content)
        return ResponseEntity.noContent().build();
    }




    @GetMapping("/api/list-services")
    public ResponseEntity<List<Service>> getAllServices(){
             List<Service>
                 servicesSansUtilisateur =
                 serviceRepository.findByAppUserIsNull();
             return ResponseEntity.ok(servicesSansUtilisateur);
    }

    @Transactional
    @GetMapping("/api/list-services-and-departments")
    public ResponseEntity<List<ServiceWithDepartmentDTO>> getServicesWithDepartments() {
        List<Service> services = serviceRepository.findByDepartemenNotNull();

        // Construire la liste des DTO à partir des entités
        List<ServiceWithDepartmentDTO> response = services.stream()
            .map(service -> new ServiceWithDepartmentDTO(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getDepartemen() != null ? service.getDepartemen().getId() : null,
                service.getDepartemen() != null ? service.getDepartemen().getName() : null
            ))
            .toList();

        return ResponseEntity.ok(response);
    }




    @PostMapping("/api/depts-services")
    public ResponseEntity<Service> saveOrUpdateService(@RequestBody ServiceWithDepartmentDTO request) {
        Service service;

        // Si l'ID du service existe, on fait une mise à jour
        if (request.id() != null) {
            service = serviceRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("Service non trouvé avec ID : " + request.id()));
        } else {
            // Sinon, on crée un nouveau service
            service = new Service();
        }

        // Mise à jour des champs du service
        service.setName(request.name());
        service.setDescription(request.description());

        // Associer un département s'il est fourni
        if (request.departmentId() != null) {
            Departement departement = departementRepository.findById(request.departmentId())
                .orElseThrow(() -> new RuntimeException("Département non trouvé avec ID : " + request.departmentId()));
            service.setDepartemen(departement);
        } else {
            service.setDepartemen(null); // Aucun département associé
        }

        // Sauvegarder le service
        Service savedService = serviceRepository.save(service);

        return ResponseEntity.ok(savedService);
    }








}






