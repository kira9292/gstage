package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.InternshipStatus;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.InternshipType;

/**
 * A DemandeStage.
 */
@Table("demande_stage")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DemandeStage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("reference")
    private String reference;

    @NotNull(message = "must not be null")
    @Column("creation_date")
    private LocalDate creationDate;

    @NotNull(message = "must not be null")
    @Column("status")
    private InternshipStatus status;

    @Column("description")
    private String description;

    @NotNull(message = "must not be null")
    @Column("internship_type")
    private InternshipType internshipType;

    @NotNull(message = "must not be null")
    @Column("start_date")
    private LocalDate startDate;

    @NotNull(message = "must not be null")
    @Column("end_date")
    private LocalDate endDate;

    @Column("resume")
    private byte[] resume;

    @NotNull
    @Column("resume_content_type")
    private String resumeContentType;

    @Column("cover_letter")
    private byte[] coverLetter;

    @NotNull
    @Column("cover_letter_content_type")
    private String coverLetterContentType;

    @Column("validated")
    private Boolean validated;

    @Transient
    private Candidat candidat;

    @Transient
    @JsonIgnoreProperties(value = { "demandeStages", "contrats", "etatPaiements" }, allowSetters = true)
    private AssistantGWTE assistantGWTE;

    @Transient
    @JsonIgnoreProperties(value = { "service", "attestationPresences", "candidats", "demandeStages" }, allowSetters = true)
    private Manager manager;

    @Transient
    @JsonIgnoreProperties(value = { "services", "demandeStages" }, allowSetters = true)
    private Departement departement;

    @Transient
    @JsonIgnoreProperties(value = { "demandeStages" }, allowSetters = true)
    private BusinessUnit businessUnit;

    @Column("candidat_id")
    private Long candidatId;

    @Column("assistantgwte_id")
    private Long assistantGWTEId;

    @Column("manager_id")
    private Long managerId;

    @Column("departement_id")
    private Long departementId;

    @Column("business_unit_id")
    private Long businessUnitId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DemandeStage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public DemandeStage reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDate getCreationDate() {
        return this.creationDate;
    }

    public DemandeStage creationDate(LocalDate creationDate) {
        this.setCreationDate(creationDate);
        return this;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public InternshipStatus getStatus() {
        return this.status;
    }

    public DemandeStage status(InternshipStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(InternshipStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return this.description;
    }

    public DemandeStage description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public InternshipType getInternshipType() {
        return this.internshipType;
    }

    public DemandeStage internshipType(InternshipType internshipType) {
        this.setInternshipType(internshipType);
        return this;
    }

    public void setInternshipType(InternshipType internshipType) {
        this.internshipType = internshipType;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public DemandeStage startDate(LocalDate startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public DemandeStage endDate(LocalDate endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public byte[] getResume() {
        return this.resume;
    }

    public DemandeStage resume(byte[] resume) {
        this.setResume(resume);
        return this;
    }

    public void setResume(byte[] resume) {
        this.resume = resume;
    }

    public String getResumeContentType() {
        return this.resumeContentType;
    }

    public DemandeStage resumeContentType(String resumeContentType) {
        this.resumeContentType = resumeContentType;
        return this;
    }

    public void setResumeContentType(String resumeContentType) {
        this.resumeContentType = resumeContentType;
    }

    public byte[] getCoverLetter() {
        return this.coverLetter;
    }

    public DemandeStage coverLetter(byte[] coverLetter) {
        this.setCoverLetter(coverLetter);
        return this;
    }

    public void setCoverLetter(byte[] coverLetter) {
        this.coverLetter = coverLetter;
    }

    public String getCoverLetterContentType() {
        return this.coverLetterContentType;
    }

    public DemandeStage coverLetterContentType(String coverLetterContentType) {
        this.coverLetterContentType = coverLetterContentType;
        return this;
    }

    public void setCoverLetterContentType(String coverLetterContentType) {
        this.coverLetterContentType = coverLetterContentType;
    }

    public Boolean getValidated() {
        return this.validated;
    }

    public DemandeStage validated(Boolean validated) {
        this.setValidated(validated);
        return this;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public Candidat getCandidat() {
        return this.candidat;
    }

    public void setCandidat(Candidat candidat) {
        this.candidat = candidat;
        this.candidatId = candidat != null ? candidat.getId() : null;
    }

    public DemandeStage candidat(Candidat candidat) {
        this.setCandidat(candidat);
        return this;
    }

    public AssistantGWTE getAssistantGWTE() {
        return this.assistantGWTE;
    }

    public void setAssistantGWTE(AssistantGWTE assistantGWTE) {
        this.assistantGWTE = assistantGWTE;
        this.assistantGWTEId = assistantGWTE != null ? assistantGWTE.getId() : null;
    }

    public DemandeStage assistantGWTE(AssistantGWTE assistantGWTE) {
        this.setAssistantGWTE(assistantGWTE);
        return this;
    }

    public Manager getManager() {
        return this.manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
        this.managerId = manager != null ? manager.getId() : null;
    }

    public DemandeStage manager(Manager manager) {
        this.setManager(manager);
        return this;
    }

    public Departement getDepartement() {
        return this.departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
        this.departementId = departement != null ? departement.getId() : null;
    }

    public DemandeStage departement(Departement departement) {
        this.setDepartement(departement);
        return this;
    }

    public BusinessUnit getBusinessUnit() {
        return this.businessUnit;
    }

    public void setBusinessUnit(BusinessUnit businessUnit) {
        this.businessUnit = businessUnit;
        this.businessUnitId = businessUnit != null ? businessUnit.getId() : null;
    }

    public DemandeStage businessUnit(BusinessUnit businessUnit) {
        this.setBusinessUnit(businessUnit);
        return this;
    }

    public Long getCandidatId() {
        return this.candidatId;
    }

    public void setCandidatId(Long candidat) {
        this.candidatId = candidat;
    }

    public Long getAssistantGWTEId() {
        return this.assistantGWTEId;
    }

    public void setAssistantGWTEId(Long assistantGWTE) {
        this.assistantGWTEId = assistantGWTE;
    }

    public Long getManagerId() {
        return this.managerId;
    }

    public void setManagerId(Long manager) {
        this.managerId = manager;
    }

    public Long getDepartementId() {
        return this.departementId;
    }

    public void setDepartementId(Long departement) {
        this.departementId = departement;
    }

    public Long getBusinessUnitId() {
        return this.businessUnitId;
    }

    public void setBusinessUnitId(Long businessUnit) {
        this.businessUnitId = businessUnit;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DemandeStage)) {
            return false;
        }
        return getId() != null && getId().equals(((DemandeStage) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DemandeStage{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", creationDate='" + getCreationDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", description='" + getDescription() + "'" +
            ", internshipType='" + getInternshipType() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", resume='" + getResume() + "'" +
            ", resumeContentType='" + getResumeContentType() + "'" +
            ", coverLetter='" + getCoverLetter() + "'" +
            ", coverLetterContentType='" + getCoverLetterContentType() + "'" +
            ", validated='" + getValidated() + "'" +
            "}";
    }
}
