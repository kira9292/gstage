package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.ContractStatus;

/**
 * A Contrat.
 */
@Entity
@Table(name = "contrat")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Contrat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "reference", nullable = false, unique = true)
    private String reference;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "compensation", nullable = false)
    private Double compensation;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContractStatus status;

    @NotNull
    @Column(name = "assignment_site", nullable = false)
    private String assignmentSite;

    @Column(name = "signature_date")
    private LocalDate signatureDate;

    @Column(name = "comments")
    private String comments;

    @JsonIgnoreProperties(value = { "validations", "contrat" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private AttestationFinStage attestationFinStage;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "contrat")
    @JsonIgnoreProperties(value = { "attestationPresence", "contrat", "attestationFinStage", "user" }, allowSetters = true)
    private Set<Validation> validations = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "service", "etatPaiements", "contrats", "demandeStages", "attestationPresences", "candidats", "validations", "roles" },
        allowSetters = true
    )
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "contrats", "demandeStage", "appUser" }, allowSetters = true)
    private Candidat candidat;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Contrat id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public Contrat reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public Contrat startDate(LocalDate startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public Contrat endDate(LocalDate endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Double getCompensation() {
        return this.compensation;
    }

    public Contrat compensation(Double compensation) {
        this.setCompensation(compensation);
        return this;
    }

    public void setCompensation(Double compensation) {
        this.compensation = compensation;
    }

    public ContractStatus getStatus() {
        return this.status;
    }

    public Contrat status(ContractStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ContractStatus status) {
        this.status = status;
    }

    public String getAssignmentSite() {
        return this.assignmentSite;
    }

    public Contrat assignmentSite(String assignmentSite) {
        this.setAssignmentSite(assignmentSite);
        return this;
    }

    public void setAssignmentSite(String assignmentSite) {
        this.assignmentSite = assignmentSite;
    }

    public LocalDate getSignatureDate() {
        return this.signatureDate;
    }

    public Contrat signatureDate(LocalDate signatureDate) {
        this.setSignatureDate(signatureDate);
        return this;
    }

    public void setSignatureDate(LocalDate signatureDate) {
        this.signatureDate = signatureDate;
    }

    public String getComments() {
        return this.comments;
    }

    public Contrat comments(String comments) {
        this.setComments(comments);
        return this;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public AttestationFinStage getAttestationFinStage() {
        return this.attestationFinStage;
    }

    public void setAttestationFinStage(AttestationFinStage attestationFinStage) {
        this.attestationFinStage = attestationFinStage;
    }

    public Contrat attestationFinStage(AttestationFinStage attestationFinStage) {
        this.setAttestationFinStage(attestationFinStage);
        return this;
    }

    public Set<Validation> getValidations() {
        return this.validations;
    }

    public void setValidations(Set<Validation> validations) {
        if (this.validations != null) {
            this.validations.forEach(i -> i.setContrat(null));
        }
        if (validations != null) {
            validations.forEach(i -> i.setContrat(this));
        }
        this.validations = validations;
    }

    public Contrat validations(Set<Validation> validations) {
        this.setValidations(validations);
        return this;
    }

    public Contrat addValidations(Validation validation) {
        this.validations.add(validation);
        validation.setContrat(this);
        return this;
    }

    public Contrat removeValidations(Validation validation) {
        this.validations.remove(validation);
        validation.setContrat(null);
        return this;
    }

    public AppUser getAppUser() {
        return this.appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public Contrat appUser(AppUser appUser) {
        this.setAppUser(appUser);
        return this;
    }

    public Candidat getCandidat() {
        return this.candidat;
    }

    public void setCandidat(Candidat candidat) {
        this.candidat = candidat;
    }

    public Contrat candidat(Candidat candidat) {
        this.setCandidat(candidat);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Contrat)) {
            return false;
        }
        return getId() != null && getId().equals(((Contrat) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Contrat{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", compensation=" + getCompensation() +
            ", status='" + getStatus() + "'" +
            ", assignmentSite='" + getAssignmentSite() + "'" +
            ", signatureDate='" + getSignatureDate() + "'" +
            ", comments='" + getComments() + "'" +
            "}";
    }
}
