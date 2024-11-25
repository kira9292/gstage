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
    @Override
    public Long id() {
        return id;
    }

    @Override
    public String username() {
        return username;
    }

    @Override
    public String email() {
        return email;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String firstName() {
        return firstName;
    }

    @Override
    public String phone() {
        return phone;
    }

    @Override
    public String roleName() {
        return roleName;
    }

    @Override
    public String serviceName() {
        return serviceName;
    }
}
