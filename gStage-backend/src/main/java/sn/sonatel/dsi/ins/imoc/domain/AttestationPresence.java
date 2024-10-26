package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A AttestationPresence.
 */
@Table("attestation_presence")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AttestationPresence implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("reference")
    private String reference;

    @NotNull(message = "must not be null")
    @Column("start_date")
    private LocalDate startDate;

    @NotNull(message = "must not be null")
    @Column("end_date")
    private LocalDate endDate;

    @NotNull(message = "must not be null")
    @Column("signature_date")
    private LocalDate signatureDate;

    @NotNull(message = "must not be null")
    @Column("status")
    private Boolean status;

    @Column("comments")
    private String comments;

    @Transient
    @JsonIgnoreProperties(value = { "attestationPresence", "contrat", "attestationFinStage", "user" }, allowSetters = true)
    private Set<Validation> validations = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "attestationFinStage", "validations", "drh", "assistantGWTECreator", "candidat" }, allowSetters = true)
    private Contrat contrat;

    @Transient
    @JsonIgnoreProperties(value = { "service", "attestationPresences", "candidats", "demandeStages" }, allowSetters = true)
    private Manager manager;

    @Transient
    @JsonIgnoreProperties(value = { "attestationPresences", "contrat", "dfc", "assistantGWTECreator" }, allowSetters = true)
    private EtatPaiement etatPaiement;

    @Column("contrat_id")
    private Long contratId;

    @Column("manager_id")
    private Long managerId;

    @Column("etat_paiement_id")
    private Long etatPaiementId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AttestationPresence id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public AttestationPresence reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public AttestationPresence startDate(LocalDate startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public AttestationPresence endDate(LocalDate endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getSignatureDate() {
        return this.signatureDate;
    }

    public AttestationPresence signatureDate(LocalDate signatureDate) {
        this.setSignatureDate(signatureDate);
        return this;
    }

    public void setSignatureDate(LocalDate signatureDate) {
        this.signatureDate = signatureDate;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public AttestationPresence status(Boolean status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getComments() {
        return this.comments;
    }

    public AttestationPresence comments(String comments) {
        this.setComments(comments);
        return this;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Set<Validation> getValidations() {
        return this.validations;
    }

    public void setValidations(Set<Validation> validations) {
        if (this.validations != null) {
            this.validations.forEach(i -> i.setAttestationPresence(null));
        }
        if (validations != null) {
            validations.forEach(i -> i.setAttestationPresence(this));
        }
        this.validations = validations;
    }

    public AttestationPresence validations(Set<Validation> validations) {
        this.setValidations(validations);
        return this;
    }

    public AttestationPresence addValidations(Validation validation) {
        this.validations.add(validation);
        validation.setAttestationPresence(this);
        return this;
    }

    public AttestationPresence removeValidations(Validation validation) {
        this.validations.remove(validation);
        validation.setAttestationPresence(null);
        return this;
    }

    public Contrat getContrat() {
        return this.contrat;
    }

    public void setContrat(Contrat contrat) {
        this.contrat = contrat;
        this.contratId = contrat != null ? contrat.getId() : null;
    }

    public AttestationPresence contrat(Contrat contrat) {
        this.setContrat(contrat);
        return this;
    }

    public Manager getManager() {
        return this.manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
        this.managerId = manager != null ? manager.getId() : null;
    }

    public AttestationPresence manager(Manager manager) {
        this.setManager(manager);
        return this;
    }

    public EtatPaiement getEtatPaiement() {
        return this.etatPaiement;
    }

    public void setEtatPaiement(EtatPaiement etatPaiement) {
        this.etatPaiement = etatPaiement;
        this.etatPaiementId = etatPaiement != null ? etatPaiement.getId() : null;
    }

    public AttestationPresence etatPaiement(EtatPaiement etatPaiement) {
        this.setEtatPaiement(etatPaiement);
        return this;
    }

    public Long getContratId() {
        return this.contratId;
    }

    public void setContratId(Long contrat) {
        this.contratId = contrat;
    }

    public Long getManagerId() {
        return this.managerId;
    }

    public void setManagerId(Long manager) {
        this.managerId = manager;
    }

    public Long getEtatPaiementId() {
        return this.etatPaiementId;
    }

    public void setEtatPaiementId(Long etatPaiement) {
        this.etatPaiementId = etatPaiement;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AttestationPresence)) {
            return false;
        }
        return getId() != null && getId().equals(((AttestationPresence) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AttestationPresence{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", signatureDate='" + getSignatureDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", comments='" + getComments() + "'" +
            "}";
    }
}
