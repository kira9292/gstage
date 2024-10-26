package sn.sonatel.dsi.ins.imoc.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.ValidationStatus;

/**
 * A DTO for the {@link sn.sonatel.dsi.ins.imoc.domain.Validation} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ValidationDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String reference;

    @NotNull(message = "must not be null")
    private LocalDate validationDate;

    @NotNull(message = "must not be null")
    private ValidationStatus status;

    private String comments;

    @NotNull(message = "must not be null")
    private String validatedBy;

    private AttestationPresenceDTO attestationPresence;

    private ContratDTO contrat;

    private AttestationFinStageDTO attestationFinStage;

    private AppUserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDate getValidationDate() {
        return validationDate;
    }

    public void setValidationDate(LocalDate validationDate) {
        this.validationDate = validationDate;
    }

    public ValidationStatus getStatus() {
        return status;
    }

    public void setStatus(ValidationStatus status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getValidatedBy() {
        return validatedBy;
    }

    public void setValidatedBy(String validatedBy) {
        this.validatedBy = validatedBy;
    }

    public AttestationPresenceDTO getAttestationPresence() {
        return attestationPresence;
    }

    public void setAttestationPresence(AttestationPresenceDTO attestationPresence) {
        this.attestationPresence = attestationPresence;
    }

    public ContratDTO getContrat() {
        return contrat;
    }

    public void setContrat(ContratDTO contrat) {
        this.contrat = contrat;
    }

    public AttestationFinStageDTO getAttestationFinStage() {
        return attestationFinStage;
    }

    public void setAttestationFinStage(AttestationFinStageDTO attestationFinStage) {
        this.attestationFinStage = attestationFinStage;
    }

    public AppUserDTO getUser() {
        return user;
    }

    public void setUser(AppUserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ValidationDTO)) {
            return false;
        }

        ValidationDTO validationDTO = (ValidationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, validationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ValidationDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", validationDate='" + getValidationDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", comments='" + getComments() + "'" +
            ", validatedBy='" + getValidatedBy() + "'" +
            ", attestationPresence=" + getAttestationPresence() +
            ", contrat=" + getContrat() +
            ", attestationFinStage=" + getAttestationFinStage() +
            ", user=" + getUser() +
            "}";
    }
}
