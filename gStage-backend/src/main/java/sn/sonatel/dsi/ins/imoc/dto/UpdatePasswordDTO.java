package sn.sonatel.dsi.ins.imoc.dto;

public record UpdatePasswordDTO(String email, String password) {
    @Override
    public String email() {
        return email;
    }

    @Override
    public String password() {
        return password;
    }
}
