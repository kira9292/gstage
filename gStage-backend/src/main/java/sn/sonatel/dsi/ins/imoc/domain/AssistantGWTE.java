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
 * A AssistantGWTE.
 */
@Table("assistant_gwte")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AssistantGWTE implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Pattern(regexp = "^[0-9]{9}$")
    @Column("phone")
    private String phone;

    @Transient
    @JsonIgnoreProperties(value = { "candidat", "assistantGWTE", "manager", "departement", "businessUnit" }, allowSetters = true)
    private Set<DemandeStage> demandeStages = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "attestationFinStage", "validations", "drh", "assistantGWTECreator", "candidat" }, allowSetters = true)
    private Set<Contrat> contrats = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "attestationPresences", "contrat", "dfc", "assistantGWTECreator" }, allowSetters = true)
    private Set<EtatPaiement> etatPaiements = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AssistantGWTE id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return this.phone;
    }

    public AssistantGWTE phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<DemandeStage> getDemandeStages() {
        return this.demandeStages;
    }

    public void setDemandeStages(Set<DemandeStage> demandeStages) {
        if (this.demandeStages != null) {
            this.demandeStages.forEach(i -> i.setAssistantGWTE(null));
        }
        if (demandeStages != null) {
            demandeStages.forEach(i -> i.setAssistantGWTE(this));
        }
        this.demandeStages = demandeStages;
    }

    public AssistantGWTE demandeStages(Set<DemandeStage> demandeStages) {
        this.setDemandeStages(demandeStages);
        return this;
    }

    public AssistantGWTE addDemandeStages(DemandeStage demandeStage) {
        this.demandeStages.add(demandeStage);
        demandeStage.setAssistantGWTE(this);
        return this;
    }

    public AssistantGWTE removeDemandeStages(DemandeStage demandeStage) {
        this.demandeStages.remove(demandeStage);
        demandeStage.setAssistantGWTE(null);
        return this;
    }

    public Set<Contrat> getContrats() {
        return this.contrats;
    }

    public void setContrats(Set<Contrat> contrats) {
        if (this.contrats != null) {
            this.contrats.forEach(i -> i.setAssistantGWTECreator(null));
        }
        if (contrats != null) {
            contrats.forEach(i -> i.setAssistantGWTECreator(this));
        }
        this.contrats = contrats;
    }

    public AssistantGWTE contrats(Set<Contrat> contrats) {
        this.setContrats(contrats);
        return this;
    }

    public AssistantGWTE addContrats(Contrat contrat) {
        this.contrats.add(contrat);
        contrat.setAssistantGWTECreator(this);
        return this;
    }

    public AssistantGWTE removeContrats(Contrat contrat) {
        this.contrats.remove(contrat);
        contrat.setAssistantGWTECreator(null);
        return this;
    }

    public Set<EtatPaiement> getEtatPaiements() {
        return this.etatPaiements;
    }

    public void setEtatPaiements(Set<EtatPaiement> etatPaiements) {
        if (this.etatPaiements != null) {
            this.etatPaiements.forEach(i -> i.setAssistantGWTECreator(null));
        }
        if (etatPaiements != null) {
            etatPaiements.forEach(i -> i.setAssistantGWTECreator(this));
        }
        this.etatPaiements = etatPaiements;
    }

    public AssistantGWTE etatPaiements(Set<EtatPaiement> etatPaiements) {
        this.setEtatPaiements(etatPaiements);
        return this;
    }

    public AssistantGWTE addEtatPaiements(EtatPaiement etatPaiement) {
        this.etatPaiements.add(etatPaiement);
        etatPaiement.setAssistantGWTECreator(this);
        return this;
    }

    public AssistantGWTE removeEtatPaiements(EtatPaiement etatPaiement) {
        this.etatPaiements.remove(etatPaiement);
        etatPaiement.setAssistantGWTECreator(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AssistantGWTE)) {
            return false;
        }
        return getId() != null && getId().equals(((AssistantGWTE) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssistantGWTE{" +
            "id=" + getId() +
            ", phone='" + getPhone() + "'" +
            "}";
    }
}
