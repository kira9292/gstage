package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.ValidationStatus;

/**
 * A Validation.
 */
@Entity
@Table(name = "validation")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Validation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "reference", nullable = false, unique = true)
    private String reference;

    @NotNull
    @Column(name = "validation_date", nullable = false)
    private LocalDate validationDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ValidationStatus status;

    @Column(name = "comments")
    private String comments;

    @NotNull
    @Column(name = "validated_by", nullable = false)
    private String validatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "validations", "contrat", "appUser", "etatPaiement" }, allowSetters = true)
    private AttestationPresence attestationPresence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "attestationFinStage", "validations", "appUser", "candidat" }, allowSetters = true)
    private Contrat contrat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "validations", "contrat" }, allowSetters = true)
    private AttestationFinStage attestationFinStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "service", "etatPaiements", "contrats", "demandeStages", "attestationPresences", "candidats", "validations", "roles" },
        allowSetters = true
    )
    private AppUser user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Validation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public Validation reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDate getValidationDate() {
        return this.validationDate;
    }

    public Validation validationDate(LocalDate validationDate) {
        this.setValidationDate(validationDate);
        return this;
    }

    public void setValidationDate(LocalDate validationDate) {
        this.validationDate = validationDate;
    }

    public ValidationStatus getStatus() {
        return this.status;
    }

    public Validation status(ValidationStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ValidationStatus status) {
        this.status = status;
    }

    public String getComments() {
        return this.comments;
    }

    public Validation comments(String comments) {
        this.setComments(comments);
        return this;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getValidatedBy() {
        return this.validatedBy;
    }

    public Validation validatedBy(String validatedBy) {
        this.setValidatedBy(validatedBy);
        return this;
    }

    public void setValidatedBy(String validatedBy) {
        this.validatedBy = validatedBy;
    }

    public AttestationPresence getAttestationPresence() {
        return this.attestationPresence;
    }

    public void setAttestationPresence(AttestationPresence attestationPresence) {
        this.attestationPresence = attestationPresence;
    }

    public Validation attestationPresence(AttestationPresence attestationPresence) {
        this.setAttestationPresence(attestationPresence);
        return this;
    }

    public Contrat getContrat() {
        return this.contrat;
    }

    public void setContrat(Contrat contrat) {
        this.contrat = contrat;
    }

    public Validation contrat(Contrat contrat) {
        this.setContrat(contrat);
        return this;
    }

    public AttestationFinStage getAttestationFinStage() {
        return this.attestationFinStage;
    }

    public void setAttestationFinStage(AttestationFinStage attestationFinStage) {
        this.attestationFinStage = attestationFinStage;
    }

    public Validation attestationFinStage(AttestationFinStage attestationFinStage) {
        this.setAttestationFinStage(attestationFinStage);
        return this;
    }

    public AppUser getUser() {
        return this.user;
    }

    public void setUser(AppUser appUser) {
        this.user = appUser;
    }

    public Validation user(AppUser appUser) {
        this.setUser(appUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Validation)) {
            return false;
        }
        return getId() != null && getId().equals(((Validation) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Validation{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", validationDate='" + getValidationDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", comments='" + getComments() + "'" +
            ", validatedBy='" + getValidatedBy() + "'" +
            "}";
    }
}
