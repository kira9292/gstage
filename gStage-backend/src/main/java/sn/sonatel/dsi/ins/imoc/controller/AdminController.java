package sn.sonatel.dsi.ins.imoc.controller;


import io.micrometer.common.util.internal.logging.InternalLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sn.sonatel.dsi.ins.imoc.dto.UserDTO;
import sn.sonatel.dsi.ins.imoc.dto.UserForAdminDTO;
import sn.sonatel.dsi.ins.imoc.repository.AppUserRepository;
import sn.sonatel.dsi.ins.imoc.service.AppUserService;

import java.util.List;
import java.util.stream.Collectors;


@RestController
public class AdminController {
    private final AppUserService appUserService;
    private final AppUserRepository appUserRepository;

    public AdminController(
        AppUserService appUserService,
        AppUserRepository appUserRepository
    ) {
        this.appUserService = appUserService;
        this.appUserRepository = appUserRepository;
    }

    @PostMapping("/api/inscription")
    public void incription(@RequestBody UserDTO appUser) {
        this.appUserService.inscription(appUser);
    }

    @GetMapping("/api/")
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





}




