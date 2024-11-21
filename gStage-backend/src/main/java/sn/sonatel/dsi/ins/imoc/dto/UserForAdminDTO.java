package sn.sonatel.dsi.ins.imoc.dto;

public record UserForAdminDTO(
    Long id,
    String username,
    String email,
    String name,
    String firstName,
    String phone,
    String roleName,
    String serviceName
) {

}
