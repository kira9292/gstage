package sn.sonatel.dsi.ins.imoc.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link sn.sonatel.dsi.ins.imoc.domain.Drh} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DrhDTO implements Serializable {

    private Long id;

    @Pattern(regexp = "^[0-9]{9}$")
    private String phone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DrhDTO)) {
            return false;
        }

        DrhDTO drhDTO = (DrhDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, drhDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DrhDTO{" +
            "id=" + getId() +
            ", phone='" + getPhone() + "'" +
            "}";
    }
}
