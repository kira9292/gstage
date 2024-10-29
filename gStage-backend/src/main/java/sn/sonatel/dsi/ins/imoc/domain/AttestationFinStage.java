package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * A AttestationFinStage.
 */
@Entity
@Table(name = "attestation_fin_stage")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AttestationFinStage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "reference", nullable = false, unique = true)
    private String reference;

    @NotNull
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "signature_date")
    private LocalDate signatureDate;

    @Column(name = "comments")
    private String comments;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "attestationFinStage")
    @JsonIgnoreProperties(value = { "attestationPresence", "contrat", "attestationFinStage", "user" }, allowSetters = true)
    private Set<Validation> validations = new HashSet<>();

    @JsonIgnoreProperties(value = { "attestationFinStage", "validations", "appUser", "candidat" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "attestationFinStage")
    private Contrat contrat;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AttestationFinStage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public AttestationFinStage reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDate getIssueDate() {
        return this.issueDate;
    }

    public AttestationFinStage issueDate(LocalDate issueDate) {
        this.setIssueDate(issueDate);
        return this;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getSignatureDate() {
        return this.signatureDate;
    }

    public AttestationFinStage signatureDate(LocalDate signatureDate) {
        this.setSignatureDate(signatureDate);
        return this;
    }

    public void setSignatureDate(LocalDate signatureDate) {
        this.signatureDate = signatureDate;
    }

    public String getComments() {
        return this.comments;
    }

    public AttestationFinStage comments(String comments) {
        this.setComments(comments);
        return this;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Set<Validation> getValidations() {
        return this.validations;
    }

    public void setValidations(Set<Validation> validations) {
        if (this.validations != null) {
            this.validations.forEach(i -> i.setAttestationFinStage(null));
        }
        if (validations != null) {
            validations.forEach(i -> i.setAttestationFinStage(this));
        }
        this.validations = validations;
    }

    public AttestationFinStage validations(Set<Validation> validations) {
        this.setValidations(validations);
        return this;
    }

    public AttestationFinStage addValidations(Validation validation) {
        this.validations.add(validation);
        validation.setAttestationFinStage(this);
        return this;
    }

    public AttestationFinStage removeValidations(Validation validation) {
        this.validations.remove(validation);
        validation.setAttestationFinStage(null);
        return this;
    }

    public Contrat getContrat() {
        return this.contrat;
    }

    public void setContrat(Contrat contrat) {
        if (this.contrat != null) {
            this.contrat.setAttestationFinStage(null);
        }
        if (contrat != null) {
            contrat.setAttestationFinStage(this);
        }
        this.contrat = contrat;
    }

    public AttestationFinStage contrat(Contrat contrat) {
        this.setContrat(contrat);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AttestationFinStage)) {
            return false;
        }
        return getId() != null && getId().equals(((AttestationFinStage) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AttestationFinStage{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", issueDate='" + getIssueDate() + "'" +
            ", signatureDate='" + getSignatureDate() + "'" +
            ", comments='" + getComments() + "'" +
            "}";
    }
}
