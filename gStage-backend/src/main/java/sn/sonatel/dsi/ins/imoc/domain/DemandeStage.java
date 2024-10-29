package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.InternshipStatus;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.InternshipType;

/**
 * A DemandeStage.
 */
@Entity
@Table(name = "demande_stage")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DemandeStage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "reference", nullable = false, unique = true)
    private String reference;

    @NotNull
    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InternshipStatus status;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "internship_type", nullable = false)
    private InternshipType internshipType;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Lob
    @Column(name = "resume", nullable = false)
    private byte[] resume;

    @NotNull
    @Column(name = "resume_content_type", nullable = false)
    private String resumeContentType;

    @Lob
    @Column(name = "cover_letter", nullable = false)
    private byte[] coverLetter;

    @NotNull
    @Column(name = "cover_letter_content_type", nullable = false)
    private String coverLetterContentType;

    @Column(name = "validated")
    private Boolean validated;

    @JsonIgnoreProperties(value = { "contrats", "demandeStage", "appUser" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Candidat candidat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "service", "etatPaiements", "contrats", "demandeStages", "attestationPresences", "candidats", "validations", "roles" },
        allowSetters = true
    )
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "services", "demandeStages" }, allowSetters = true)
    private Departement departement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "demandeStages" }, allowSetters = true)
    private BusinessUnit businessUnit;

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
    }

    public DemandeStage candidat(Candidat candidat) {
        this.setCandidat(candidat);
        return this;
    }

    public AppUser getAppUser() {
        return this.appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public DemandeStage appUser(AppUser appUser) {
        this.setAppUser(appUser);
        return this;
    }

    public Departement getDepartement() {
        return this.departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
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
    }

    public DemandeStage businessUnit(BusinessUnit businessUnit) {
        this.setBusinessUnit(businessUnit);
        return this;
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
