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
 * A Manager.
 */
@Table("manager")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Manager implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Pattern(regexp = "^[0-9]{9}$")
    @Column("phone")
    private String phone;

    @Transient
    private AppService service;

    @Transient
    @JsonIgnoreProperties(value = { "validations", "contrat", "manager", "etatPaiement" }, allowSetters = true)
    private Set<AttestationPresence> attestationPresences = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "contrats", "demandeStage", "manager" }, allowSetters = true)
    private Set<Candidat> candidats = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "candidat", "assistantGWTE", "manager", "departement", "businessUnit" }, allowSetters = true)
    private Set<DemandeStage> demandeStages = new HashSet<>();

    @Column("service_id")
    private Long serviceId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Manager id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return this.phone;
    }

    public Manager phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public AppService getService() {
        return this.service;
    }

    public void setService(AppService appService) {
        this.service = appService;
        this.serviceId = appService != null ? appService.getId() : null;
    }

    public Manager service(AppService appService) {
        this.setService(appService);
        return this;
    }

    public Set<AttestationPresence> getAttestationPresences() {
        return this.attestationPresences;
    }

    public void setAttestationPresences(Set<AttestationPresence> attestationPresences) {
        if (this.attestationPresences != null) {
            this.attestationPresences.forEach(i -> i.setManager(null));
        }
        if (attestationPresences != null) {
            attestationPresences.forEach(i -> i.setManager(this));
        }
        this.attestationPresences = attestationPresences;
    }

    public Manager attestationPresences(Set<AttestationPresence> attestationPresences) {
        this.setAttestationPresences(attestationPresences);
        return this;
    }

    public Manager addAttestationPresences(AttestationPresence attestationPresence) {
        this.attestationPresences.add(attestationPresence);
        attestationPresence.setManager(this);
        return this;
    }

    public Manager removeAttestationPresences(AttestationPresence attestationPresence) {
        this.attestationPresences.remove(attestationPresence);
        attestationPresence.setManager(null);
        return this;
    }

    public Set<Candidat> getCandidats() {
        return this.candidats;
    }

    public void setCandidats(Set<Candidat> candidats) {
        if (this.candidats != null) {
            this.candidats.forEach(i -> i.setManager(null));
        }
        if (candidats != null) {
            candidats.forEach(i -> i.setManager(this));
        }
        this.candidats = candidats;
    }

    public Manager candidats(Set<Candidat> candidats) {
        this.setCandidats(candidats);
        return this;
    }

    public Manager addCandidats(Candidat candidat) {
        this.candidats.add(candidat);
        candidat.setManager(this);
        return this;
    }

    public Manager removeCandidats(Candidat candidat) {
        this.candidats.remove(candidat);
        candidat.setManager(null);
        return this;
    }

    public Set<DemandeStage> getDemandeStages() {
        return this.demandeStages;
    }

    public void setDemandeStages(Set<DemandeStage> demandeStages) {
        if (this.demandeStages != null) {
            this.demandeStages.forEach(i -> i.setManager(null));
        }
        if (demandeStages != null) {
            demandeStages.forEach(i -> i.setManager(this));
        }
        this.demandeStages = demandeStages;
    }

    public Manager demandeStages(Set<DemandeStage> demandeStages) {
        this.setDemandeStages(demandeStages);
        return this;
    }

    public Manager addDemandeStages(DemandeStage demandeStage) {
        this.demandeStages.add(demandeStage);
        demandeStage.setManager(this);
        return this;
    }

    public Manager removeDemandeStages(DemandeStage demandeStage) {
        this.demandeStages.remove(demandeStage);
        demandeStage.setManager(null);
        return this;
    }

    public Long getServiceId() {
        return this.serviceId;
    }

    public void setServiceId(Long appService) {
        this.serviceId = appService;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Manager)) {
            return false;
        }
        return getId() != null && getId().equals(((Manager) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Manager{" +
            "id=" + getId() +
            ", phone='" + getPhone() + "'" +
            "}";
    }
}
