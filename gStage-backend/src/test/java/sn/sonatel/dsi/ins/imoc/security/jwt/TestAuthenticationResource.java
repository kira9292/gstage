package sn.sonatel.dsi.ins.imoc.security.jwt;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing testing authentication token.
 */
@RestController
@RequestMapping("/api")
public class TestAuthenticationResource {

    /**
     * {@code GET  /authenticate} : check if the authentication token correctly validates
     *
     * @return ok.
     */
    @GetMapping(value = "/authenticate", produces = MediaType.TEXT_PLAIN_VALUE)
    public String isAuthenticated() {
        return "ok";
    }
}
