package sn.sonatel.dsi.ins.imoc.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.dto.AuthentificationDTO;
import sn.sonatel.dsi.ins.imoc.dto.ManagerDTO2;
import sn.sonatel.dsi.ins.imoc.dto.UserDTO;
import sn.sonatel.dsi.ins.imoc.service.AppUserService;
import sn.sonatel.dsi.ins.imoc.service.JwtService;

import javax.print.attribute.standard.Media;
import java.awt.*;
import java.util.Map;

@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;




//@PostMapping("/api/inscription")
//public void incription(@RequestBody UserDTO appUser) {
//    log.info("Incription of {}", appUser);
//    this.appUserService.inscription(appUser);
//}
    @PostMapping("/api/activation")
    public void activation(@RequestBody Map<String ,String> activation) {

        this.appUserService.activation(activation);
    }

    @PostMapping("/api/connexion")
    public Map<String,String> connexion(@RequestBody AuthentificationDTO authentificationDTO) {
       final Authentication authenticate = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authentificationDTO.username(),authentificationDTO.password())
        );
        if(authenticate.isAuthenticated()){
            return this.jwtService.generate(authentificationDTO.username());
        }
        return null;
    }
    @PostMapping("/api/deconnexion")
    public ResponseEntity<Void> logout() {
        this.jwtService.deconnexion();
        return ResponseEntity.ok().build();
    }

    @GetMapping("api/users/{id}")
    public ResponseEntity<ManagerDTO2> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(appUserService.getUserById(id));
    }

}
