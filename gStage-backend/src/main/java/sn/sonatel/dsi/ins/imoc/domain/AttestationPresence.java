package sn.sonatel.dsi.ins.imoc.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * A AttestationPresence.
 */
@Entity
@Table(name = "attestation_presence")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AttestationPresence implements Serializable {

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
    @Column(name = "signature_date", nullable = false)
    private LocalDate signatureDate;

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status;

    @Column(name = "comments")
    private String comments;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "attestationPresence")
    @JsonIgnoreProperties(value = { "attestationPresence", "contrat", "attestationFinStage", "user" }, allowSetters = true)
    private Set<Validation> validations = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "attestationFinStage", "validations", "appUser", "candidat" }, allowSetters = true)
    private Contrat contrat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "service", "etatPaiements", "contrats", "demandeStages", "attestationPresences", "candidats", "validations", "roles" },
        allowSetters = true
    )
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "attestationPresences", "contrat", "appUser" }, allowSetters = true)
    private EtatPaiement etatPaiement;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AttestationPresence id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public AttestationPresence reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public AttestationPresence startDate(LocalDate startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public AttestationPresence endDate(LocalDate endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getSignatureDate() {
        return this.signatureDate;
    }

    public AttestationPresence signatureDate(LocalDate signatureDate) {
        this.setSignatureDate(signatureDate);
        return this;
    }

    public void setSignatureDate(LocalDate signatureDate) {
        this.signatureDate = signatureDate;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public AttestationPresence status(Boolean status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getComments() {
        return this.comments;
    }

    public AttestationPresence comments(String comments) {
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
            this.validations.forEach(i -> i.setAttestationPresence(null));
        }
        if (validations != null) {
            validations.forEach(i -> i.setAttestationPresence(this));
        }
        this.validations = validations;
    }

    public AttestationPresence validations(Set<Validation> validations) {
        this.setValidations(validations);
        return this;
    }

    public AttestationPresence addValidations(Validation validation) {
        this.validations.add(validation);
        validation.setAttestationPresence(this);
        return this;
    }

    public AttestationPresence removeValidations(Validation validation) {
        this.validations.remove(validation);
        validation.setAttestationPresence(null);
        return this;
    }

    public Contrat getContrat() {
        return this.contrat;
    }

    public void setContrat(Contrat contrat) {
        this.contrat = contrat;
    }

    public AttestationPresence contrat(Contrat contrat) {
        this.setContrat(contrat);
        return this;
    }

    public AppUser getAppUser() {
        return this.appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public AttestationPresence appUser(AppUser appUser) {
        this.setAppUser(appUser);
        return this;
    }

    public EtatPaiement getEtatPaiement() {
        return this.etatPaiement;
    }

    public void setEtatPaiement(EtatPaiement etatPaiement) {
        this.etatPaiement = etatPaiement;
    }

    public AttestationPresence etatPaiement(EtatPaiement etatPaiement) {
        this.setEtatPaiement(etatPaiement);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AttestationPresence)) {
            return false;
        }
        return getId() != null && getId().equals(((AttestationPresence) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AttestationPresence{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", signatureDate='" + getSignatureDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", comments='" + getComments() + "'" +
            "}";
    }
}
