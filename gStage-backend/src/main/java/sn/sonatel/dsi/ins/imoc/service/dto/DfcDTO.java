package sn.sonatel.dsi.ins.imoc.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link sn.sonatel.dsi.ins.imoc.domain.Dfc} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DfcDTO implements Serializable {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DfcDTO)) {
            return false;
        }

        DfcDTO dfcDTO = (DfcDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, dfcDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DfcDTO{" +
            "id=" + getId() +
            "}";
    }
}
