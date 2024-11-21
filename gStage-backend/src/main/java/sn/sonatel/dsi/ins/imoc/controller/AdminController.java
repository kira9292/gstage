package sn.sonatel.dsi.ins.imoc.controller;


import io.micrometer.common.util.internal.logging.InternalLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sn.sonatel.dsi.ins.imoc.dto.UserDTO;
import sn.sonatel.dsi.ins.imoc.repository.AppUserRepository;
import sn.sonatel.dsi.ins.imoc.service.AppUserService;


@RestController
public class AdminController {
    private final AppUserService appUserService;

    public AdminController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping("/api/inscription")
    public void incription(@RequestBody UserDTO appUser) {
        this.appUserService.inscription(appUser);
    }
}
