package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.ValidationStatus;

/**
 * A Validation.
 */
@Table("validation")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Validation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("reference")
    private String reference;

    @NotNull(message = "must not be null")
    @Column("validation_date")
    private LocalDate validationDate;

    @NotNull(message = "must not be null")
    @Column("status")
    private ValidationStatus status;

    @Column("comments")
    private String comments;

    @NotNull(message = "must not be null")
    @Column("validated_by")
    private String validatedBy;

    @Transient
    @JsonIgnoreProperties(value = { "validations", "contrat", "manager", "etatPaiement" }, allowSetters = true)
    private AttestationPresence attestationPresence;

    @Transient
    @JsonIgnoreProperties(value = { "attestationFinStage", "validations", "drh", "assistantGWTECreator", "candidat" }, allowSetters = true)
    private Contrat contrat;

    @Transient
    @JsonIgnoreProperties(value = { "validations", "contrat" }, allowSetters = true)
    private AttestationFinStage attestationFinStage;

    @Transient
    @JsonIgnoreProperties(value = { "validations" }, allowSetters = true)
    private AppUser user;

    @Column("attestation_presence_id")
    private Long attestationPresenceId;

    @Column("contrat_id")
    private Long contratId;

    @Column("attestation_fin_stage_id")
    private Long attestationFinStageId;

    @Column("user_id")
    private Long userId;

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
        this.attestationPresenceId = attestationPresence != null ? attestationPresence.getId() : null;
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
        this.contratId = contrat != null ? contrat.getId() : null;
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
        this.attestationFinStageId = attestationFinStage != null ? attestationFinStage.getId() : null;
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
        this.userId = appUser != null ? appUser.getId() : null;
    }

    public Validation user(AppUser appUser) {
        this.setUser(appUser);
        return this;
    }

    public Long getAttestationPresenceId() {
        return this.attestationPresenceId;
    }

    public void setAttestationPresenceId(Long attestationPresence) {
        this.attestationPresenceId = attestationPresence;
    }

    public Long getContratId() {
        return this.contratId;
    }

    public void setContratId(Long contrat) {
        this.contratId = contrat;
    }

    public Long getAttestationFinStageId() {
        return this.attestationFinStageId;
    }

    public void setAttestationFinStageId(Long attestationFinStage) {
        this.attestationFinStageId = attestationFinStage;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long appUser) {
        this.userId = appUser;
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
