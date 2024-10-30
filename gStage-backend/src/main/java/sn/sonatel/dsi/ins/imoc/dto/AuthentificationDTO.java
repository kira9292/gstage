package sn.sonatel.dsi.ins.imoc.dto;

import org.springframework.security.core.Authentication;

public record AuthentificationDTO(String username, String password)  {
}
