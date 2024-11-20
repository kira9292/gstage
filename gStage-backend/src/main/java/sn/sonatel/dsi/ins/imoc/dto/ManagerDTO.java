package sn.sonatel.dsi.ins.imoc.dto;

public record ManagerDTO(String name,String lastname, String email, String serviceName) {
    @Override
    public String serviceName() {
        return serviceName;
    }

    @Override
    public String lastname() {
        return lastname;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String email() {
        return email;
    }
}
