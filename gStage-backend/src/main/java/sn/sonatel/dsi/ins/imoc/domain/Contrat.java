package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.ContractStatus;

/**
 * A Contrat.
 */
@Table("contrat")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Contrat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("reference")
    private String reference;

    @NotNull(message = "must not be null")
    @Column("start_date")
    private LocalDate startDate;

    @NotNull(message = "must not be null")
    @Column("end_date")
    private LocalDate endDate;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0")
    @Column("compensation")
    private Double compensation;

    @NotNull(message = "must not be null")
    @Column("status")
    private ContractStatus status;

    @NotNull(message = "must not be null")
    @Column("assignment_site")
    private String assignmentSite;

    @Column("signature_date")
    private LocalDate signatureDate;

    @Column("comments")
    private String comments;

    @Transient
    private AttestationFinStage attestationFinStage;

    @Transient
    @JsonIgnoreProperties(value = { "attestationPresence", "contrat", "attestationFinStage", "user" }, allowSetters = true)
    private Set<Validation> validations = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "contrats" }, allowSetters = true)
    private Drh drh;

    @Transient
    @JsonIgnoreProperties(value = { "demandeStages", "contrats", "etatPaiements" }, allowSetters = true)
    private AssistantGWTE assistantGWTECreator;

    @Transient
    @JsonIgnoreProperties(value = { "contrats", "demandeStage", "manager" }, allowSetters = true)
    private Candidat candidat;

    @Column("attestation_fin_stage_id")
    private Long attestationFinStageId;

    @Column("drh_id")
    private Long drhId;

    @Column("assistantgwtecreator_id")
    private Long assistantGWTECreatorId;

    @Column("candidat_id")
    private Long candidatId;

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
        this.attestationFinStageId = attestationFinStage != null ? attestationFinStage.getId() : null;
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

    public Drh getDrh() {
        return this.drh;
    }

    public void setDrh(Drh drh) {
        this.drh = drh;
        this.drhId = drh != null ? drh.getId() : null;
    }

    public Contrat drh(Drh drh) {
        this.setDrh(drh);
        return this;
    }

    public AssistantGWTE getAssistantGWTECreator() {
        return this.assistantGWTECreator;
    }

    public void setAssistantGWTECreator(AssistantGWTE assistantGWTE) {
        this.assistantGWTECreator = assistantGWTE;
        this.assistantGWTECreatorId = assistantGWTE != null ? assistantGWTE.getId() : null;
    }

    public Contrat assistantGWTECreator(AssistantGWTE assistantGWTE) {
        this.setAssistantGWTECreator(assistantGWTE);
        return this;
    }

    public Candidat getCandidat() {
        return this.candidat;
    }

    public void setCandidat(Candidat candidat) {
        this.candidat = candidat;
        this.candidatId = candidat != null ? candidat.getId() : null;
    }

    public Contrat candidat(Candidat candidat) {
        this.setCandidat(candidat);
        return this;
    }

    public Long getAttestationFinStageId() {
        return this.attestationFinStageId;
    }

    public void setAttestationFinStageId(Long attestationFinStage) {
        this.attestationFinStageId = attestationFinStage;
    }

    public Long getDrhId() {
        return this.drhId;
    }

    public void setDrhId(Long drh) {
        this.drhId = drh;
    }

    public Long getAssistantGWTECreatorId() {
        return this.assistantGWTECreatorId;
    }

    public void setAssistantGWTECreatorId(Long assistantGWTE) {
        this.assistantGWTECreatorId = assistantGWTE;
    }

    public Long getCandidatId() {
        return this.candidatId;
    }

    public void setCandidatId(Long candidat) {
        this.candidatId = candidat;
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
