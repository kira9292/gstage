package sn.sonatel.dsi.ins.imoc.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.ContractStatus;

/**
 * A DTO for the {@link sn.sonatel.dsi.ins.imoc.domain.Contrat} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ContratDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String reference;

    @NotNull(message = "must not be null")
    private LocalDate startDate;

    @NotNull(message = "must not be null")
    private LocalDate endDate;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0")
    private Double compensation;

    @NotNull(message = "must not be null")
    private ContractStatus status;

    @NotNull(message = "must not be null")
    private String assignmentSite;

    private LocalDate signatureDate;

    private String comments;

    private AttestationFinStageDTO attestationFinStage;

    private DrhDTO drh;

    private AssistantGWTEDTO assistantGWTECreator;

    private CandidatDTO candidat;

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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Double getCompensation() {
        return compensation;
    }

    public void setCompensation(Double compensation) {
        this.compensation = compensation;
    }

    public ContractStatus getStatus() {
        return status;
    }

    public void setStatus(ContractStatus status) {
        this.status = status;
    }

    public String getAssignmentSite() {
        return assignmentSite;
    }

    public void setAssignmentSite(String assignmentSite) {
        this.assignmentSite = assignmentSite;
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

    public AttestationFinStageDTO getAttestationFinStage() {
        return attestationFinStage;
    }

    public void setAttestationFinStage(AttestationFinStageDTO attestationFinStage) {
        this.attestationFinStage = attestationFinStage;
    }

    public DrhDTO getDrh() {
        return drh;
    }

    public void setDrh(DrhDTO drh) {
        this.drh = drh;
    }

    public AssistantGWTEDTO getAssistantGWTECreator() {
        return assistantGWTECreator;
    }

    public void setAssistantGWTECreator(AssistantGWTEDTO assistantGWTECreator) {
        this.assistantGWTECreator = assistantGWTECreator;
    }

    public CandidatDTO getCandidat() {
        return candidat;
    }

    public void setCandidat(CandidatDTO candidat) {
        this.candidat = candidat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContratDTO)) {
            return false;
        }

        ContratDTO contratDTO = (ContratDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, contratDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ContratDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", compensation=" + getCompensation() +
            ", status='" + getStatus() + "'" +
            ", assignmentSite='" + getAssignmentSite() + "'" +
            ", signatureDate='" + getSignatureDate() + "'" +
            ", comments='" + getComments() + "'" +
            ", attestationFinStage=" + getAttestationFinStage() +
            ", drh=" + getDrh() +
            ", assistantGWTECreator=" + getAssistantGWTECreator() +
            ", candidat=" + getCandidat() +
            "}";
    }
}
