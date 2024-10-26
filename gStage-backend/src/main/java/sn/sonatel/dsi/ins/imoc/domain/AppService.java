package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A AppService.
 */
@Table("app_service")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AppService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 2)
    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Transient
    @JsonIgnoreProperties(value = { "demandeStages" }, allowSetters = true)
    private BusinessUnit businessUnit;

    @Transient
    private Manager manager;

    @Transient
    @JsonIgnoreProperties(value = { "services", "demandeStages" }, allowSetters = true)
    private Departement departemen;

    @Column("business_unit_id")
    private Long businessUnitId;

    @Column("departemen_id")
    private Long departemenId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AppService id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public AppService name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public AppService description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BusinessUnit getBusinessUnit() {
        return this.businessUnit;
    }

    public void setBusinessUnit(BusinessUnit businessUnit) {
        this.businessUnit = businessUnit;
        this.businessUnitId = businessUnit != null ? businessUnit.getId() : null;
    }

    public AppService businessUnit(BusinessUnit businessUnit) {
        this.setBusinessUnit(businessUnit);
        return this;
    }

    public Manager getManager() {
        return this.manager;
    }

    public void setManager(Manager manager) {
        if (this.manager != null) {
            this.manager.setService(null);
        }
        if (manager != null) {
            manager.setService(this);
        }
        this.manager = manager;
    }

    public AppService manager(Manager manager) {
        this.setManager(manager);
        return this;
    }

    public Departement getDepartemen() {
        return this.departemen;
    }

    public void setDepartemen(Departement departement) {
        this.departemen = departement;
        this.departemenId = departement != null ? departement.getId() : null;
    }

    public AppService departemen(Departement departement) {
        this.setDepartemen(departement);
        return this;
    }

    public Long getBusinessUnitId() {
        return this.businessUnitId;
    }

    public void setBusinessUnitId(Long businessUnit) {
        this.businessUnitId = businessUnit;
    }

    public Long getDepartemenId() {
        return this.departemenId;
    }

    public void setDepartemenId(Long departement) {
        this.departemenId = departement;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppService)) {
            return false;
        }
        return getId() != null && getId().equals(((AppService) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppService{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
