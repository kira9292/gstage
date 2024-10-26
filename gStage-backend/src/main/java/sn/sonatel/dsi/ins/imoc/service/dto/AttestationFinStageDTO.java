package sn.sonatel.dsi.ins.imoc.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link sn.sonatel.dsi.ins.imoc.domain.AttestationFinStage} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AttestationFinStageDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String reference;

    @NotNull(message = "must not be null")
    private LocalDate issueDate;

    private LocalDate signatureDate;

    private String comments;

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

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getSignatureDate() {
        return signatureDate;
    }

    public void setSignatureDate(LocalDate signatureDate) {
        this.signatureDate = signatureDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AttestationFinStageDTO)) {
            return false;
        }

        AttestationFinStageDTO attestationFinStageDTO = (AttestationFinStageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, attestationFinStageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AttestationFinStageDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", issueDate='" + getIssueDate() + "'" +
            ", signatureDate='" + getSignatureDate() + "'" +
            ", comments='" + getComments() + "'" +
            "}";
    }
}
