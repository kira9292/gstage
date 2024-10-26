package sn.sonatel.dsi.ins.imoc.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link sn.sonatel.dsi.ins.imoc.domain.AppService} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AppServiceDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 2)
    private String name;

    private String description;

    private BusinessUnitDTO businessUnit;

    private DepartementDTO departemen;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BusinessUnitDTO getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(BusinessUnitDTO businessUnit) {
        this.businessUnit = businessUnit;
    }

    public DepartementDTO getDepartemen() {
        return departemen;
    }

    public void setDepartemen(DepartementDTO departemen) {
        this.departemen = departemen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppServiceDTO)) {
            return false;
        }

        AppServiceDTO appServiceDTO = (AppServiceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, appServiceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppServiceDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", businessUnit=" + getBusinessUnit() +
            ", departemen=" + getDepartemen() +
            "}";
    }
}
