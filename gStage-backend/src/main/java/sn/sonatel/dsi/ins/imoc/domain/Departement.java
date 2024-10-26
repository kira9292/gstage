package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Departement.
 */
@Table("departement")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Departement implements Serializable {

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
    @JsonIgnoreProperties(value = { "businessUnit", "manager", "departemen" }, allowSetters = true)
    private Set<AppService> services = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "candidat", "assistantGWTE", "manager", "departement", "businessUnit" }, allowSetters = true)
    private Set<DemandeStage> demandeStages = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Departement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Departement name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Departement description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<AppService> getServices() {
        return this.services;
    }

    public void setServices(Set<AppService> appServices) {
        if (this.services != null) {
            this.services.forEach(i -> i.setDepartemen(null));
        }
        if (appServices != null) {
            appServices.forEach(i -> i.setDepartemen(this));
        }
        this.services = appServices;
    }

    public Departement services(Set<AppService> appServices) {
        this.setServices(appServices);
        return this;
    }

    public Departement addServices(AppService appService) {
        this.services.add(appService);
        appService.setDepartemen(this);
        return this;
    }

    public Departement removeServices(AppService appService) {
        this.services.remove(appService);
        appService.setDepartemen(null);
        return this;
    }

    public Set<DemandeStage> getDemandeStages() {
        return this.demandeStages;
    }

    public void setDemandeStages(Set<DemandeStage> demandeStages) {
        if (this.demandeStages != null) {
            this.demandeStages.forEach(i -> i.setDepartement(null));
        }
        if (demandeStages != null) {
            demandeStages.forEach(i -> i.setDepartement(this));
        }
        this.demandeStages = demandeStages;
    }

    public Departement demandeStages(Set<DemandeStage> demandeStages) {
        this.setDemandeStages(demandeStages);
        return this;
    }

    public Departement addDemandeStages(DemandeStage demandeStage) {
        this.demandeStages.add(demandeStage);
        demandeStage.setDepartement(this);
        return this;
    }

    public Departement removeDemandeStages(DemandeStage demandeStage) {
        this.demandeStages.remove(demandeStage);
        demandeStage.setDepartement(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Departement)) {
            return false;
        }
        return getId() != null && getId().equals(((Departement) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Departement{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
