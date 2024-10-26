package sn.sonatel.dsi.ins.imoc.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.InternshipStatus;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.InternshipType;

/**
 * A DTO for the {@link sn.sonatel.dsi.ins.imoc.domain.DemandeStage} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DemandeStageDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String reference;

    @NotNull(message = "must not be null")
    private LocalDate creationDate;

    @NotNull(message = "must not be null")
    private InternshipStatus status;

    @Lob
    private String description;

    @NotNull(message = "must not be null")
    private InternshipType internshipType;

    @NotNull(message = "must not be null")
    private LocalDate startDate;

    @NotNull(message = "must not be null")
    private LocalDate endDate;

    @Lob
    private byte[] resume;

    private String resumeContentType;

    @Lob
    private byte[] coverLetter;

    private String coverLetterContentType;

    private Boolean validated;

    private CandidatDTO candidat;

    private AssistantGWTEDTO assistantGWTE;

    private ManagerDTO manager;

    private DepartementDTO departement;

    private BusinessUnitDTO businessUnit;

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

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public InternshipStatus getStatus() {
        return status;
    }

    public void setStatus(InternshipStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public InternshipType getInternshipType() {
        return internshipType;
    }

    public void setInternshipType(InternshipType internshipType) {
        this.internshipType = internshipType;
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

    public byte[] getResume() {
        return resume;
    }

    public void setResume(byte[] resume) {
        this.resume = resume;
    }

    public String getResumeContentType() {
        return resumeContentType;
    }

    public void setResumeContentType(String resumeContentType) {
        this.resumeContentType = resumeContentType;
    }

    public byte[] getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(byte[] coverLetter) {
        this.coverLetter = coverLetter;
    }

    public String getCoverLetterContentType() {
        return coverLetterContentType;
    }

    public void setCoverLetterContentType(String coverLetterContentType) {
        this.coverLetterContentType = coverLetterContentType;
    }

    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public CandidatDTO getCandidat() {
        return candidat;
    }

    public void setCandidat(CandidatDTO candidat) {
        this.candidat = candidat;
    }

    public AssistantGWTEDTO getAssistantGWTE() {
        return assistantGWTE;
    }

    public void setAssistantGWTE(AssistantGWTEDTO assistantGWTE) {
        this.assistantGWTE = assistantGWTE;
    }

    public ManagerDTO getManager() {
        return manager;
    }

    public void setManager(ManagerDTO manager) {
        this.manager = manager;
    }

    public DepartementDTO getDepartement() {
        return departement;
    }

    public void setDepartement(DepartementDTO departement) {
        this.departement = departement;
    }

    public BusinessUnitDTO getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(BusinessUnitDTO businessUnit) {
        this.businessUnit = businessUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DemandeStageDTO)) {
            return false;
        }

        DemandeStageDTO demandeStageDTO = (DemandeStageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, demandeStageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DemandeStageDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", creationDate='" + getCreationDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", description='" + getDescription() + "'" +
            ", internshipType='" + getInternshipType() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", resume='" + getResume() + "'" +
            ", coverLetter='" + getCoverLetter() + "'" +
            ", validated='" + getValidated() + "'" +
            ", candidat=" + getCandidat() +
            ", assistantGWTE=" + getAssistantGWTE() +
            ", manager=" + getManager() +
            ", departement=" + getDepartement() +
            ", businessUnit=" + getBusinessUnit() +
            "}";
    }
}
