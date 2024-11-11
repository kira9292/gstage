package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
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

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "internship_type")
    private InternshipType internshipType;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Lob
    @Column(name = "cv")
    private byte[] cv;

    @Column(name = "cv_content_type")
    private String cvContentType;

    @Lob
    @Column(name = "cover_letter")
    private byte[] coverLetter;

    @Column(name = "cover_letter_content_type")
    private String coverLetterContentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InternshipStatus status;

    @Column(name = "validated")
    private Boolean validated;

    @JsonIgnoreProperties(value = { "demandeStage", "validationStatuscandidat", "appUser" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Candidat candidat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = {
            "service",
            "attestationFinStage",
            "etatPaiements",
            "contrats",
            "demandeStages",
            "attestationPresences",
            "candidats",
            "validations",
            "role",
            "validationStatusUser",
            "restaurationStagiaires",
            "jwts",
        },
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

    public byte[] getCv() {
        return this.cv;
    }

    public DemandeStage cv(byte[] cv) {
        this.setCv(cv);
        return this;
    }

    public void setCv(byte[] cv) {
        this.cv = cv;
    }

    public String getCvContentType() {
        return this.cvContentType;
    }

    public DemandeStage cvContentType(String cvContentType) {
        this.cvContentType = cvContentType;
        return this;
    }

    public void setCvContentType(String cvContentType) {
        this.cvContentType = cvContentType;
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
            ", creationDate='" + getCreationDate() + "'" +
            ", internshipType='" + getInternshipType() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", cv='" + getCv() + "'" +
            ", cvContentType='" + getCvContentType() + "'" +
            ", coverLetter='" + getCoverLetter() + "'" +
            ", coverLetterContentType='" + getCoverLetterContentType() + "'" +
            ", status='" + getStatus() + "'" +
            ", validated='" + getValidated() + "'" +
            "}";
    }
}
